package edu.ivankuznetsov.registerdataviabarcode.database.entity

import android.provider.ContactsContract.Data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "dataModel")
data class DataModel(@PrimaryKey var uuid: UUID, var fname: String, var sname: String = "", var lname: String, var company: String = "", var phone: String, var date: Date = Date()) {
    override fun equals(other: Any?): Boolean {
        return if(other is DataModel){
            this.uuid == other.uuid &&
            (this.company == other.company) &&
            (this.date == other.date) &&
            (this.fname == other.fname) &&
            (this.lname == other.lname) &&
            (this.sname == other.sname) &&
            (this.phone == other.phone)
        } else false
    }
}