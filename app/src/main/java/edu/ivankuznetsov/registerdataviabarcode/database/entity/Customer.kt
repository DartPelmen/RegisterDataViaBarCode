package edu.ivankuznetsov.registerdataviabarcode.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.ivankuznetsov.registerdataviabarcode.util.LocalDateTimeSerializer
import edu.ivankuznetsov.registerdataviabarcode.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

/*
{
   "fname": "Ivan",
   "sname": "Vladimirovich",
   "lname": "Kuznetsov",
   "rank": "st. teacher",
   "phone": "+79990882846",
   "office": "VTiKG",
   "date": "1686888022",
   "idCustomer": "6bc34baf-19c1-4343-8bb5-57413b54631f"
}
*
*
* */
@Serializable
@Entity(tableName = "customers")
data class Customer(@PrimaryKey
                    @Serializable(UUIDSerializer::class)
                    var idCustomer: UUID = UUID.randomUUID(),
                    var fname: String,
                    var sname: String = "",
                    var lname: String,
                    var rank: String = "",
                    var phone: String,
                    var office: String,
                    @Serializable(LocalDateTimeSerializer::class)
                    var date: LocalDateTime = LocalDateTime.now()) {
    override fun equals(other: Any?): Boolean {
        return if(other is Customer){
            this.idCustomer == other.idCustomer &&
            (this.rank == other.rank) &&
            (this.office == other.office) &&
            (this.fname == other.fname) &&
            (this.lname == other.lname) &&
            (this.sname == other.sname) &&
            (this.phone == other.phone)
        } else false
    }

    override fun hashCode(): Int {
        var result = idCustomer.hashCode()
        result = 31 * result + fname.hashCode()
        result = 31 * result + sname.hashCode()
        result = 31 * result + lname.hashCode()
        result = 31 * result + rank.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + office.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}