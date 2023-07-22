package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.common.Barcode

class BarCodeViewModel : ViewModel() {
    private val barcodes = MutableLiveData<MutableList<Barcode>>()
    val shouldBind = MutableLiveData(false)
    fun setCodes(codes: MutableList<Barcode>){
        barcodes.postValue(codes)
    }
    fun getCodes() = barcodes.value
    fun switchBind(){
        shouldBind.postValue(!shouldBind.value!!)
    }

    fun dropCode(position: Int) {
        barcodes.value?.removeAt(position)
        barcodes.postValue(barcodes.value)
    }
}