package edu.ivankuznetsov.registerdataviabarcode.database.entity

import androidx.room.Entity
import java.util.Date
import java.util.UUID

@Entity(tableName = "dataModel")
data class DataModel(var uuid: UUID, var fname: String, var sname: String = "", var lname: String, var company: String = "", var phone: String, var date: Date = Date())