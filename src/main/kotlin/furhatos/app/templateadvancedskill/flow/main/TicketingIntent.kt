package furhatos.app.templateadvancedskill.flow.main

import furhatos.nlu.SimpleIntent

class TicketingIntent(
    val questions : List<String>,
    val answer : String
) : SimpleIntent(questions)

val tQuestions =
    listOf(
        TicketingIntent(
            listOf("I would like to buy tickets", "Buy tickets", "Tickets", "Ticketing"),
            "Sure!"
        )
    )