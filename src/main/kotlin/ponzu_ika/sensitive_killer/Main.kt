package ponzu_ika.sensitive_killer

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.function.UnaryOperator


class Main : ListenerAdapter() {
    private lateinit var receivedMessage:String
    lateinit var guild:Guild
    lateinit var jda: JDA
    fun main(token:String, guild_id:String) {
        //JDAのセットアップ。それ以上でも以下でもない。
         jda = JDABuilder.createDefault(token,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.GUILD_MESSAGES)

            .addEventListeners(this,SlashCommands())
            .build()

        jda.awaitReady()

        //コマンド実装用。辛いので投げた
        guild = jda.getGuildById(guild_id)!!

        guild.updateCommands()
            .addCommands(Commands.slash("toggle_channel","channel毎の有効無効の切り替え"))
            .queue()
    }

    //全てのメッセージに反応。サーバー指定なんてない
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val guildid = event.message.guildId!!
        val channelid = event.message.channelId
        if(!File(guildid).isFile)
            File(guildid).createNewFile()
        val channelList = reader(guildid)
        //メッセージがこのBOTから飛んでいた場合は反応しない
        if(event.author.id == jda.selfUser.id) return
        if(channelList.contains(channelid)) return

        //受け取ったメッセージをreceivedMessageに代入
        receivedMessage = event.message.contentDisplay
        //SensitiveKillerからreturnされた文章をretrunedに代入
        val retruned = SensitiveKiller().sensitiveKiller(receivedMessage)
        val returnedStr = retruned.first
        val returnedInt = retruned.second

        val user_id = event.author.id
        val path = "user/$user_id"
        if(!File(path).isFile)
            File(path).createNewFile()


        var inrandoReader = File(path).readText().toIntOrNull()
        val inrandoWriter = writer(path)

        if(inrandoReader==null)
            inrandoReader = 0

        inrandoWriter.append("${inrandoReader + returnedInt}")
        inrandoWriter.flush()
        inrandoWriter.close()

        //アスタリスク(太字に用いている)があれば返信する。無ければそのまま終了
        if (returnedStr.contains("*")){
            event.message.addReaction(Emoji.fromUnicode("\uD83D\uDE93")).queue()
            event.message.reply("$returnedStr\n${returnedInt}単語が禁制文字です。").queue {message ->
                message.delete().queueAfter(5,TimeUnit.SECONDS)
            }
        }
    }

}
//"1252613009076125738"
//println("引数1: tokenファイル, 引数2: GUILD_ID")
fun main(args: Array<String>){
    if(args.size != 2) {
        println("引数が二つより多い、若しくは少ないです。")
        return
    }
    val bot = Main()

    bot.main(File(args[0]).readText(),args[1])
}