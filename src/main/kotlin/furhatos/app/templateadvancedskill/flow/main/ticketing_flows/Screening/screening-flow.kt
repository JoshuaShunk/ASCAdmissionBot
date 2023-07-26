package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Screening

import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.CartItem
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Planetarium.PurchaseAddOns
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Planetarium.todayShows
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Traveling.travelingExhibitAddOn
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.customerCart


import furhatos.flow.kotlin.*
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

val ScreeningAddOn: State = state {
    var admissionStage = 0

    onEntry {
        val showScreening = furhat.askYN("At the science center today we are showing " + screenNames(todayScreenings) +
                " Would you like to purchase tickets to any of these shows? ")
        if (showScreening != false){
            furhat.ask("What show would you like to purchase tickets for?")
        }else{
            goto(travelingExhibitAddOn)
        }
    }
    onResponse(todayShows) {
        val showIntent = it.intent as Screenings
        if (showIntent.time.size>1){
            furhat.say("There are " + showIntent.time.size + " showings of " + showIntent.screenName + " at " + screenTimes(showIntent) + ".")
            println(furhat.ask("What Showing would you like to go to?"))
        }
        val confirmedAdd = furhat.askYN("Just to confirm. You want to add " + showIntent.screenName + " at " + showIntent.time + "at a price of $" + showIntent.price.toString() + "per ticket?")

        if(confirmedAdd != false){
            val adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
            val childCount = furhat.askFor<Number>("How many children ages 3-17?")
            if(adultCount.toString().toInt()>0){
                customerCart.add(CartItem(showIntent.screenName + ("Adult"),adultCount.toString().toInt(), showIntent.price.toString().toDouble()))
            }
            if(childCount.toString().toInt()>0){
                customerCart.add(CartItem(showIntent.screenName + ("Child"),childCount.toString().toInt(), showIntent.price.toString().toDouble()))
            }
            furhat.say("I have added " + adultCount + " adult tickets and " + childCount + " child tickets for the " + showIntent.time[0] + " showing of " + showIntent.screenName + " to your purchase!")
        }
        else{
            furhat.ask("What show would you like to purchase tickets for?")
        }
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

fun screenNames(shows: List<Screenings>): String{
    val numOfScreenings = shows.size
    val outputString = ""
    if(numOfScreenings == 1){
        return shows[0].screenName
    }
    if(numOfScreenings == 2){
        return shows[0].screenName + " and " + shows[1].screenName
    }
    for (i in 0..numOfScreenings-2){
        outputString + shows[i].screenName + ", "
    }
    return outputString + "and" + shows[numOfScreenings-1]
}

fun screenTimes(show: Screenings): String{
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
fun screenList(show: Screenings): List<String>{
    return show.time


}
/** Run this to test the intents of this state from the command line. **/
fun main(args: Array<String>) {
    while (true) {
        val utterance = readLine()
        val results = ScreeningAddOn.getIntentClassifier(lang = Language.ENGLISH_US).classify(utterance!!)
        if (results.isEmpty()) {
            println("No match")
        } else {
            results.forEach {
                println("Matched ${it.intents} with ${it.conf} confidence")
            }
        }
    }
}
