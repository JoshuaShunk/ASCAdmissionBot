package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.traveling

import furhatos.nlu.SimpleIntent

/**
 * Represents an exhibit with its name, location, and ticket price.
 * @property exhibitName Name of the exhibit.
 * @property location Location of the exhibit within the venue.
 * @property price Ticket price for the exhibit.
 */
class Exhibit (
    val exhibitName : String,
    private val location : String,
    val price : Double
) : SimpleIntent(exhibitName) {
    init {
        require(price >= 0) { "Price must be non-negative" }
    }

    constructor() : this("DefaultName", "Floor Zero", 0.00)
}
val todayExhibit = listOf(
    Exhibit("The Science of Guinness World Records", "Third Floor", 9.50)
)
