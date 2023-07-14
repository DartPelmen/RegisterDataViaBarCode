package edu.ivankuznetsov.registerdataviabarcode.ui.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ivankuznetsov.registerdataviabarcode.R
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import edu.ivankuznetsov.registerdataviabarcode.databinding.ActivityEventsBinding
import edu.ivankuznetsov.registerdataviabarcode.ui.adapter.EventsAdapter
import edu.ivankuznetsov.registerdataviabarcode.util.CustomersDiffUtil
import edu.ivankuznetsov.registerdataviabarcode.util.EventsDiffUtil
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.EventsViewModel
import java.time.LocalDateTime
import java.util.UUID

class EventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventsBinding
    private val eventsViewModel: EventsViewModel by viewModels()
    private val cameraContract = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            binding = ActivityEventsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val adapter = EventsAdapter()
            eventsViewModel.data.observe(this){
                val productDiffUtilCallback =
                    EventsDiffUtil(adapter.getEvents(), it)
                val productDiffResult =
                    DiffUtil.calculateDiff(productDiffUtilCallback)
                adapter.setEvents(it)
                productDiffResult.dispatchUpdatesTo(adapter)
            }
            eventsViewModel.getAll(this)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
            binding.floatingActionButton.setOnClickListener {
                eventsViewModel.addEvents(this, listOf(Event(UUID.randomUUID(),UUID.randomUUID().toString(), LocalDateTime.now(), LocalDateTime.now().plusDays(1))))
            }
        }
        else {
            Log.e(CustomersActivity.TAG, "no camera permission")
            Toast.makeText(this,"no camera permission", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraContract.launch(Manifest.permission.CAMERA)

    }
}