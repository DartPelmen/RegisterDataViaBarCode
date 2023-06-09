package edu.ivankuznetsov.registerdataviabarcode.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ivankuznetsov.registerdataviabarcode.database.converter.DateConverter
import edu.ivankuznetsov.registerdataviabarcode.database.converter.UUIDConverter


class DatabaseSingleton private constructor(context: Context) {
    val database: BarCodeDatabase

    init {
        database = databaseBuilder(context, BarCodeDatabase::class.java, "barcodes-db").build()
    }

    @Database(
        entities = [/*User::class, Any::class, Medicine::class, Person::class, Place::class, WebPage::class*/],
        version = 1
    )
    @TypeConverters(DateConverter::class,UUIDConverter::class)
    abstract class BarCodeDatabase : RoomDatabase() {
//        abstract fun userDao(): UserDao?
//        abstract fun personDao(): PersonDao?
//        abstract fun objectDao(): ObjectDao?
//        abstract fun medicineDao(): MedicineDao?
//        abstract fun placeDao(): PlaceDao?
//        abstract fun webPageDao(): WebPageDao?
    }

    companion object {
        private var instance: DatabaseSingleton? = null
        fun getInstance(context: Context): DatabaseSingleton? {
            synchronized(DatabaseSingleton::class.java) {
                if (instance == null) {
                    synchronized(DatabaseSingleton::class.java) {
                        instance = DatabaseSingleton(context)
                    }
                }
                return instance
            }
        }
    }
}