package edu.ivankuznetsov.registerdataviabarcode.util

import com.google.mlkit.vision.barcode.common.Barcode
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object JsonConverter {

    fun barCodeValuesToData(barcodes: List<Barcode>): MutableList<Customer>?{
        val resultList = mutableListOf<Customer>()
        return try {
            barcodes.forEach {
                it.rawValue?.let {
                        value ->
                    val data = Json.decodeFromString<Customer>(value)
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