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
        val memberStatus = furhat.say("Please scan your science center member pass?")
        Thread.sleep(3000)
        goto(PurchaseTicket)

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


