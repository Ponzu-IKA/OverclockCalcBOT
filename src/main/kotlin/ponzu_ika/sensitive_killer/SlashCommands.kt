package ponzu_ika.sensitive_killer

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.File

class SlashCommands :ListenerAdapter(){

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val guildid = event.guild?.id!!
        if(event.name == "toggle_channel") {
            if(!File(guildid).isFile)
                File(guildid).createNewFile()
            val channelList = reader(guildid)
            val channelListWriter = writer(guildid)

            println()
            val id = event.channelId
            val name = event.channel

            if(id==null) {
                event.reply("ChannelIDの取得に失敗しました").queue()
                return
            }

            if(channelList.contains(id)) {
                channelList.remove(id)
                println("$name を除外リストから削除")
                event.reply("$name で実行可能にしました。").queue()
            } else {
                channelList.add(id)
                println("$name を除外リストに追加")
                event.reply("$name で実行不能にしました。").queue()
            }

            channelList.forEach {
                channelListWriter.appendLine(it)
            }
            channelListWriter.flush()
            channelListWriter.close()
        }

        if(event.name == "ranking_upper") {

        }

        if(event.name == "ranking_lower") {

        }
    }

    fun ranking() {
        val path = "user/"
        val list = File(path).list()!!
        var username = ""
        list.forEach { userid ->
            username = Main().jda.getUserById(userid).toString()
        }
    }
}