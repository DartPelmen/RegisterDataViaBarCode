package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ivankuznetsov.registerdataviabarcode.database.DatabaseSingleton
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import edu.ivankuznetsov.registerdataviabarcode.database.model.EventsCustomersCrossRef
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.Executors

class CustomerViewModel: ViewModel() {
    private val executorService = Executors.newSingleThreadExecutor()
    val data = MutableLiveData<MutableList<Customer>>()
    val currentEvent = MutableLiveData<Event>()
    init {
        data.value = mutableListOf()
    }
    fun addData(context: Context, customer: List<Customer>){
        Log.d("CUSTOMERS", "ADDING CUSTOMERS")
        Log.d("CUSTOMERS", customer.size.toString())
        customer.forEach {
            Log.d("CUSTOMERS", it.idCustomer.toString())
        }
        executorService.execute {
            currentEvent.value?.let {
                Log.d("CUSTOMERS", "CURRENT EVENT IS ${it.idEvent}")
                DatabaseSingleton.getInstance(context).database.customerDao().addCustomer(*customer.toTypedArray())
                val crossList = customer.map { x -> EventsCustomersCrossRef(x.idCustomer,it.idEvent) }
                DatabaseSingleton.getInstance(context).database.crossDao().addCrossData(*crossList.toTypedArray())
                data.postValue(DatabaseSingleton.getInstance(context).database.customerDao().getAllByEventId(UUID.fromString(it.idEvent.toString())))
            }?: Log.d("CUSTOMERS", "CURRENT EVENT IS NULL")

        }
    }
    fun contains(customer: Customer): Boolean = data.value?.stream()?.anyMatch { x -> x.idCustomer == customer.idCustomer } ?: false

    fun setCurrentCustomer(uuid: String, context: Context){
        executorService.execute {
            currentEvent.postValue(DatabaseSingleton.getInstance(context).database.eventsDao().getByUUID(UUID.fromString(uuid)))
            Log.d("CUSTOMERS", "CURRENT UUID FOR SET CURRENT EVENT IS $uuid")
            data.postValue(DatabaseSingleton.getInstance(context).database.customerDao().getAllByEventId(UUID.fromString(uuid)))

        }
    }
    fun getAll(context: Context){
        executorService.execute {
            data.postValue(DatabaseSingleton.getInstance(context).database.customerDao().getAll().toMutableList()) }
    }

    fun getAllByDate(context: Context, date: LocalDateTime){
        executorService.execute {
            data.postValue(DatabaseSingleton.getInstance(context).database.customerDao().getAllByDate(date).toMutableList()) }
    }

    fun dropData(context: Context, customer: List<Customer>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.customerDao().dropCustomer(*customer.toTypedArray())
            val list = data.value
            list?.let {
                data.postValue(it.filterNot { x -> x in customer }.toMutableList())
            }
        }
    }

    fun updateData(context: Context, customer: List<Customer>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.customerDao().editCustomer(*customer.toTypedArray())
            val list = data.value
            customer.forEach {
                list?.set(list.indexOf(it), it)
            }
            data.postValue(data.value)
        }
    }
}