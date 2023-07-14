package edu.ivankuznetsov.registerdataviabarcode.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.ivankuznetsov.registerdataviabarcode.util.LocalDateTimeSerializer
import edu.ivankuznetsov.registerdataviabarcode.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID


@Serializable
@Entity(tableName = "events")
data class Event(@PrimaryKey
                 @Serializable(UUIDSerializer::class)
                 var idEvent: UUID = UUID.randomUUID(),
                 var title: String,
                 @Serializable(LocalDateTimeSerializer::class)
                 var startDateTime: LocalDateTime,
                 @Serializable(LocalDateTimeSerializer::class)
                 var endDateTime: LocalDateTime) {
    override fun equals(other: Any?): Boolean {
        return if(other is Event){
            this.idEvent == other.idEvent &&
                    (this.title == other.title) &&
                    (this.startDateTime == other.startDateTime) &&
                    (this.endDateTime == other.endDateTime)
        } else false
    }

    override fun hashCode(): Int {
        var result = idEvent.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + startDateTime.hashCode()
        result = 31 * result + endDateTime.hashCode()
        return result
    }
}