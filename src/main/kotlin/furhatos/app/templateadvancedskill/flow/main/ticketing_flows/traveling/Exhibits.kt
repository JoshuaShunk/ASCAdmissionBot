package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.traveling

import furhatos.nlu.SimpleIntent

class Exhibit (
    val exhibitName : String,
    val location : String,
    val price : Double
) : SimpleIntent(exhibitName){
    constructor() : this("DefaultName", "Floor Zero", 0.00)
}
val todayExhibit = listOf(
    Exhibit("The Science of Guinness World Records", "Third Floor", 9.50)
)
