package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.screening

import furhatos.app.templateadvancedskill.flow.main.ticketing_flows.planetarium.Event


// Defines a theater screening with attributes like screeningName and time
data class Screening(
    override val name: String,
    override val time: List<String>,
    override val price: Double
) : Event

// List of available screenings for the day
val todayScreenings = listOf(
    Screening("Jane Goodall Reasons for Hope", listOf("11:00", "13:00"), 15.0)
)

