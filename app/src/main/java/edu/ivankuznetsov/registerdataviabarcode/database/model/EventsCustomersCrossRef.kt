package edu.ivankuznetsov.registerdataviabarcode.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.RESTRICT
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import edu.ivankuznetsov.registerdataviabarcode.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID


@Entity(primaryKeys = ["idCustomer", "idEvent"],
    foreignKeys = [
        ForeignKey(Customer::class,
            parentColumns = ["idCustomer"],
            childColumns = ["idCustomer"],
            onDelete = CASCADE,
            onUpdate = CASCADE),
        ForeignKey(Event::class, parentColumns = ["idEvent"], childColumns = ["idEvent"], onDelete = RESTRICT, onUpdate = CASCADE)
    ])
data class EventsCustomersCrossRef (
    @Serializable(UUIDSerializer::class) val idCustomer: UUID, @Serializable(UUIDSerializer::class) val idEvent: UUID)

