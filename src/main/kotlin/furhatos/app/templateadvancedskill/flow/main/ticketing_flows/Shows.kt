package furhatos.app.templateadvancedskill.flow.main.ticketing_flows

import furhatos.nlu.SimpleIntent

class Shows (
    val showName : String,
    val time : List<String>,
    val price : Double
) : SimpleIntent(showName) {
    constructor() : this("DefaultName", listOf("default times"), 0.00)
}
val todayShows = listOf<Shows>(
    Shows(
        "Arizona Skies", listOf("1:00"), 9.50),
    Shows("Expedition: Solar System", listOf("11:30","2:30"), 9.50)
)
