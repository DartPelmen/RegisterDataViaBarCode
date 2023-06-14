package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ivankuznetsov.registerdataviabarcode.database.DatabaseSingleton
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel
import java.util.concurrent.Executors

class DataViewModel: ViewModel() {
    private val executorService = Executors.newSingleThreadExecutor()
    val data = MutableLiveData<MutableList<DataModel>>()
    fun addData(context: Context, dataModel: List<DataModel>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.dataModelDao().addData(*dataModel.toTypedArray())
            data.value?.addAll(dataModel)
            data.postValue(data.value)
        }
    }
    fun getAll(context: Context){
        executorService.execute {
            data.postValue(DatabaseSingleton.getInstance(context).database.dataModelDao().getAll().toMutableList()) }
    }
    fun dropData(context: Context, dataModel: List<DataModel>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.dataModelDao().dropData(*dataModel.toTypedArray())
            val list = data.value
            list?.let {
                data.postValue(it.filterNot { x -> x in dataModel }.toMutableList())
            }
        }
    }

    fun updateData(context: Context, dataModel: List<DataModel>){
        executorService.execute {
            DatabaseSingleton.getInstance(context).database.dataModelDao().editData(*dataModel.toTypedArray())
            val list = data.value
            dataModel.forEach {
                list?.set(list.indexOf(it), it)
            }
            data.postValue(data.value)
        }
    }
}