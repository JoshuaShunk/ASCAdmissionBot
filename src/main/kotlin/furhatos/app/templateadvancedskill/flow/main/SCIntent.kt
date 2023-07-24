package furhatos.app.templateadvancedskill.flow.main

import furhatos.nlu.SimpleIntent

class SCIntent(
    val questions : List<String>,
    val answer : String
) : SimpleIntent(questions)

val sQuestions =
    listOf(
        SCIntent(
            listOf("What time does the science center open", "what are the hours", "what time does the science center close"),
            "The Science Center is open from 10:30 to 4:00 every day!"
        ),
        SCIntent(
            listOf("What it the current GA price", "What is the current general admission price", "what is the current admission price", "How much are tickets"),
            "General admission tickets cost $21.95 for adults and $15.95 for children ages 3-17. Children under 3 are free. Would you like to buy tickets?"
        ),
        SCIntent(
            listOf("What is create", "What is the create center"),
            "CREATE is a 6,500 square-foot community hub for turning dreams into reality. At CREATE, people of all ages can share ideas, " +
                    "collaborate on projects and learn about science, technology, engineering, math, and art through making. By using state-of-the-art, " +
                    "computer-controlled equipment and traditional crafting tools, visitors will have the chance to cut, saw, code, hammer, sew, paint, " +
                    "solder and much more!"
        )
    )