package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import android.content.Context
import android.provider.ContactsContract.Data
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ivankuznetsov.registerdataviabarcode.database.DatabaseSingleton
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class EventsViewModel: ViewModel() {
    private val executorService = Executors.newSingleThreadExecutor()
    val data = MutableLiveData<MutableList<Event>>()
    init {
        data.value = mutableListOf()
    }
    fun addEvents(context: Context, events: List<Event>){
        executorService.execute {
            DatabaseSingleton.getInstance(context)
                .database.eventsDao().addEvent(*events.toTypedArray())
            getAll(context)
        }
    }
    fun contains(event: Event): Boolean =
        data.value?.stream()?.anyMatch { x -> x.idEvent == event.idEvent } ?: false


    fun getAll(context: Context){
        executorService.execute {
            data.postValue(DatabaseSingleton.getInstance(context)
                .database.eventsDao().getAll().toMutableList()) }
    }

    fun getAllEvents(context: Context) = CompletableFuture.supplyAsync( { DatabaseSingleton.getInstance(context).database.eventsDao().getAll()}, executorService ).get()

    fun checkCustomers(context: Context, event: Event) =
        CompletableFuture.supplyAsync { DatabaseSingleton.getInstance(context)
            .database.customerDao().getAllByEventId(event.idEvent).size!=0 }.get()

    fun dropEvents(context: Context, events: List<Event>){
        executorService.execute {
            DatabaseSingleton.getInstance(context)
                .database.eventsDao().dropEvent(*events.toTypedArray())
            val list = data.value
            list?.let {
                data.postValue(it.filterNot { x -> x in events }.toMutableList())
            }
        }
    }

    fun updateEvent(context: Context, events: List<Event>){
        executorService.execute {
            DatabaseSingleton.getInstance(context)
                .database.eventsDao().editEvent(*events.toTypedArray())
            val list = data.value
            events.forEach {
                list?.set(list.indexOf(it), it)
            }
            data.postValue(data.value)
        }
    }
}