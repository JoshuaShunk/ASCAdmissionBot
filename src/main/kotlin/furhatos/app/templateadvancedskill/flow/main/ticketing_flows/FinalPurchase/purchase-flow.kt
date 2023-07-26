package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.FinalPurchase

import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.CartItem
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.PurchaseTicket
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.customerCart


import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes


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
val Checkout: State = state {

    onEntry {
        println("All Purchases Complete. Ready for Checkout")
        val confirmedPurchase = furhat.askYN("Confirm purchase for " + readPurchase(customerCart))
        if(confirmedPurchase != false){
            furhat.say("Thank you for visiting the Arizona Science Center and I hope you enjoy your visit. Tap or insert your card to purchase")
        }

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
fun readPurchase(cart: List<CartItem>): String{
    val itemInPurchase = cart.size
    var outputString = ""
    var totalPrice = 0.00
    for (i in 0 until itemInPurchase){
        outputString = outputString + cart[i].quantity + " " + cart[i].item + " " + "\n"
        totalPrice += cart[i].price
    }
    outputString = outputString + " For a total of $" + totalPrice
    return outputString
}


