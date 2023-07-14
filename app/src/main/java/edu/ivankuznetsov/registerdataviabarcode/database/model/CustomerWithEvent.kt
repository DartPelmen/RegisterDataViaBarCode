package edu.ivankuznetsov.registerdataviabarcode.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event

data class CustomerWithEvent(@Embedded val customer: Customer,
                             @Relation(parentColumn = "idCustomer",
                                 entityColumn = "idEvent",
                                 associateBy = Junction(EventsCustomersCrossRef::class))
                                val events: MutableList<Event>)