package edu.ivankuznetsov.registerdataviabarcode.database.dao

import android.provider.ContactsContract.Data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface DataModelDao {

    @Insert
    fun addData(vararg data: DataModel)
    @Update
    fun editData(vararg data: DataModel)
    @Delete
    fun dropData(vararg data: DataModel)
    @Query("SELECT * FROM dataModel")
    fun getAll(): List<DataModel>
    @Query("SELECT * FROM dataModel WHERE uuid = :uuid")
    fun getByUUID(uuid: UUID): DataModel?
}