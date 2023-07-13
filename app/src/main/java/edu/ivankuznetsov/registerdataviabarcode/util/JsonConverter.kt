package edu.ivankuznetsov.registerdataviabarcode.util

import android.provider.ContactsContract.Data
import com.google.mlkit.vision.barcode.common.Barcode
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.jvm.Throws

object JsonConverter {

    fun barCodeValuesToData(barcodes: List<Barcode>): MutableList<DataModel>?{
        val resultList = mutableListOf<DataModel>()
        return try {
            barcodes.forEach {
                it.rawValue?.let {
                        value ->
                    val data = Json.decodeFromString<DataModel>(value)
                    resultList += data
                }
            }
            resultList
        } catch(exception: IllegalArgumentException) {
            null
        } catch (exception: SerializationException) {
            null
        }
    }
}