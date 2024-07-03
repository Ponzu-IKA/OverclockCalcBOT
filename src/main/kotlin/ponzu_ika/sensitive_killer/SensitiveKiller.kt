package ponzu_ika.sensitive_killer

import com.atilika.kuromoji.ipadic.Tokenizer
import com.ibm.icu.text.Transliterator
import java.io.File



class SensitiveKiller {
    private val ngWords = File("words/SensitiveWords.txt").readLines()
    private val wordException = File("words/WordException.txt").readLines().map { it.split(",") }
    private val tokenizer = Tokenizer()
    private val katakanaToLatin = Transliterator.getInstance("Katakana-Latin")
    private val kanaToKatakana = Transliterator.getInstance("Hiragana-Katakana")
    fun sensitiveKiller(input:String): Pair<String, Int> {
        println(wordException)
        println(ngWords)
        println("入力: $input")
        var out: String
        var wordExcepted = ""
        wordException.forEach {word ->
             wordExcepted = input.replace(Regex(word[0]),word[1]).uppercase()
        }

        //読みがカタカナのためそのまま流用。しかし、読みがない場合があるためその際は文字そのものを取得
        //恐らくここの文字取得でひらがな/カタカナ以外が取得されるとエラーを吐く。要するにバグの温床
        //Regexで削除してしまえば解決できるがRegexの多用は重そう
        val res = tokenizer.tokenize(wordExcepted).joinToString {
            if (it.reading == "*") it.surface else it.reading
        }.replace(Regex("""\s|,"""),"")
/*
        for (token:Token in tokenizer.tokenize(input)) {
            res += token.surface.replace(Regex("""\n"""),"")
        }*/
        println("カナ: $res")
        //ひらがなをカタカナに変換。主にsurfaceで取得されたデータ用
        out = kanaToKatakana.transliterate(res)
        println(out)

        //ここで句読点など余計なものを消去
        out = out.replace(Regex("""[^ア-ンA-Z0-9ー-]"""),"")
        println("編集済みカナ: $out")

        //カタカナをアルファベットに変換
        out = katakanaToLatin.transliterate(out).uppercase()
        println("英大文字: $out")

        ngWords.forEach { word ->
            val uppercaseWord = word.uppercase()
            out =
                    out.replace(Regex("N'"),"NN")
                        .replace(Regex("GYI"),"GI")
                        .replace(Regex("DZU"),"DU")
                        .replace(Regex("Ō"),"O-")
                        .replace(Regex("Ā"),"A-")
                        .replace(Regex(
                    //regex内でRegexしていて大変気持ちが悪い。
                    //やっていることは単純で、wordの表記ゆれを押さえているだけ。
                            uppercaseWord.replace(Regex("""CHI|TI"""), "(CHI|TI)")
                                .replace(Regex("""N|NN"""), "(N|NN)")
                                .replace(Regex("""CO|KO"""), "(CO|KO)")
                                .replace(Regex("""RA|LLA"""), "(RA|LLA)")
                                .replace(Regex("""RI|LI"""),"(RI|LI)")
                                .replace(Regex("""HU|FU"""),"(HU|FU)")
                                .replace(Regex("""SI|SHI"""),"(SHI|SI)")
                                .replace(Regex("""JI|ZI"""),"(JI|ZI)")
                                .replace(Regex("""RO|LO"""),"(RO|LO)")
                                .replace(Regex("""SHO|SYO"""),("(SHO|SYO)"))
                        ), " _**$uppercaseWord**_ ")
        }

        val hit = out.filter { it == '_' }.count()/2

        println("あれば太字: $out")

        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // o see how IntelliJ IDEA suggests fixing it.

        return Pair(out,hit)
    }
}
/* debug酔う
fun main() {
    SensitiveKiller().sensitiveKiller("我慢、このカフェラテは(ry ")
}*/