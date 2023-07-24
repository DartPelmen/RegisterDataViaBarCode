package edu.ivankuznetsov.registerdataviabarcode.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import edu.ivankuznetsov.registerdataviabarcode.R
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Customer
import edu.ivankuznetsov.registerdataviabarcode.databinding.FragmentBarCodeInfoDialogListDialogBinding
import edu.ivankuznetsov.registerdataviabarcode.databinding.FragmentScannerBinding
import edu.ivankuznetsov.registerdataviabarcode.ui.adapter.DialogPreviewAdapter
import edu.ivankuznetsov.registerdataviabarcode.util.CustomersDiffUtil
import edu.ivankuznetsov.registerdataviabarcode.util.JsonConverter
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.BarCodeViewModel
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.CameraXViewModel
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.CustomerViewModel
import java.lang.ref.WeakReference
import java.util.concurrent.Executors


class ScannerFragment : Fragment() {
    private lateinit var barCodeViewModel: BarCodeViewModel
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    private lateinit var previewUseCase: Preview
    private lateinit var analysisUseCase: ImageAnalysis
    private lateinit var viewModel: CameraXViewModel
    private lateinit var barCodeDialog:BottomSheetDialog
    private lateinit var binding: FragmentScannerBinding
    private lateinit var dialogBinding: FragmentBarCodeInfoDialogListDialogBinding
    private lateinit var dataModel: CustomerViewModel
    private lateinit var adapter: DialogPreviewAdapter
    private val analysisExecutor = Executors.newSingleThreadExecutor()

    private val simpleItemTouchCallback = object : ItemTouchHelper
        .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val position = viewHolder.bindingAdapterPosition
            val list = mutableListOf<Customer>()
            list.addAll(adapter.getData())
            list.remove(adapter.getDataByPosition(position))
            val productDiffUtilCallback =
                CustomersDiffUtil(adapter.getData(), list)
            val productDiffResult =
                DiffUtil.calculateDiff(productDiffUtilCallback)
            adapter.setCameras(list)
            productDiffResult.dispatchUpdatesTo(adapter)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barCodeViewModel = requireActivity().viewModels<BarCodeViewModel>().value
        dataModel = requireActivity().viewModels<CustomerViewModel>().value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        adapter = DialogPreviewAdapter()
        binding = FragmentScannerBinding.inflate(inflater,container,false)
        barCodeDialog = BottomSheetDialog(requireActivity())
        dialogBinding = FragmentBarCodeInfoDialogListDialogBinding.inflate(barCodeDialog.layoutInflater)
        dialogBinding.dialogRV.layoutManager = LinearLayoutManager(requireActivity())
//        dialogBinding.dialogRV.addItemDecoration(
//            DividerItemDecoration(requireActivity(),
//                DividerItemDecoration.VERTICAL)
//        )
        dialogBinding.dialogRV.adapter = adapter

        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(dialogBinding.dialogRV)

        barCodeDialog.setContentView(dialogBinding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        viewModel = CameraXViewModel(WeakReference(requireActivity()))

    }

    override fun onStart() {
        super.onStart()
        viewModel.processCameraProvider.observe(requireActivity()) { provider: ProcessCameraProvider? ->
            provider?.let {
                cameraProvider = it
                bindPreviewUseCase()
                bindAnalyseUseCase()
            }
        }
        barCodeViewModel.shouldBind.observe(this){
            if(it){
                bindAnalyseUseCase()
            }
        }
    }
    private fun bindPreviewUseCase() {

        if (this::previewUseCase.isInitialized) {
            cameraProvider.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

//Attach the PreviewView surface provider to the preview use case.
        previewUseCase.setSurfaceProvider(binding.previewView.surfaceProvider)

        try {
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun bindAnalyseUseCase() {
        // Note that if you know which format of barcode your app is dealing with,
        // detection will be faster
        if(this::analysisUseCase.isInitialized){
            cameraProvider.unbind(analysisUseCase)
        }
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)
        analysisUseCase = ImageAnalysis.Builder()
            .setResolutionSelector(ResolutionSelector
                .Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
            .build())
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        // Initialize our background executor

        analysisUseCase.setAnalyzer(
            analysisExecutor
        ) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }

        try {
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun  processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        barcodeScanner.process(InputImage.fromMediaImage(imageProxy.image!!,
            imageProxy.imageInfo.rotationDegrees))
            .addOnSuccessListener { barcodes ->
                barCodeViewModel.setCodes(barcodes)
                if (barcodes.size > 0) {
                    requireActivity().runOnUiThread {
                        cameraProvider.unbind(analysisUseCase)
                    }
                    val list = JsonConverter.barCodeValuesToData(barcodes)
                    list?.let { models ->
                        val productDiffUtilCallback = CustomersDiffUtil(adapter.getData(), models)
                        val productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback)
                        adapter.setCameras(models)
                        productDiffResult.dispatchUpdatesTo(adapter)
                        configureAddDialog()
                        barCodeDialog.show()
                        } ?: showErrorMessage("Ошибка сканирования",
                        "Невозможно извлечь информацию из QR-кода")
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message ?: it.toString())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }


    }

    private fun configureAddDialog() {
        dialogBinding.button.setOnClickListener {
            try {
                if(adapter.getData().stream()
                        .anyMatch {x -> dataModel.getAll(requireContext()).contains(x) }) {
                    showErrorMessage("Ошибка добавления данных",
                        "Обнаружены данные, которые уже добавлены в список!")
                } else {
                    dataModel.addData(requireActivity().applicationContext,
                        adapter.getData())
                }
            } catch (ex: Exception) {
                Toast.makeText(requireContext(),"SOMETHING WENT WRONG!",
                    Toast.LENGTH_SHORT).show()
                ex.printStackTrace()
            }
            barCodeDialog.dismiss()
        }
        barCodeDialog.setOnCancelListener {
            requireActivity().runOnUiThread {
                cameraProvider.bindToLifecycle(this,cameraSelector,analysisUseCase)
            }
        }
        barCodeDialog.setOnDismissListener {
            requireActivity().runOnUiThread {
                cameraProvider.bindToLifecycle(this,cameraSelector,analysisUseCase)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "Scan to pause")
    }
    override fun onStop() {
        super.onStop()

    }

    private fun showErrorMessage(title: String, text: String){
        MaterialAlertDialogBuilder(requireContext(),
            R.style.AlertDialogTheme).setTitle(title).setPositiveButton("OK"){ x, _->
            requireActivity().runOnUiThread {
                cameraProvider.bindToLifecycle(this,cameraSelector,analysisUseCase)
            }
            x.dismiss()
        }.setMessage(text).show()
    }

    companion object {
        val TAG = ScannerFragment::class.java.simpleName
    }


}