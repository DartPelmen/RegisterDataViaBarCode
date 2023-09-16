package edu.ivankuznetsov.registerdataviabarcode.util

import com.google.mlkit.vision.barcode.common.Barcode
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

object JsonConverter {

    fun barCodeValuesToData(barcodes: List<Barcode>): MutableList<Customer>?{
        val resultList = mutableListOf<Customer>()
        return try {
            barcodes.forEach {
                it.rawValue?.let {
                        value ->
                    val data = Json.decodeFromString<Customer>(value)
                    data.date = LocalDateTime.now() //Заменить (добавить поле дата создания кода и поле дата сканирования кода)
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