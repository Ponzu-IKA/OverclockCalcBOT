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
    lateinit var guild: Guild
    lateinit var jda: JDA
    fun main(token: String, guild_id: String) {
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
        guild = jda.getGuildById(guild_id)!!

        guild.updateCommands()
            .addCommands(
                Commands.slash("oc", "オーバークロックの計算").addOption(OptionType.INTEGER, "voltage", "電圧", true)
                    .addOption(OptionType.NUMBER, "time", "処理時間", true)
                    .addOption(OptionType.BOOLEAN, "istick", "処理時間をtickとして処理", false,)
            )
            .queue()
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