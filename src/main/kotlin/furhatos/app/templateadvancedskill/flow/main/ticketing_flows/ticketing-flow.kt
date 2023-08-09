package furhatos.app.templateadvancedskill.flow.main.ticketing_flows

import furhatos.flow.kotlin.*


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
val Ticketing: State = state {
    var admissionStage = 0

    onEntry {
        val memberStatus = furhat.askYN("Are you a member of the Arizona Science Center?",10000)
        if (memberStatus != false){
            admissionStage++
            furhat.ask("Would you like to collect tickets bought online or buy new general admission tickets?")
        }
        else{
            furhat.say("Sorry, I am only here to help memebers purchase tickets. Please see a sales representative at the counter to purchase general admission tickets")
            delay(400)
            terminate()
        }
    }

    onResponse("new tickets", "new general admission tickets") {
        admissionStage++
        furhat.ask("Great! Are these tickets for today or a future visit?",10000)
    }
    onResponse("Today") {
        goto(PurchaseTicket)

    }

    onResponse("Bought online") {
        admissionStage++
    }

    onResponse {
        furhat.say("I'm not sure I understood you")
        delay(400)
        reentry()
    }

    onNoResponse {
        terminate()
    }
}


