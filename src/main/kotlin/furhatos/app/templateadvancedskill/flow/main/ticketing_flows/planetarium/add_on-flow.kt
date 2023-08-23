package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.planetarium


import furhatos.app.templateadvancedskill.flow.main.normalizeTimeInput
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.*
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.traveling.travelingExhibitAddOn
import furhatos.app.templateadvancedskill.flow.main.to12HourFormat
import furhatos.app.templateadvancedskill.flow.main.to24HourFormat

import furhatos.flow.kotlin.*
import furhatos.nlu.common.Time

val PurchaseShows: State = state {

    onEntry {
        if (todayShows.size == 1) {
            val onlyShow = todayShows.first()
            val availableTimes = filterPastTimes(onlyShow)
            if (availableTimes.size == 1) {
                val buyTickets = furhat.askYN("At the science center today we are showing ${onlyShow.name} at ${to12HourFormat(availableTimes.first())}. Would you like to purchase tickets for this screening?")
                if(buyTickets == true){goto(ConfirmShowWithOneTime)}
                else{goto(travelingExhibitAddOn)}

            } else if (availableTimes.size > 1){
                if (furhat.askYN("At the science center today we are showing ${onlyShow.name} at the following times: ${
                        availableTimes.joinToString(
                            separator = " and "
                        ) { to12HourFormat(it) }
                    }. Would you like to purchase tickets for one of these screenings?")==true){
                    goto(AskForTimeForSingleShow)
                }else{goto(travelingExhibitAddOn)}
            }else{
                //furhat.say("Sorry. There are no more available showing of ${onlyShow.showName} today. Come back another time to watch it!")
                goto(travelingExhibitAddOn)
            }
        } else if (todayShows.size > 1) {
            if (furhat.askYN("At the science center today we have the following screenings: ${eventNames(todayShows)}. Would you like to purchase tickets for any of these?") == true){
                goto(AskForSpecificShow)
            } else {goto(travelingExhibitAddOn)}
        } else {
            furhat.say("Sorry there are no more available screenings today.")
            goto(travelingExhibitAddOn)
        }

    }
}

val AskForTimeForSingleShow: State = state {
    onEntry {
        val availableTimes = filterPastTimes(todayShows.first())
        val formattedTimes = availableTimes.map { to12HourFormat(it) }
        furhat.ask("What time would you like to watch the show? Available options are: ${formattedTimes.joinToString(separator = " and ")}")
    }

    onResponse {
        val selectedShow = todayShows.first()
        print("Normalized Time: " + normalizeTimeInput(it.text))

        if (normalizeTimeInput(it.text) != null) {

            if (normalizeTimeInput(it.text)?.let { it1 -> to24HourFormat(it1) } in selectedShow.time) {
                confirmEventPurchase(furhat, selectedShow, it.text)
                goto(travelingExhibitAddOn)
            } else {
                furhat.say("Sorry, that's not a valid show time for ${selectedShow.name}.")
                reentry()
            }
        }
    }
}

val ConfirmShowWithOneTime: State = state {
    onEntry {
        val onlyShow = todayShows.first()
        val availableTimes = filterPastTimes(onlyShow)
        confirmEventPurchase(furhat, onlyShow, to12HourFormat(availableTimes.first()))
        goto(travelingExhibitAddOn)
    }
}

val AskForSpecificShow: State = state {

    var selectedShow: Show? = null
    var selectedTime: String?
    var selectionSuccess: Boolean

    onEntry {
        furhat.ask("What show would you like to purchase tickets for?")
    }

    onResponse<Time> {
        if (selectedShow != null && normalizeTimeInput(it.text)?.let { it1 -> to24HourFormat(it1) } in selectedShow!!.time) {
            selectedTime = normalizeTimeInput(it.text)
            confirmEventPurchase(furhat, selectedShow, selectedTime)
            goto(travelingExhibitAddOn)
        } else if (selectedShow != null) {
            furhat.say("Sorry, that's not a valid show time for ${selectedShow?.name}.")
            reentry()
        }
    }

    onResponse {
        val normalizedTime = normalizeTimeInput(it.text.toLowerCase())
        if(normalizedTime != null && normalizedTime in selectedShow!!.time && selectedShow != null){
            selectedTime = it.text
            confirmEventPurchase(furhat, selectedShow, selectedTime)
            goto(travelingExhibitAddOn)
        }
        else {
            val userInput = it.text.toLowerCase()
            selectedShow = getEventByName(userInput, todayShows) as? Show

            if (selectedShow != null) {
                val (success, time) = handleEventSelection(furhat, selectedShow!!)
                selectionSuccess = success
                if (time != null) {
                    selectedTime = time
                }
            } else {
                furhat.say("Sorry, I didn't recognize that show.")
                reentry()
            }
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











