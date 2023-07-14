package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ivankuznetsov.registerdataviabarcode.database.DatabaseSingleton
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import java.util.concurrent.Executors

class EventsViewModel: ViewModel() {
    private val executorService = Executors.newSingleThreadExecutor()
    val data = MutableLiveData<MutableList<Event>>()
    fun addEvents(context: Context, events: List<Event>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.eventsDao().addEvent(*events.toTypedArray())
            data.value?.addAll(events)
            data.postValue(data.value)
        }
    }
    fun contains(event: Event): Boolean = data.value?.stream()?.anyMatch { x -> x.idEvent == event.idEvent } ?: false


    fun getAll(context: Context){
        executorService.execute {
            data.postValue(DatabaseSingleton.getInstance(context).database.eventsDao().getAll().toMutableList()) }
    }


    fun dropEvents(context: Context, events: List<Event>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.eventsDao().dropEvent(*events.toTypedArray())
            val list = data.value
            list?.let {
                data.postValue(it.filterNot { x -> x in events }.toMutableList())
            }
        }
    }

    fun updateEvent(context: Context, events: List<Event>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.eventsDao().editEvent(*events.toTypedArray())
            val list = data.value
            events.forEach {
                list?.set(list.indexOf(it), it)
            }
            data.postValue(data.value)
        }
    }
}