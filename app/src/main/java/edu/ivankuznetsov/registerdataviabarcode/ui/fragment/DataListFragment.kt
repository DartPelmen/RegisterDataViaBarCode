package edu.ivankuznetsov.registerdataviabarcode.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.databinding.FragmentDataListBinding
import edu.ivankuznetsov.registerdataviabarcode.ui.adapter.CustomersListAdapter
import edu.ivankuznetsov.registerdataviabarcode.util.CustomersDiffUtil
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.CustomerViewModel
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DataListFragment : Fragment() {
    private lateinit var binding: FragmentDataListBinding
    private lateinit var controller: NavController
    private lateinit var dataModel: CustomerViewModel
    private lateinit var adapter: CustomersListAdapter
    var eventId: String? = null
    var eventName:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = findNavController()
        dataModel = requireActivity().viewModels<CustomerViewModel>().value
        adapter = CustomersListAdapter()

        dataModel.data.observe(requireActivity()) {
            Log.d("CUSTOMERS", "OBSERVING CUSTOMERS")
            val productDiffUtilCallback =
                CustomersDiffUtil(adapter.getCustomers(), it)
            val productDiffResult =
                DiffUtil.calculateDiff(productDiffUtilCallback)
            adapter.setCustomers(it)
            productDiffResult.dispatchUpdatesTo(adapter)
        }
        eventName = requireActivity().intent.getStringExtra("eventName")
        eventId = requireActivity().intent.getStringExtra("eventId")
        eventId?.let {
            dataModel.setCurrentCustomer(it, requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dataList.layoutManager = LinearLayoutManager(requireActivity())
//      binding.dataList.addItemDecoration(DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL))
        binding.dataList.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.bindingAdapterPosition
                dataModel.dropData(requireContext(), listOf(adapter.getCustomers()[position]))
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.dataList)
        binding.toExcelButton.setOnClickListener {
            dataModel.data.value?.let { value ->
                exportToExcel(value)
            }
        }
    }

    private fun exportToExcel(customerList: List<Customer>) {
        val workbook = fillWorkBookData(customerList, createWorkBookWithHeader())
        val excelFile = saveExcelFile(workbook)
        shareExcelFile(excelFile)
    }


    private fun createWorkBookWithHeader(): HSSFWorkbook {
        val workbook = HSSFWorkbook()
        val firstSheet = workbook.createSheet("Sheet No 1")
        val headers = firstSheet.createRow(0)
        val firstName = headers.createCell(0)
        val patronymic = headers.createCell(1)
        val lastName = headers.createCell(2)
        val phone = headers.createCell(3)
        val rank = headers.createCell(4)
        val office = headers.createCell(5)
        val scanDate = headers.createCell(6)
        lastName.setCellValue("Фамилия")
        firstName.setCellValue("Имя")
        patronymic.setCellValue("Отчество")
        phone.setCellValue("Телефон")
        rank.setCellValue("Должность")
        office.setCellValue("Подразделение")
        scanDate.setCellValue("Дата сканирования")
        return workbook
    }

    private fun fillWorkBookData(
        customerList: List<Customer>,
        workbook: HSSFWorkbook
    ): HSSFWorkbook {
        for (i in customerList.indices) {
            val row = workbook.getSheetAt(0).createRow(i+1)
            val firstName = row.createCell(0)
            val patronymic = row.createCell(1)
            val lastName = row.createCell(2)
            val phone = row.createCell(3)
            val rank = row.createCell(4)
            val office = row.createCell(5)
            val scanDate = row.createCell(6)
            firstName.setCellValue(customerList[i].fname)
            patronymic.setCellValue(customerList[i].sname)
            lastName.setCellValue(customerList[i].lname)
            phone.setCellValue(customerList[i].phone)
            office.setCellValue(customerList[i].office)
            rank.setCellValue(customerList[i].rank)
            scanDate.setCellValue(customerList[i].date.toString())
        }
        return workbook
    }

    private fun saveExcelFile(workbook: HSSFWorkbook): File {
        val fileName = eventName ?: "sample"
        var fos: FileOutputStream? = null
        val pathPrefix = Environment.getExternalStorageDirectory().toString()
        val file = File(
            pathPrefix, "Documents/$fileName ${
                DateTimeFormatter.ofPattern("dd_MMMM_yyyy HH_mm_ss").format(LocalDateTime.now())
                    
            }.xls"
        )
        try {
            fos = FileOutputStream(file)
            workbook.write(fos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return file
    }

    private fun shareExcelFile(file: File) {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        if (file.exists()) {
            intentShareFile.type = "application/vnd.ms-excel"
            intentShareFile.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    requireContext(),
                    "edu.ivankuznetsov.registerdataviabarcode.provider", file
                )
            )
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
            startActivity(Intent.createChooser(intentShareFile, "Share File"))
        }
    }
}