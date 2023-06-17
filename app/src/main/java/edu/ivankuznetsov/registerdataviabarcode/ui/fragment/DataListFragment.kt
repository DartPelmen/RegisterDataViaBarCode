package edu.ivankuznetsov.registerdataviabarcode.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ivankuznetsov.registerdataviabarcode.databinding.FragmentDataListBinding
import edu.ivankuznetsov.registerdataviabarcode.ui.adapter.DataModelAdapter
import edu.ivankuznetsov.registerdataviabarcode.util.DataModelListDiffUtil
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.DataViewModel

class DataListFragment : Fragment() {
    private lateinit var binding: FragmentDataListBinding
    private lateinit var controller: NavController
    private lateinit var dataModel: DataViewModel
    private lateinit var adapter: DataModelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = findNavController()
        dataModel = requireActivity().viewModels<DataViewModel>().value

        adapter = DataModelAdapter()
        dataModel.data.observe(requireActivity()){


            val productDiffUtilCallback =
                DataModelListDiffUtil(adapter.getData(), it)
            val productDiffResult =
                DiffUtil.calculateDiff(productDiffUtilCallback)
            adapter.setCameras(it)
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
        binding.dataList.addItemDecoration(DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL))
        binding.dataList.adapter = adapter
        binding.addDataButton.setOnClickListener {
            val action = DataListFragmentDirections.actionDataListFragmentToScannerFragment()
            controller.navigate(action)
        }
        binding.toExcelButton.setOnClickListener {

        }
    }
}