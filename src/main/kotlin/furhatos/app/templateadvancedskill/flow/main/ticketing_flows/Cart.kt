package furhatos.app.templateadvancedskill.flow.main.ticketing_flows


class CartItem (
    val item : String,
    val quantity : Int,
    val price : Double
)
{
    constructor() : this("Ticket", 2, 12.00)
}
val customerCart = mutableListOf<CartItem>()
