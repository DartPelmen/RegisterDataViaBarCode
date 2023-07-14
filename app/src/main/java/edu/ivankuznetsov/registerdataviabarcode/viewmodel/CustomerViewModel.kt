package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ivankuznetsov.registerdataviabarcode.database.DatabaseSingleton
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.Executors

class CustomerViewModel: ViewModel() {
    private val executorService = Executors.newSingleThreadExecutor()
    val data = MutableLiveData<MutableList<Customer>>()
    val currentCustomer = MutableLiveData<Customer>()
    fun addData(context: Context, customer: List<Customer>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.customerDao().addCustomer(*customer.toTypedArray())
            val list = TODO()
            DatabaseSingleton.getInstance(context).database.crossDao().addCrossData()
            data.postValue(data.value)
        }
    }
    fun contains(customer: Customer): Boolean = data.value?.stream()?.anyMatch { x -> x.idCustomer == customer.idCustomer } ?: false

    fun setCurrentCustomer(uuid: String, context: Context){
        executorService.execute {
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