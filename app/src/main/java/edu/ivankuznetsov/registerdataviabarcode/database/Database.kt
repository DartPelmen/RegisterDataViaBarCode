package edu.ivankuznetsov.registerdataviabarcode.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ivankuznetsov.registerdataviabarcode.database.converter.DateConverter
import edu.ivankuznetsov.registerdataviabarcode.database.converter.UUIDConverter
import edu.ivankuznetsov.registerdataviabarcode.database.dao.DataModelDao
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel


class DatabaseSingleton private constructor(context: Context) {
    val database: BarCodeDatabase

    init {
        database = databaseBuilder(context, BarCodeDatabase::class.java, "barcodes-db").build()
    }

    @Database(
        entities = [DataModel::class],
        version = 1
    )
    @TypeConverters(DateConverter::class,UUIDConverter::class)
    abstract class BarCodeDatabase : RoomDatabase() {
        abstract fun dataModelDao():DataModelDao
    }

    companion object {
        private var instance: BarCodeDatabase? = null
        fun getInstance(context: Context): BarCodeDatabase {
            synchronized(DatabaseSingleton::class.java) {
                if (instance == null) {
                    synchronized(DatabaseSingleton::class.java) {
                        instance = databaseBuilder(context,BarCodeDatabase::class.java, "datamodel-db").build()
                    }
                }
                return instance!!
            }
        }
    }
}