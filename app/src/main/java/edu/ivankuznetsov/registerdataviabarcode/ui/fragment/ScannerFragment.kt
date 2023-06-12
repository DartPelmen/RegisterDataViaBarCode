package edu.ivankuznetsov.registerdataviabarcode.ui.fragment

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import edu.ivankuznetsov.registerdataviabarcode.databinding.FragmentScannerBinding
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.BarCodeViewModel
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.CameraXViewModel
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

class ScannerFragment : Fragment() {
    private lateinit var barCodeViewModel: BarCodeViewModel
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    private lateinit var previewUseCase: Preview
    private lateinit var analysisUseCase: ImageAnalysis
    private lateinit var viewModel: CameraXViewModel
    private lateinit var barCodeDialogFragment:BottomSheetDialog
    private lateinit var binding: FragmentScannerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barCodeViewModel = requireActivity().viewModels<BarCodeViewModel>().value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScannerBinding.inflate(inflater,container,false)
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
            .setTargetResolution(Size(1080,1920))
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()
        analysisUseCase.setAnalyzer(
            cameraExecutor
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

            val inputImage =
                InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->

                    if (barcodes.size != 0) {
                        requireActivity().runOnUiThread {
                            cameraProvider.unbind(analysisUseCase)
                        }
                    }
                    binding.tvScannedData.text = "SIZE is ${barcodes.size}"
                    barCodeViewModel.setCodes(barcodes)
                    if (barcodes.size > 0) {
                        barCodeViewModel.switchBind()
                        cameraProvider.unbind(analysisUseCase)
                        barCodeDialogFragment.setOnCancelListener {
                            cameraProvider.bindToLifecycle(this,cameraSelector,analysisUseCase)
                        }
                        barCodeDialogFragment.setOnDismissListener {
                            cameraProvider.bindToLifecycle(this,cameraSelector,analysisUseCase)
                        }
                        barCodeDialogFragment.setOnKeyListener { dialog, keyCode, event ->
                            cameraProvider.bindToLifecycle(this@ScannerFragment,cameraSelector,analysisUseCase)
                            true
                        }
                        barCodeDialogFragment.show()


                    }

                }
                .addOnFailureListener {
                    Log.e(TAG, it.message ?: it.toString())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }


    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "Scan to pause")
    }
    companion object {
        val TAG = ScannerFragment::class.java.simpleName
    }

    override fun onStop() {
        super.onStop()

    }
}