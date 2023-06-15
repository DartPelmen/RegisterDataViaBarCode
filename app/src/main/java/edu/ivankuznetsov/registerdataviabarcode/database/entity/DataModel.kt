package edu.ivankuznetsov.registerdataviabarcode.database.entity

import android.provider.ContactsContract.Data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

@Entity(tableName = "dataModel")
data class DataModel(@PrimaryKey var uuid: UUID, var fname: String, var sname: String = "", var lname: String, var rank: String = "", var phone: String, var office: String, var date: LocalDateTime = LocalDateTime.now()) {
    override fun equals(other: Any?): Boolean {
        return if(other is DataModel){
            this.uuid == other.uuid &&
            (this.rank == other.rank) &&
            (this.office == other.office) &&
            (this.fname == other.fname) &&
            (this.lname == other.lname) &&
            (this.sname == other.sname) &&
            (this.phone == other.phone)
        } else false
    }
}