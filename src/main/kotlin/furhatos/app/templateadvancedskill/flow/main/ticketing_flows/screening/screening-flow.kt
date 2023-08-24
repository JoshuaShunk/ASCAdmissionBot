package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.screening

import furhatos.app.templateadvancedskill.flow.main.normalizeTimeInput
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.*
import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.traveling.travelingExhibitAddOn
import furhatos.app.templateadvancedskill.flow.main.to12HourFormat
import furhatos.app.templateadvancedskill.flow.main.to24HourFormat


import furhatos.flow.kotlin.*
import furhatos.nlu.common.Time

val PurchaseScreenings: State = state {

    onEntry {
        when {
            todayScreenings.size == 1 -> {
                val onlyScreening = todayScreenings.first()
                val availableTimes = filterPastTimes(onlyScreening)

                when {
                    availableTimes.size == 1 -> {
                        if (furhat.askYN("At the science center today we are showing ${onlyScreening.name} at ${to12HourFormat(availableTimes.first())}. Would you like to purchase tickets for this screening?") == true) {
                            goto(ConfirmScreeningWithOneTime)
                        } else {
                            goto(travelingExhibitAddOn)
                        }
                    }
                    availableTimes.isNotEmpty() -> {
                        if (furhat.askYN("At the science center today we are showing ${onlyScreening.name} at the following times: ${availableTimes.joinToString(separator = " and ") { to12HourFormat(it) }}. Would you like to purchase tickets for one of these screenings?") == true) {
                            goto(AskForTimeForSingleScreening)
                        } else {
                            goto(travelingExhibitAddOn)
                        }
                    }
                    else -> goto(travelingExhibitAddOn)
                }
            }
            todayScreenings.size > 1 -> {
                if (furhat.askYN("At the science center today we have the following screenings: ${eventNames(todayScreenings)}. Would you like to purchase tickets for any of these?") == true) {
                    goto(AskForSpecificScreening)
                } else {
                    goto(travelingExhibitAddOn)
                }
            }
            else -> {
                furhat.say("Sorry there are no more available screenings today.")
                goto(travelingExhibitAddOn)
            }
        }
    }
}

val AskForTimeForSingleScreening: State = state {
    onEntry {
        val availableTimes = filterPastTimes(todayScreenings.first())
        furhat.ask("What time would you like to watch the screening? Available options are: ${availableTimes.joinToString(separator = " and ") { to12HourFormat(it) }}")
    }

    onResponse { it ->
        val selectedScreening = todayScreenings.first()
        val normalizedTime = normalizeTimeInput(it.text)?.let { to24HourFormat(it) }
        if (normalizedTime in selectedScreening.time) {
            confirmEventPurchase(furhat, selectedScreening, normalizedTime)
            goto(travelingExhibitAddOn)
        } else {
            furhat.say("Sorry, that's not a valid screening time for ${selectedScreening.name}.")
            reentry()
        }
    }
}

val ConfirmScreeningWithOneTime: State = state {
    onEntry {
        val onlyScreening = todayScreenings.first()
        confirmEventPurchase(furhat, onlyScreening,
            normalizeTimeInput(onlyScreening.time.first())?.let { to24HourFormat(it) })
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
        if (selectedScreening != null && normalizeTimeInput(it.text)?.let { it1 -> to24HourFormat(it1) } in selectedScreening!!.time) {
            selectedTime = normalizeTimeInput(it.text)?.let { it1 -> to24HourFormat(it1) }
            confirmEventPurchase(furhat, selectedScreening, selectedTime)
            goto(travelingExhibitAddOn)
        } else if (selectedScreening != null) {
            furhat.say("Sorry, that's not a valid screening time for ${selectedScreening?.name}.")
            reentry()
        } else{
            furhat.ask("Sorry, I'm not sure what you said. Can you repeat the screening time again?")
        }
    }

    onResponse {
        val normalizedTime = normalizeTimeInput(it.text.toLowerCase())
        if(normalizedTime != null && normalizedTime in selectedScreening!!.time && selectedScreening != null){
            selectedTime = normalizeTimeInput(it.text)?.let { it1 -> to24HourFormat(it1) }
            confirmEventPurchase(furhat, selectedScreening, selectedTime)
            goto(travelingExhibitAddOn)
        }
        else {
            val userInput = it.text.toLowerCase()
            selectedScreening = getEventByName(userInput, todayScreenings) as? Screening

            if (selectedScreening != null) {
                val (success, time) = handleEventSelection(furhat, selectedScreening!!)
                selectionSuccess = success
                if (time != null) {
                    selectedTime = time
                }
            } else {
                furhat.say("Sorry, I didn't recognize that screening.")
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
















