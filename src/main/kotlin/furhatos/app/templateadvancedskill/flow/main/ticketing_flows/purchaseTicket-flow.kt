package furhatos.app.templateadvancedskill.flow.main.ticketing_flows


import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.planetarium.PurchaseAddOns
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.screening.PurchaseScreenings
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

val PurchaseTicket: State = state {

    onEntry {
        furhat.say("Thank you for your interest in exploring the science center today!")
        val adultCount: Number? = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
        val childCount: Number? = furhat.askFor<Number>("How many children ages 3-17?")
        println("Total Adults: " + adultCount + "Child Count: " + childCount)
        customerCart.add(CartItem("Adult Ticket(s)", adultCount.toString().toInt(), 20.00))
        customerCart.add(CartItem("Child Ticket(s)", childCount.toString().toInt(), 15.00))
        val addons = furhat.askYN("Thank you! During your last visit you enjoyed a planetarium show! Would you like to enhance your experience with any add-on tickets?")

        if(addons != false){
            goto(PurchaseAddOns)
        }
        else{
            goto(PurchaseScreenings)
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

/** Run this to test the intents of this state from the command line. **/
fun main(args: Array<String>) {
    while (true) {
        val utterance = readLine()
        val results = PurchaseTicket.getIntentClassifier(lang = Language.ENGLISH_US).classify(utterance!!)
        if (results.isEmpty()) {
            println("No match")
        } else {
            results.forEach {
                println("Matched ${it.intents} with ${it.conf} confidence")
            }
        }
    }
}
