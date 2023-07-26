package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Planetarium

import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.CartItem
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Screening.ScreeningAddOn
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.customerCart


import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Number
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
        val purchaseShow = furhat.askYN("At the science center today we are showing " + showNames(todayShows) +
                " Would you like to purchase tickets to any of these shows? ")
        if (purchaseShow != false){
            furhat.ask("What show would you like to purchase tickets for?")
        }
        else{
            goto(ScreeningAddOn)
        }

    }

    onResponse(todayShows) {
        val showIntent = it.intent as Shows
        if (showIntent.time.size>1){
            furhat.say("There are " + showIntent.time.size + " showings of " + showIntent.showName + " at " + showTimes(showIntent) + ".")
            println(furhat.ask("What Showing would you like to go to?"))
        }
        val confirmedAdd = furhat.askYN("Just to confirm. You want to add " + showIntent.showName + " at " + showIntent.time + "at a price of $" + showIntent.price.toString() + "per ticket?")

        if(confirmedAdd != false){
            val adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
            val childCount = furhat.askFor<Number>("How many children ages 3-17?")
            if(adultCount.toString().toInt()>0){
                customerCart.add(CartItem(showIntent.showName + ("Adult"),adultCount.toString().toInt(), showIntent.price.toString().toDouble()))
            }
            if(childCount.toString().toInt()>0){
                customerCart.add(CartItem(showIntent.showName + ("Child"),childCount.toString().toInt(), showIntent.price.toString().toDouble()))
            }
            furhat.say("I have added " + adultCount + " adult tickets and " + childCount + " child tickets for the " + showIntent.time[0] + " showing of " + showIntent.showName + " to your purchase!")
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
        outputString + shows[i].showName + ", "
    }
    return outputString + "and" + shows[numOfShows-1]
}

fun showTimes(show: Shows): String{
    val numOfTimes = show.time.size
    val outputString = ""
    if(numOfTimes == 1){
        return show.time[0]
    }
    if(numOfTimes == 2){
        return show.time[0] + " and " + show.time[1]
    }
    for (i in 0..numOfTimes-2){
        outputString + show.time[i] + ", "
    }
    return outputString + " and " + show.time[numOfTimes-1]
}
fun showingList(show: Shows): List<String>{
    return show.time

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
