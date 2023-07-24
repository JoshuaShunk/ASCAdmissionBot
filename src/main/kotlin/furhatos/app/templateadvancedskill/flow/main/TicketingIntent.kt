package furhatos.app.templateadvancedskill.flow.main

import furhatos.nlu.SimpleIntent

class TicketingIntent(
    val questions : List<String>,
    val answer : String
) : SimpleIntent(questions)

val tQuestions =
    listOf(
        TicketingIntent(
            listOf("What time does the science center open", "what are the hours", "what time does the science center close"),
            "The Science Center is open from 10:30 to 4:00 every day!"
        ),
        TicketingIntent(
            listOf("what is your name", "who are you"),
            "My name is Furhat"
        ),
        TicketingIntent(
            listOf("what is your favorite food", "do you have a favorite food"),
            "I love meatballs"
        )
    )