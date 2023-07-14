package edu.ivankuznetsov.registerdataviabarcode.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.database.model.CustomerWithEvent
import java.time.LocalDateTime
import java.util.UUID

@Dao
interface CustomerDao {

    @Insert
    fun addCustomer(vararg customer: Customer)
    @Update
    fun editCustomer(vararg customer: Customer)
    @Delete
    fun dropCustomer(vararg customer: Customer)
    @Query("SELECT * FROM customers")
    fun getAll(): List<Customer>
    @Query("SELECT * FROM customers WHERE idCustomer = :uuid")
    fun getByUUID(uuid: UUID): Customer?

    @Query("SELECT * FROM customers WHERE date = :date")
    fun getAllByDate(date:LocalDateTime): List<Customer>

    @Transaction
    @Query("SELECT * FROM CUSTOMERS")
    fun getAllAuthorsWithBooks():List<CustomerWithEvent>

    @Query("SELECT * FROM CUSTOMERS AS C INNER JOIN EventsCustomersCrossRef AS T ON C.idCustomer == T.idCustomer AND T.idEvent == :eventID ")
    fun getAllByEventId(eventID: UUID): MutableList<Customer>
}