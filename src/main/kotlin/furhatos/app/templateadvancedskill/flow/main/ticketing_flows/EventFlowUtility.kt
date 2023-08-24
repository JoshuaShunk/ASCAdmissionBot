package furhatos.app.templateadvancedskill.flow.main.ticketing_flows

import furhatos.app.templateadvancedskill.flow.main.getCurrentTime
import furhatos.app.templateadvancedskill.flow.main.normalizeTimeInput
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.planetarium.Event
import furhatos.app.templateadvancedskill.flow.main.to12HourFormat
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Number

fun filterPastTimes(event: Event): List<String> {
    val (currentHour, currentMinute) = getCurrentTime()
    return event.time.filter {
        val (eventHour, eventMinute) = it.split(":").map(String::toInt)
        eventHour > currentHour || (eventHour == currentHour && eventMinute > currentMinute)
    }
}

fun eventTimes(event: Event): String {
    // Convert all times to 12-hour format
    val timesIn12HrFormat = event.time.map { to12HourFormat(it) }

    // Adjust the joining method for traditional English lists
    return when (timesIn12HrFormat.size) {
        1 -> timesIn12HrFormat.first()
        2 -> timesIn12HrFormat.joinToString(separator = " and ")
        else -> timesIn12HrFormat.dropLast(1).joinToString(", ") + ", and " + timesIn12HrFormat.last()
    }
}

fun eventNames(events: List<Event>): String {
    return events.joinToString(separator = " and ") { it.name }
}

fun confirmEventPurchase(furhat: Furhat, selectedEvent: Event?, selectedTime: String?) {
    val convertedTime = selectedTime?.let { to12HourFormat(it) }
    val confirmedAdd = furhat.askYN("Just to confirm. You want to add ${selectedEvent?.name} at $convertedTime at a price of $${selectedEvent?.price} per ticket?")

    if (confirmedAdd == true) {


        if(furhat.askYN("Do you want to add tickets for your whole party?") == true){
            furhat.say("Adding ${customerCart[0].quantity} adult tickets and ${customerCart[1].quantity} child tickets for the $convertedTime showing of ${selectedEvent?.name} to your purchase!")
        }else{
            var adultCount: Number? = null
            var childCount: Number? = null

            while (adultCount == null) {
                adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
                if (adultCount == null) {
                    furhat.say("Please enter a valid number for adult tickets.")
                }
            }

            while (childCount == null) {
                childCount = furhat.askFor<Number>("How many children ages 3-17?")
                if (childCount == null) {
                    furhat.say("Please enter a valid number for child tickets.")
                }
            }

            furhat.say("Adding $adultCount adult tickets and $childCount child tickets for the $convertedTime showing of ${selectedEvent?.name} to your purchase!")
        }

    } else {
        furhat.ask("What event would you like to purchase tickets for?")
    }
}

fun handleEventSelection(furhat: Furhat, selectedEvent: Event): Pair<Boolean, String?> {
    val selectedTimeLocal: String?
    if (selectedEvent.time.size > 1) {
        furhat.ask("There are multiple showings of ${selectedEvent.name} at ${eventTimes(selectedEvent)}. What showing would you like to go to?")
    } else {
        furhat.say("There is a showing of ${selectedEvent.name} at ${selectedEvent.time.first()}.")
        val confirmTime = furhat.askYN("Does this time work for you?")
        if (confirmTime != false) {
            selectedTimeLocal = selectedEvent.time.first()
            confirmEventPurchase(furhat, selectedEvent, selectedTimeLocal)
            return Pair(true, selectedTimeLocal)
        }
    }
    return Pair(false, null)
}
fun getEventByName(eventName: String, eventList: List<Event>): Event? {
    return eventList.firstOrNull { it.name.toLowerCase() == eventName }
}


