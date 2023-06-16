package edu.ivankuznetsov.registerdataviabarcode.util

import android.provider.ContactsContract.Data
import com.google.mlkit.vision.barcode.common.Barcode
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonConverter {

    fun dataModelToJson(data: DataModel): String {
        return Json.encodeToString(data)
    }

    fun jsonToDataModel(json: String): DataModel {
        return Json.decodeFromString(json)
    }

    fun barCodeValuesToData(barcodes: List<Barcode>): MutableList<DataModel>{
        val resultList = mutableListOf<DataModel>()
        barcodes.forEach {
            it.rawValue?.let {
                    value -> jsonToDataModel(value).let {
                    data -> resultList += data
                    }
            }
        }
        return resultList
    }
}