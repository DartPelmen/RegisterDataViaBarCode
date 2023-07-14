package edu.ivankuznetsov.registerdataviabarcode.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
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

class DataListFragment : Fragment() {
    private lateinit var binding: FragmentDataListBinding
    private lateinit var controller: NavController
    private lateinit var dataModel: CustomerViewModel
    private lateinit var adapter: CustomersListAdapter
    var clickedNum = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = findNavController()
        dataModel = requireActivity().viewModels<CustomerViewModel>().value
        adapter = CustomersListAdapter()
        dataModel.data.observe(requireActivity()){
            val productDiffUtilCallback =
                CustomersDiffUtil(adapter.getCustomers(), it)
            val productDiffResult =
                DiffUtil.calculateDiff(productDiffUtilCallback)
            adapter.setCustomers(it)
            productDiffResult.dispatchUpdatesTo(adapter)
        }
        dataModel.getAll(requireActivity().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDataListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dataList.layoutManager = LinearLayoutManager(requireActivity())
//        binding.dataList.addItemDecoration(DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL))
        binding.dataList.adapter = adapter
        binding.addDataButton.setOnClickListener {
            val action = DataListFragmentDirections.actionDataListFragmentToScannerFragment()
            controller.navigate(action)
        }

        binding.toExcelButton.setOnClickListener {
            dataModel.data.value?.let { value ->
                val workbook = HSSFWorkbook()
                val firstSheet = workbook.createSheet("Sheet No 1")
                val headers = firstSheet.createRow(0)
                val firstName = headers.createCell(1)
                val patronymic = headers.createCell(2)
                val lastName = headers.createCell(0)
                val phone = headers.createCell(3)
                lastName.setCellValue("Фамилия")
                firstName.setCellValue("Имя")
                patronymic.setCellValue("Отчество")
                phone.setCellValue("Телефон")
                phone.setCellValue("Офис")
                phone.setCellValue("Должность")

                for (i in 1 until  value.size){
                    val row = firstSheet.createRow(i)
                    val dataFirstName = row.createCell(1)
                    val dataPatronymic = row.createCell(2)
                    val dataLastName = row.createCell(0)
                    val dataPhone = row.createCell(3)
                    dataFirstName.setCellValue(value[i].fname)
                    dataPatronymic.setCellValue(value[i].sname)
                    dataLastName.setCellValue(value[i].lname)
                    dataPhone.setCellValue(value[i].phone)
                    dataPhone.setCellValue(value[i].office)
                    dataPhone.setCellValue(value[i].rank)
                }
                var fos: FileOutputStream? = null
                val strPath = Environment.getExternalStorageDirectory().toString()
                val file = File(strPath, "Documents/sample ${LocalDateTime.now().toEpochSecond(
                    ZoneOffset.UTC)}.xls")
                try {


                    fos = FileOutputStream(file)
                    workbook.write(fos)
                    clickedNum++
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
                    clickedNum++
                    File(strPath).listFiles()?.forEach { Log.d("TAG", it.absolutePath) }
                    val intentShareFile = Intent(Intent.ACTION_SEND)

                    if (file.exists()) {
                        intentShareFile.type = "application/vnd.ms-excel"
                        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                            FileProvider.getUriForFile(requireContext(), "edu.ivankuznetsov.registerdataviabarcode.provider", file))
                        intentShareFile.putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Sharing File..."
                        )
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
                        startActivity(Intent.createChooser(intentShareFile, "Share File"))
                    }
                }
            }
            }

    }
}