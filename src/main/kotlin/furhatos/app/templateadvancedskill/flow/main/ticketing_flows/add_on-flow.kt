package furhatos.app.templateadvancedskill.flow.main.ticketing_flows

import furhatos.nlu.SimpleIntent


import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Number
import furhatos.nlu.common.Yes
import furhatos.util.Language


/**
 * Tip!
 *
 * This subflow has all relevant resources including intents, entities and phrases together in its own package
 * without any dependencies to other skills files. This allows for easy reuse of the subflow in other skills.
 */

/**
 * Example of a subflow
 * Flow will ask about how to user feels today and return.
 **/

val PurchaseAddOns: State = state {
    var admissionStage = 0

    onEntry {
        furhat.ask("At the science center today we are showing " + showNames(todayShows) +
                "Would you like to purchase tickets to any of these shows? ")

    }
    onResponse<Yes> {
        furhat.ask("What show would you like to purchase tickets for?")
    }
    onResponse<Shows> {
        val showIntent = it.intent as Shows
        val confirmedAdd = furhat.askYN("Just to confirm. You want to add " + showIntent.showName + "at " + showIntent.time + "at a price pf " + showIntent.price.toString() + "per ticket?")

        if(confirmedAdd != false){
            val adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
            val childCount = furhat.askFor<Number>("How many children ages 3-17?")
            furhat.say("I have added " + adultCount + " adult tickets " + childCount + "child tickets for the " + showIntent.time + " showing of " + showIntent.showName + " to your purchase!")
        }
        else{
            furhat.ask("What show would you like to purchase tickets for?")
        }
    }
    onResponse<No> {
        furhat.ask("OK. Our giant screen theater is currently showing Jane Goodall Reasons for Hope. Would you like to purchase tickets for this film at 11:00am or 1:00pm?")
    }


    onResponse {
        furhat.say("I'm not sure I understood you.")
        delay(400)
        reentry()
    }

    onNoResponse {
        terminate()
    }
}

fun showNames(shows: List<Shows>): String{
    val numOfShows = shows.size
    val outputString = ""
    if(numOfShows == 1){
        return shows[0].showName
    }
    if(numOfShows == 2){
        return shows[0].showName + " and " + shows[1].showName
    }
    for (i in 0..numOfShows-2){
        outputString + shows[i].showName + " and "
    }
    return outputString + shows[numOfShows-1]
}

/** Run this to test the intents of this state from the command line. **/
fun main(args: Array<String>) {
    while (true) {
        val utterance = readLine()
        val results = PurchaseAddOns.getIntentClassifier(lang = Language.ENGLISH_US).classify(utterance!!)
        if (results.isEmpty()) {
            println("No match")
        } else {
            results.forEach {
                println("Matched ${it.intents} with ${it.conf} confidence")
            }
        }
    }
}
