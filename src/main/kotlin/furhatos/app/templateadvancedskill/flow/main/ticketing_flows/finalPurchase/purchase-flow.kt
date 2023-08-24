package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.finalPurchase

import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.CartItem
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.customerCart



import furhatos.flow.kotlin.*


import java.util.Scanner

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

            val read = Scanner(System.`in`)

            furhat.say("Please enter your first name")
            print("First Name: ")
            val customerFName = read.nextLine()

            furhat.say("Please enter your last name")
            print("Last Name: ")
            val customerLName = read.nextLine()

            read.close()

            val customerName = "$customerFName $customerLName"

            DBU.updateDB(customerName,2,"SKY", "Jane Goodall", "SGWR", findTotal(customerCart))
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
fun findTotal(cart: List<CartItem>): Double {
    return cart.sumByDouble { it.price }
}

fun readPurchase(cart: List<CartItem>): String {
    val outputString = cart.joinToString(separator = "\n") { "${it.quantity} ${it.item}" }
    return "$outputString For a total of $${findTotal(cart)}"
}


