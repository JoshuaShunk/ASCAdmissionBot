package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.screening


import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.traveling.travelingExhibitAddOn
import java.time.LocalTime

import furhatos.flow.kotlin.*
import furhatos.nlu.common.Number
import furhatos.nlu.common.Time

val PurchaseScreenings: State = state {

    onEntry {
        if (todayScreenings.size == 1) {
            val onlyScreening = todayScreenings.first()
            val availableTimes = filterPastTimes(onlyScreening)
            if (availableTimes.size == 1) {
                val buyTickets = furhat.askYN("At the science center today we are showing ${onlyScreening.screeningName} at ${to12HourFormat(availableTimes.first())}. Would you like to purchase tickets for this screening?")
                if(buyTickets == true){goto(ConfirmScreeningWithOneTime)}
                else{goto(travelingExhibitAddOn)}

            } else if (availableTimes.size > 1){
                if (furhat.askYN("At the science center today we are showing ${onlyScreening.screeningName} at the following times: ${
                    availableTimes.joinToString(
                        separator = " and "
                    ) { to12HourFormat(it) }
                }. Would you like to purchase tickets for one of these screenings?")==true){
                    goto(AskForTimeForSingleScreening)
                }else{goto(travelingExhibitAddOn)}
            }else{
                //furhat.say("Sorry. There are no more available showing of ${onlyScreening.screeningName} today. Come back another time to watch it!")
                goto(travelingExhibitAddOn)
            }
        } else if (todayScreenings.size > 1) {
            if (furhat.askYN("At the science center today we have the following screenings: ${screeningNames(todayScreenings)}. Would you like to purchase tickets for any of these?") == true){
                goto(AskForSpecificScreening)
            } else {goto(travelingExhibitAddOn)}
        } else {
            furhat.say("Sorry there are no more available screenings today.")
            goto(travelingExhibitAddOn)
        }

    }
}

val AskForTimeForSingleScreening: State = state {
    onEntry {
        val availableTimes = filterPastTimes(todayScreenings.first())
        furhat.ask("What time would you like to watch the screening? Available options are: ${availableTimes.joinToString(separator = " and ")}")
    }

    onResponse<Time> {
        val selectedScreening = todayScreenings.first()
        if (it.text in selectedScreening.time) {
            confirmScreeningPurchase(furhat, selectedScreening, it.text)
            goto(travelingExhibitAddOn)
        } else {
            furhat.say("Sorry, that's not a valid screening time for ${selectedScreening.screeningName}.")
            reentry()
        }
    }
}

val ConfirmScreeningWithOneTime: State = state {
    onEntry {
        val onlyScreening = todayScreenings.first()
        val availableTimes = filterPastTimes(onlyScreening)
        confirmScreeningPurchase(furhat, onlyScreening, to12HourFormat(availableTimes.first()))
        goto(travelingExhibitAddOn)
    }
}

val AskForSpecificScreening: State = state {

    var selectedScreening: Screening? = null
    var selectedTime: String?
    var selectionSuccess: Boolean

    onEntry {
        furhat.ask("What screening would you like to purchase tickets for?")
    }

    onResponse<Time> {
        if (selectedScreening != null && it.text in selectedScreening!!.time) {
            selectedTime = it.text
            confirmScreeningPurchase(furhat, selectedScreening, selectedTime)
            goto(travelingExhibitAddOn)
        } else if (selectedScreening != null) {
            furhat.say("Sorry, that's not a valid screening time for ${selectedScreening?.screeningName}.")
            reentry()
        }
    }

    onResponse {
        val userInput = it.text.toLowerCase()
        selectedScreening = getScreeningByName(userInput)
        if (selectedScreening != null) {
            val (success, time) = handleScreeningSelection(furhat, selectedScreening!!)
            selectionSuccess = success
            if (time != null) {
                selectedTime = time
            }
        } else {
            furhat.say("Sorry, I didn't recognize that screening.")
            reentry()
        }

        if (selectionSuccess) {
            goto(travelingExhibitAddOn)
        } else {
            reentry()
        }
    }

    onNoResponse {
        terminate()
    }
}

fun getScreeningByName(screeningName: String): Screening? {
    return todayScreenings.firstOrNull { it.screeningName.toLowerCase() == screeningName }
}

fun handleScreeningSelection(furhat: Furhat, selectedScreening: Screening): Pair<Boolean, String?> {
    val selectedTimeLocal: String?
    if (selectedScreening.time.size > 1) {
        furhat.ask("There are multiple showings of ${selectedScreening.screeningName} at ${screeningTimes(selectedScreening)}. What showing would you like to go to?")
    } else {
        furhat.say("There is a showing of ${selectedScreening.screeningName} at ${selectedScreening.time.first()}.")
        val confirmTime = furhat.askYN("Does this time work for you?")
        if (confirmTime != false) {
            selectedTimeLocal = selectedScreening.time.first()
            confirmScreeningPurchase(furhat, selectedScreening, selectedTimeLocal)
            return Pair(true, selectedTimeLocal)
        }
    }
    return Pair(false, null)
}

fun confirmScreeningPurchase(furhat: Furhat, selectedScreening: Screening?, selectedTime: String?) {
    val confirmedAdd = furhat.askYN("Just to confirm. You want to add ${selectedScreening?.screeningName} at $selectedTime at a price of $${selectedScreening?.price} per ticket?")

    if (confirmedAdd == true) {
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

        furhat.say("Adding $adultCount adult tickets and $childCount child tickets for the $selectedTime showing of ${selectedScreening?.screeningName} to your purchase!")
    } else {
        furhat.ask("What screening would you like to purchase tickets for?")
    }
}

fun screeningNames(screenings: List<Screening>): String {
    return screenings.joinToString(separator = " and ") { it.screeningName }
}

fun screeningTimes(screening: Screening): String {
    return screening.time.joinToString(separator = " and ")
}

// Function to get the current hour and minute
fun getCurrentTime(): Pair<Int, Int> {
    val now = LocalTime.now()
    return Pair(now.hour, now.minute)
}

fun filterPastTimes(screening: Screening): List<String> {
    val (currentHour, currentMinute) = getCurrentTime()
    return screening.time.filter {
        val (screeningHour, screeningMinute) = it.split(":").map { timePart -> timePart.toInt() }
        screeningHour > currentHour || (screeningHour == currentHour && screeningMinute > currentMinute)
    }
}

fun to12HourFormat(time: String): String {
    val (hour, minute) = time.split(":").map { it.toInt() }
    val period = if (hour < 12) "AM" else "PM"
    val hour12Format = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
    return String.format("%d:%02d %s", hour12Format, minute, period)
}
