package edu.ivankuznetsov.registerdataviabarcode.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface EventsCustomersCrossRefDao{
    @Insert
    fun addCrossData(vararg eventsCustomersCrossRefDao: EventsCustomersCrossRefDao)
    @Delete
    fun dropCrossData(vararg eventsCustomersCrossRefDao: EventsCustomersCrossRefDao)
}