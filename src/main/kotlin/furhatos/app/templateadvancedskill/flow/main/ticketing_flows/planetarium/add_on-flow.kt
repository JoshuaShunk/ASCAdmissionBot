package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.planetarium

import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.CartItem
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.customerCart
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.screening.PurchaseScreenings
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.screening.to12HourFormat
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Time
import java.time.LocalTime
import furhatos.nlu.common.Number



val PurchaseAddOns: State = state {
    onEntry {
        if (todayShows.size == 1) {
            val onlyShow = todayShows.first()
            val availableTimes = filterPastShowTimes(onlyShow)
            if (availableTimes.size == 1) {
                furhat.askYN("At the science center today we are showing ${onlyShow.showName} at ${to12HourFormat(availableTimes.first())}. Would you like to purchase tickets for this show?")
                goto(ConfirmShowWithOneTime)
            } else if (todayShows.size > 1) {
                furhat.askYN("At the science center today we are showing ${onlyShow.showName} at the following times: ${
                    availableTimes.joinToString(
                        separator = " and "
                    ) { to12HourFormat(it) }
                }. Would you like to purchase tickets for this show?")
                goto(AskForTimeForSingleShow)
            } else {
                goto(PurchaseScreenings)
            }
        } else if (todayShows.size > 1) {
            furhat.askYN("At the science center today we have the following shows: ${showNames(todayShows)}. Would you like to purchase tickets for any of these?")
            goto(AskForSpecificShow)
        } else {
            furhat.say("Sorry there are no more available shows today.")
            goto(PurchaseScreenings)
        }
    }
}

val AskForTimeForSingleShow: State = state {
    onEntry {
        val availableTimes = filterPastShowTimes(todayShows.first())
        furhat.ask("What time would you like to watch the show? Available options are: ${availableTimes.joinToString(separator = " and ")}")
    }

    onResponse<Time> {
        val selectedTime = to12HourFormat(it.text)
        val show = todayShows.first()
        if (selectedTime in show.time) {
            confirmShowPurchase(furhat, show, selectedTime)
            goto(PurchaseScreenings)
        } else {
            furhat.say("Sorry, that's not a valid show time for ${show.showName}.")
            reentry()
        }
    }

    onNoResponse {
        terminate()
    }
}

val AskForSpecificShow: State = state {
    onEntry {
        furhat.ask("What show would you like to purchase tickets for?")
    }

    onResponse {
        val userInput = it.text.toLowerCase()
        val selectedShow = getShowByName(userInput)
        if (selectedShow != null) {
            val (success, time) = handleShowSelection(furhat, selectedShow)
            if (success && time != null) {
                confirmShowPurchase(furhat, selectedShow, time)
                goto(PurchaseScreenings)
            }
        } else {
            furhat.say("Sorry, I didn't recognize that show.")
            reentry()
        }
    }

    onNoResponse {
        terminate()
    }
}

val ConfirmShowWithOneTime: State = state {
    onEntry {
        val onlyShow = todayShows.first()
        val availableTimes = filterPastShowTimes(onlyShow)
        confirmShowPurchase(furhat, onlyShow, availableTimes.first())
        goto(PurchaseScreenings)
    }
}



// Helper functions:
fun getShowByName(name: String): Show? {
    return todayShows.find { it.showName.equals(name, ignoreCase = true) }
}

fun handleShowSelection(furhat: Furhat, show: Show): Pair<Boolean, String?> {
    val availableTimes = filterPastShowTimes(show)
    return if (availableTimes.size == 1) {
        Pair(true, availableTimes.first())
    } else if (availableTimes.isNotEmpty()) {
        furhat.ask("The show ${show.showName} is available at the following times: ${
            availableTimes.joinToString(", ") {
                to12HourFormat(
                    it
                )
            }
        }. Which one would you prefer?")
        Pair(false, null)
    } else {
        furhat.say("Sorry, there are no more available times for ${show.showName} today.")
        Pair(false, null)
    }
}

fun confirmShowPurchase(furhat: Furhat, show: Show, time: String) {
    if(furhat.askYN("You have selected the ${show.showName} show at ${to12HourFormat(time)}. Would you like to confirm this?") ==true){
        val adultCount = furhat.askFor<Number>("How many adult tickets would you like to purchase?")
        val childCount = furhat.askFor<Number>("How many children ages 3-17?")
        if(adultCount.toString().toInt()>0){
            customerCart.add(CartItem(show.showName + ("Adult"),adultCount.toString().toInt(), show.price.toString().toDouble()))
        }
        if(childCount.toString().toInt()>0){
            customerCart.add(CartItem(show.showName + ("Child"),childCount.toString().toInt(), show.price.toString().toDouble()))
        }
        furhat.say("I have added " + adultCount + " adult tickets and " + childCount + " child tickets for the " + show.time[0] + " showing of " + show.showName + " to your purchase!")
    }

}

fun showNames(shows: List<Show>): String {
    return shows.joinToString(separator = " and ") { it.showName }
}

fun getCurrentTime(): Pair<Int, Int> {
    val now = LocalTime.now()
    return Pair(now.hour, now.minute)
}

fun filterPastShowTimes(show: Show): List<String> {
    val (currentHour, currentMinute) = getCurrentTime()
    return show.time.filter {
        val (showHour, showMinute) = it.split(":").map { timePart -> timePart.toInt() }
        showHour > currentHour || (showHour == currentHour && showMinute > currentMinute)
    }
}

