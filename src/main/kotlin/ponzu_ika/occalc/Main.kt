package ponzu_ika.occalc

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import java.io.File


class Main : ListenerAdapter() {
    private lateinit var guild: Guild
    private lateinit var jda: JDA
    fun main(token: String, guildId: String) {
        //JDAのセットアップ。それ以上でも以下でもない。
        jda = JDABuilder.createDefault(
            token,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.GUILD_MESSAGES
        )

            .addEventListeners(this, SlashCommands())
            .build()

        jda.awaitReady()

        //コマンド実装用。辛いので投げた
        guild = jda.getGuildById(guildId)!!

        guild.updateCommands()
            .addCommands(
                Commands.slash("oc", "オーバークロックの計算").addOption(OptionType.INTEGER, "voltage", "電圧", true)
                    .addOption(OptionType.NUMBER, "time", "処理時間", true)
                    .addOption(OptionType.BOOLEAN, "istick", "処理時間をtickとして処理", false,),
                Commands.slash("forge", "TFCの鍛冶計算")
                    .addOption(OptionType.INTEGER, "hammer", "赤い矢印が指す値", true)
                    .addOption(OptionType.STRING, "sequence", "最後に打つ奴を入力(例: -3,7,-15)", true)
            ).queue()
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