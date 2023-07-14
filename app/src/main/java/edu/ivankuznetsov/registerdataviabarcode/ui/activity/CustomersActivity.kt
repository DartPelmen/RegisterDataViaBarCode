package edu.ivankuznetsov.registerdataviabarcode.ui.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import edu.ivankuznetsov.registerdataviabarcode.databinding.ActivityMainBinding

class CustomersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val cameraContract = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

                val navController =
                supportFragmentManager.findFragmentById(binding.fragmentContainerView.id)
                    ?.findNavController()
            //привязка навигации между экранами к bottom navigation.
            navController?.let {
                NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
            }
        }
        else {
            Log.e(TAG, "no camera permission")
            Toast.makeText(this,"no camera permission", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraContract.launch(Manifest.permission.CAMERA)
    }
    companion object {
        val TAG = CustomersActivity::class.java.simpleName
    }
}