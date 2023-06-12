package edu.ivankuznetsov.registerdataviabarcode.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.ivankuznetsov.registerdataviabarcode.databinding.FragmentBarCodeInfoDialogListDialogBinding
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.BarCodeViewModel


class BarCodeInfoDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBarCodeInfoDialogListDialogBinding
    private lateinit var barcodeVM: BarCodeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarCodeInfoDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding.button.setOnClickListener {
                this.dismiss()
            }
        barcodeVM = requireActivity().viewModels<BarCodeViewModel>().value
        Toast.makeText(requireContext(), "SIZE START IS ${barcodeVM.getCodes()?.size}", Toast.LENGTH_SHORT).show()
        barcodeVM.barcodes.observe(requireActivity()){
            Toast.makeText(requireContext(),"SIZE IS ${it.size}",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        barcodeVM.switchBind()
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        barcodeVM.switchBind()
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()

    }

}