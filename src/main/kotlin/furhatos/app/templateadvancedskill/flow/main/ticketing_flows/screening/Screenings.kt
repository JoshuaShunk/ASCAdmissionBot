/*

package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.Screening


import furhatos.nlu.SimpleIntent

class Screenings (
    val screenName : String,
    val time : List<String>,
    val price : Double
) : SimpleIntent(screenName){
    constructor() : this("DefaultName", listOf("default times"), 0.00)
}
val todayScreenings = listOf<Screenings>(
    Screenings("Jane Goodall Reasons for Hope", listOf("11:00","1:00"), 9.50)

)
*/

package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.screening

// Defines a theater screening with attributes like screeningName and time
data class Screening(val screeningName: String, val time: List<String>, val price: Double)

// List of available screenings for the day
val todayScreenings = listOf(
    Screening("Jane Goodall Reasons for Hope", listOf("11:00", "13:00"), 15.0)
)

