package edu.ivankuznetsov.registerdataviabarcode.util

import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel

object JsonConverter {
    private val gson = Gson()

    fun dataModelToJson(data: DataModel): String {
        return gson.toJson(data)
    }

    fun jsonToDataModel(json: String): DataModel? {
        return gson.fromJson(json, DataModel::class.java)
    }

    fun barCodeValuesToData(barcodes: List<Barcode>): List<DataModel>{
        val resultList = mutableListOf<DataModel>()
        barcodes.forEach {
            it.rawValue?.let { value -> jsonToDataModel(value)?.let { data -> resultList += data } }
        }
        return resultList
    }
}