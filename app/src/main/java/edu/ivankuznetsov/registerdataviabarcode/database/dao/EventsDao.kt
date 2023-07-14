package edu.ivankuznetsov.registerdataviabarcode.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import edu.ivankuznetsov.registerdataviabarcode.database.model.EventWithCustomer
import java.util.UUID

@Dao
interface EventsDao {

    @Insert
    fun addEvent(vararg event: Event)
    @Update
    fun editEvent(vararg event: Event)
    @Delete
    fun dropEvent(vararg event: Event)
    @Query("SELECT * FROM events")
    fun getAll(): List<Event>
    @Query("SELECT * FROM events WHERE idEvent = :uuid")
    fun getByUUID(uuid: UUID): Event?
    @Transaction
    @Query("SELECT * FROM events")
    fun getAllAuthorsWithBooks():List<EventWithCustomer>

    @Query("SELECT * FROM EVENTS AS C INNER JOIN EVENTSCUSTOMERSCROSSREF AS T ON C.idEvent == T.idEvent AND T.idCustomer == :customerId ")
    fun getAllByCustomerId(customerId: UUID): MutableList<Event>
}