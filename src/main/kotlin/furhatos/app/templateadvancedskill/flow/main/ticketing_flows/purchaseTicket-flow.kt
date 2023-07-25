package furhatos.app.templateadvancedskill.flow.main.ticketing_flows


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
val PurchaseTicket: State = state {
    var admissionStage = 0
    onEntry {
        furhat.say("Thank you for your interest in exploring the science center today!")
        val adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
        val childCount = furhat.askFor<Number>("How many children ages 3-17?")
        println("Total Adults: " + adultCount + "Child Count: " + childCount)

        furhat.ask("Thank you! During your last visit you enjoyed a planetarium show! Would you like to enhance your experience with any add-on tickets? There are two planetarium shows available today at 12:00pm and 2:00pm. ")
    }
    onResponse<Yes> {
        goto(PurchaseAddOns)
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
