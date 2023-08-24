package furhatos.app.templateadvancedskill.flow.main

import java.time.LocalTime

fun to12HourFormat(time: String): String {
    val (hour, minute) = time.split(":").map { it.toInt() }
    val period = if (hour < 12) "AM" else "PM"
    val hour12Format = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
    return String.format("%d:%02d %s", hour12Format, minute, period)
}

fun normalizeTimeInput(timeInput: String): String? {

    if (timeInput.isBlank()) {
        return null
    }

    // Clean the input string: remove "a.m.", "p.m.", "am", "pm" and trim whitespace
    val cleanedInput = timeInput.replace("a.m.", "").replace("p.m.", "").replace("am", "").replace("pm", "").trim()
    // 1. Extract time mentions from the sentence
    val timePattern = """(\d{1,2}):?(\d{0,2})(?:\s?[oO]-?[cC]lock)?""".toRegex()
    val matchResult = timePattern.find(cleanedInput)
    val extractedHour = matchResult?.groups?.get(1)?.value
    val extractedMinute = matchResult?.groups?.get(2)?.value ?: "00"

    if (extractedHour != null) {
        // 2. Remove AM/PM mentions (if any) and apply necessary conversion
        var hour = extractedHour.toInt()
        if (timeInput.toLowerCase().contains("pm") && hour in 1..11) {
            hour += 12
        }
        if (timeInput.toLowerCase().contains("am") && hour == 12) {
            hour = 0
        }
        // Validate the hour and minute values
        if (hour !in 0..23 || extractedMinute.toInt() !in 0..59) {
            return null
        }

        // 3. Standardize to HH:MM format (24-hour format)
        return String.format("%02d:%02d", hour, extractedMinute.toInt())
    }
    return null
}

fun to24HourFormat(time: String): String {
    val (hour, minute) = time.split(":").map { it.toInt() }

    return when(hour) {
        in 1..9 -> String.format("%02d:%02d", hour + 12, minute)  // Convert 1-9 PM times
        10, 11 -> String.format("%02d:%02d", hour, minute)  // Keep 10 and 11 as AM times
        else -> time  // Return input as is if it doesn't match the above cases
    }
}

fun getCurrentTime(): Pair<Int, Int> {
    val now = LocalTime.now()
    return now.hour to now.minute
}
