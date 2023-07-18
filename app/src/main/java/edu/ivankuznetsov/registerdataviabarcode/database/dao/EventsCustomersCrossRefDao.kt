package edu.ivankuznetsov.registerdataviabarcode.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import edu.ivankuznetsov.registerdataviabarcode.database.model.EventsCustomersCrossRef

@Dao
interface EventsCustomersCrossRefDao{
    @Insert
    fun addCrossData(vararg eventsCustomersCrossRefDao: EventsCustomersCrossRef)
    @Delete
    fun dropCrossData(vararg eventsCustomersCrossRefDao: EventsCustomersCrossRef)
}