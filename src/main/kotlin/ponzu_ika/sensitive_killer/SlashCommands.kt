package ponzu_ika.sensitive_killer

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

class SlashCommands :ListenerAdapter(){

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        if(event.name == "oc") {
            event.replyEmbeds(
                voltageCalc(
                    event.getOption("voltage")!!.asInt,
                    event.getOption("time")!!.asDouble,
                    if (event.getOption("istick") == null) false else event.getOption("istick")!!.asBoolean
                )
            ).queue()
        }
    }

    fun voltageCalc(inputVoltage:Int, inputTime:Double, isTick:Boolean = false): MessageEmbed {
        val eb = EmbedBuilder()

        val time = if(isTick) inputTime/20 else inputTime

        eb.setColor(Color.CYAN)

        eb.setDescription("TotalEnergy: ${(time*inputVoltage*20).roundToInt()}EU")

        val tier: ArrayList<String> = arrayListOf("ULV","LV","MV","HV","EV","IV","LuV","ZPM","UV","UHV","UEV","UIV","UXV","OpV","MAX")
        var loop = 1

        for (i in 1..tier.size) {
            val voltage = 4.0.pow(i).toLong()*2
            if(inputVoltage <= voltage && voltage < inputVoltage*4)
                eb.setTitle("Voltage: ${tier[i-1]}(${inputVoltage}EU/t), Time: ${time}sec")
            if (inputVoltage <= voltage && 0.5<=time/loop*20) {
                eb.addField("${tier[i-1]} (${if(i < tier.size) voltage else Int.MAX_VALUE}EU/t)","${round(time/loop*100)/100}s (${(time/loop*20).roundToInt()}t)",false)
                loop *= 2
            }
        }

        return eb.build()
    }
}