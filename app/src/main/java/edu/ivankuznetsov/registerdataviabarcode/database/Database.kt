package edu.ivankuznetsov.registerdataviabarcode.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ivankuznetsov.registerdataviabarcode.database.converter.DateConverter
import edu.ivankuznetsov.registerdataviabarcode.database.converter.UUIDConverter
import edu.ivankuznetsov.registerdataviabarcode.database.dao.CustomerDao
import edu.ivankuznetsov.registerdataviabarcode.database.dao.EventsCustomersCrossRefDao
import edu.ivankuznetsov.registerdataviabarcode.database.dao.EventsDao
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import edu.ivankuznetsov.registerdataviabarcode.database.model.EventsCustomersCrossRef
import java.util.concurrent.Executors


class DatabaseSingleton private constructor(context: Context) {
    val database: BarCodeDatabase

    init {
        database = databaseBuilder(context, BarCodeDatabase::class.java, "barcodes-db")
            .setQueryCallback(object : RoomDatabase.QueryCallback{
                override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
                    Log.d("SQLQUERY", sqlQuery)
                    bindArgs.forEach { Log.d("SQLARGS", it.toString()) }
                }
        },Executors.newSingleThreadExecutor())
            .build()
    }

    @Database(
        entities = [Customer::class, Event::class, EventsCustomersCrossRef::class],
        version = 1
    )
    @TypeConverters(DateConverter::class,UUIDConverter::class)
    abstract class BarCodeDatabase : RoomDatabase() {
        abstract fun customerDao():CustomerDao
        abstract fun eventsDao():EventsDao
        abstract fun crossDao():EventsCustomersCrossRefDao
    }

    companion object {
        private var instance: DatabaseSingleton? = null
        fun getInstance(context: Context): DatabaseSingleton {
            synchronized(DatabaseSingleton::class.java) {
                if (instance == null) {
                    synchronized(DatabaseSingleton::class.java) {
                        instance = DatabaseSingleton(context)
                    }
                }
                return instance!!
            }
        }
    }
}
