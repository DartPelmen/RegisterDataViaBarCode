package edu.ivankuznetsov.registerdataviabarcode.ui.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import edu.ivankuznetsov.registerdataviabarcode.databinding.ActivityMainBinding
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.CustomerViewModel

class CustomersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val customersViewModel:CustomerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.extras?.getString("eventId")?.let {uuid ->
            customersViewModel.setCurrentCustomer(uuid, this)
        }
        val navController =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id)
                ?.findNavController()
        //привязка навигации между экранами к bottom navigation.
        navController?.let {
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        }
    }
    companion object {
        val TAG = CustomersActivity::class.java.simpleName
    }
}