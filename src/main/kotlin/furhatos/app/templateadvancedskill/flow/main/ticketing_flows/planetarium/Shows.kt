package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.planetarium


interface Event {
    val name: String
    val time: List<String>
    val price: Double
}

// Defines a show with attributes like showName, time, and price
data class Show(
    override val name: String,
    override val time: List<String>,
    override val price: Double
) : Event


// List of available shows for the day
val todayShows = listOf(
    Show("Expedition Solar System", listOf("11:30", "14:30"), 9.50),
    Show("Arizona Skies", listOf("13:00"), 11.50)
)