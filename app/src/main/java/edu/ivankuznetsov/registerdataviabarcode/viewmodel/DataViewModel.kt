package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel

class DataViewModel: ViewModel() {
    val data = MutableLiveData<List<DataModel>>()
}