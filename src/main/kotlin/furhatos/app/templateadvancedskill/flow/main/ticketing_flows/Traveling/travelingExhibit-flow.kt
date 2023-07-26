package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Traveling

import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.CartItem
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.FinalPurchase.Checkout
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Screening.Screenings


import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Planetarium.PurchaseAddOns
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Planetarium.todayShows
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.customerCart


import furhatos.flow.kotlin.*
import furhatos.nlu.common.Number
import furhatos.nlu.intent
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

val travelingExhibitAddOn: State = state {
    var admissionStage = 0
    var showExhibit = false
    onEntry {
        if(todayExhibit.size <= 1){
            showExhibit = furhat.askYN("For a limited time we are also hosting " + exhibitNames(todayExhibit) +
                    " Would you like to purchase tickets to this exhibit? ")!!
            if (showExhibit){
                val confirmedAdd = furhat.askYN("Just to confirm. You want to add " + todayExhibit[0].exhibitName  + "at a price of $" + todayExhibit[0].price.toString() + "per ticket?")

                if(confirmedAdd != false){
                    val adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
                    val childCount = furhat.askFor<Number>("How many children ages 3-17?")
                    addToCart(todayExhibit[0].exhibitName, adultCount.toString().toInt(), childCount.toString().toInt(), todayExhibit[0].price)
                    furhat.say("I have added " + adultCount + " adult tickets and " + childCount + " child tickets for " + todayExhibit[0].exhibitName + " to your purchase!")
                    if(furhat.askYN("Is there anything else you woud like to add to your purchase") != true){
                        goto(Checkout)
                    }
                }
                else{
                    furhat.ask("What show would you like to purchase tickets for?")
                }
            }
        }else{
            showExhibit = furhat.askYN("For a limited time we are also hosting " + exhibitNames(todayExhibit) +
                    " Would you like to purchase tickets to any of these exhibits? ")!!
        }


    }
    onResponse(todayExhibit) {
        val showIntent = it.intent as Exhibit

        val confirmedAdd = furhat.askYN("Just to confirm. You want to add " + showIntent.exhibitName  + "at a price of $" + showIntent.price.toString() + "per ticket?")

        if(confirmedAdd != false){
            val adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
            val childCount = furhat.askFor<Number>("How many children ages 3-17?")
            addToCart(showIntent.exhibitName, adultCount.toString().toInt(), childCount.toString().toInt(), showIntent.price)
            furhat.say("I have added " + adultCount + " adult tickets and " + childCount + " child tickets for " + showIntent.exhibitName + " to your purchase!")
            if(furhat.askYN("Is there anything else you woud like to add to your purchase") != true){
                goto(Checkout)
            }
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

fun addToCart(name: String, adultCount: Int, childCount: Int,  price: Double){
    if(adultCount.toString().toInt()>0){
        customerCart.add(CartItem("$name (Adult)",adultCount, price))
    }
    if(childCount.toString().toInt()>0){
        customerCart.add(CartItem("$name (Child)",childCount, price))
    }
}

fun exhibitNames(exhibits: List<Exhibit>): String{
    val numOfExhibits = exhibits.size
    val outputString = ""
    if(numOfExhibits == 1){
        return exhibits[0].exhibitName
    }
    if(numOfExhibits == 2){
        return exhibits[0].exhibitName + " and " + exhibits[1].exhibitName
    }
    for (i in 0..numOfExhibits-2){
        outputString + exhibits[i].exhibitName + ", "
    }
    return outputString + "and" + exhibits[numOfExhibits-1]
}


/** Run this to test the intents of this state from the command line. **/
fun main(args: Array<String>) {
    while (true) {
        val utterance = readLine()
        val results = travelingExhibitAddOn.getIntentClassifier(lang = Language.ENGLISH_US).classify(utterance!!)
        if (results.isEmpty()) {
            println("No match")
        } else {
            results.forEach {
                println("Matched ${it.intents} with ${it.conf} confidence")
            }
        }
    }
}
