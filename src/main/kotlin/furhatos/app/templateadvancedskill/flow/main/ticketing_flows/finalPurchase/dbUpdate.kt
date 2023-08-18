package furhatos.app.templateadvancedskill.flow.main.ticketing_flows.finalPurchase

import java.sql.DriverManager
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * A Java MySQL UPDATE example.
 * Demonstrates the use of a SQL UPDATE statement against a
 * MySQL database, called from a Java program.
 *
 * Created by Alvin Alexander, http://devdaily.com
 *
 */
object DBU {

    private val purchaseID = (Random.nextLong().toInt())

    fun updateDB(
        guestName: String?,
        adultTickets: Int,
        planetariumShow: String?,
        screenings: String?,
        exhibits: String?,
        total: Double
    ) {
        try {
            // create a MySQL database connection
            val myDriver = "com.mysql.cj.jdbc.Driver"
            val myUrl = "jdbc:mysql://localhost:3306/ascdb"

            // Ideally, fetch these from a configuration file or environment variable
            val dbUser = "root"
            val dbPassword = "Hockeyfan7696!"

            Class.forName(myDriver)
            val conn = DriverManager.getConnection(myUrl, dbUser, dbPassword)

            // Get the current date and time
            val purchaseDateTime = LocalDateTime.now()

            // Convert to SQL Timestamp
            val purchaseTimestamp = Timestamp.valueOf(purchaseDateTime)

            // create the MySQL update preparedstatement
            val query = """
            INSERT INTO guest_purchases 
            (purchaseID, purchaseDate, guestName, adultTickets, planetariumShow, screenings, exhibits, totalPurchase) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """

            val preparedStmt = conn.prepareStatement(query)
            preparedStmt.setInt(1, purchaseID)
            preparedStmt.setTimestamp(2, purchaseTimestamp)
            preparedStmt.setString(3, guestName)
            preparedStmt.setInt(4, adultTickets)
            preparedStmt.setString(5, planetariumShow)
            preparedStmt.setString(6, screenings)
            preparedStmt.setString(7, exhibits)
            preparedStmt.setDouble(8, total)

            // execute the prepared statement
            preparedStmt.executeUpdate()
            conn.close()
        } catch (e: Exception) {
            System.err.println("Got an exception!")
            System.err.println(e.message)
        }
    }
}