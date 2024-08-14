package ponzu_ika.occalc

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import okhttp3.internal.wait
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
        } else if(event.name == "forge") {
            val hammerValue = event.getOption("hammer")!!.asInt
            val sequence =  event.getOption("sequence")!!.asString.split(",").map(String::toInt)
            event.reply(
                hammerCalc(
                    hammerValue,
                    sequence,
                    event.messageChannel
                )
            ).queue()
        }
    }

    private fun hammerCalc(hammerValue:Int,inputSequence:List<Int>, channel:MessageChannel): String {
        //左上を0右下を7とした配列
        val hammerPower = mutableListOf(-3,-6,-9,-15,2,7,13,16)

        //checkvalue
        inputSequence.forEach{
            if(!hammerPower.contains(it))
                return "入力されたシーケンスが不正です。\n${it}は存在しません。$inputSequence"
        }

        //CYAN AQUA GREEN YELLOW BLUE PURPLE ORANGE RED
        val hammerOrder = mutableListOf(0,0,0,0,0,0,0,0)
        val hammerValueCalc = hammerValue - inputSequence.sum()

        val hammer = mutableListOf(16,13,7,2)

        var hammerPowerList: MutableList<Int>
        val subsPowerList = mutableListOf(0,0,0,0)

        var sumValue:Int

        var looped = 0
        do {
            hammerPowerList = mutableListOf(0,0,0,0)
            var value = hammerValueCalc
            for (i in 0..3) {
                println(hammer[i])
                println(value % hammer[i])
                println(value / hammer[i])

                println()

                for (j in 0..<value / hammer[i] - subsPowerList[i]) {
                    hammerPowerList[i] += 1
                    value -= hammer[i]
                }
            }

            println(hammerPowerList)


            for (i in 0..2) {
                if (0 <= hammerPowerList[i] - subsPowerList[i]) {
                    if(hammerPowerList[i+1]<=hammerPowerList[i]){
                        subsPowerList[i] += 1
                        break
                    }
                }
            }
            sumValue = 0

            for (i in 0..3)
                sumValue += hammerPowerList[i]*hammer[i]
            println("sumValue: $sumValue")
            println("sum: ${inputSequence.sum()}")

            looped++
            if(looped > 10) {
                return "処理できませんでした,入力値:$hammerValue, 仕上げ:$inputSequence"
            }
        } while (sumValue != hammerValueCalc)

        //-3,-6,-9,-15,2,7,13,16
        val emojiList = listOf(
            "<:3_:1272874569492992000>",
            "<:6_:1272874571476893747>",
            "<:9_:1272874575667134527>",
            "<:15:1272874579643338783>",

            "<:2_:1272874568041627678>",
            "<:7_:1272874573066539101>",
            "<:13:1272874577395191848>",
            "<:16:1272874581262340186>")

        val outputList = mutableListOf<String>()

        for(i in 0 until hammer.size) {
            for (j in 0 until hammerPowerList[i])
                outputList.add(emojiList[hammer.size-i+3])
        }

        inputSequence.map{
            for(i in 0..7)
                if(it == hammerPower[i])
                    outputList.add(emojiList[i])
        }

        val strL = mutableListOf<String>()

        strL.add("入力: $hammerValue, $inputSequence")

        strL.add("仕上げ作業\n$inputSequence")
        println(inputSequence)


        channel.sendMessage(strL.joinToString("\n")).queue()

        println(outputList.joinToString(""))

        return outputList.joinToString("")
    }

    private fun voltageCalc(inputVoltage:Int, inputTime:Double, isTick:Boolean = false): MessageEmbed {
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