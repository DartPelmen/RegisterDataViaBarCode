package edu.ivankuznetsov.registerdataviabarcode.viewmodel

import android.content.Context
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutionException

class CameraXViewModel(private val context: WeakReference<Context>) : ViewModel() {

    private lateinit var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>
    val processCameraProvider: LiveData<ProcessCameraProvider>
        get() {
            context.get()?.let {
                if (!this::cameraProviderLiveData.isInitialized) {
                    cameraProviderLiveData = MutableLiveData()
                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(it)
                    cameraProviderFuture.addListener(
                        {
                            try {
                                cameraProviderLiveData.setValue(cameraProviderFuture.get())
                            } catch (e: ExecutionException) {
                                Log.e(TAG, "Unhandled exception", e)
                            }
                        },
                        ContextCompat.getMainExecutor(it)
                    )
                }
            }
            return cameraProviderLiveData
        }

    companion object {
        private val TAG = CameraXViewModel::class.java.simpleName
    }
}