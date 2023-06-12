package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.common.Barcode

class BarCodeViewModel : ViewModel() {
    val barcodes = MutableLiveData<List<Barcode>>()
    val shouldBind = MutableLiveData(false)
    fun setCodes(codes: List<Barcode>){
        barcodes.postValue(codes)
    }
    fun getCodes() = barcodes.value
    fun switchBind(){
        shouldBind.postValue(!shouldBind.value!!)
    }
}