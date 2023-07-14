package edu.ivankuznetsov.registerdataviabarcode.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event

data class EventWithCustomer(@Embedded val event: Event,
                        @Relation(parentColumn = "idEvent",
                            entityColumn = "idCustomer",
                            associateBy = Junction(EventsCustomersCrossRef::class))
                        val events: MutableList<Customer>
)