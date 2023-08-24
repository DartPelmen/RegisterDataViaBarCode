package edu.ivankuznetsov.registerdataviabarcode.ui.activity

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import edu.ivankuznetsov.registerdataviabarcode.databinding.ActivityEventsBinding
import edu.ivankuznetsov.registerdataviabarcode.databinding.EventNewDialogBinding
import edu.ivankuznetsov.registerdataviabarcode.ui.adapter.EventsAdapter
import edu.ivankuznetsov.registerdataviabarcode.util.EventsDiffUtil
import edu.ivankuznetsov.registerdataviabarcode.util.showErrorMessage
import edu.ivankuznetsov.registerdataviabarcode.viewmodel.EventsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventsBinding
    private lateinit var addEventDialog: BottomSheetDialog
    private lateinit var addEventDialogBinding: EventNewDialogBinding
    private var startDate: LocalDateTime? = null
    private var endDate: LocalDateTime? = null
    private val eventsViewModel: EventsViewModel by viewModels()
    private val cameraContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { it ->
            if (it) {
                binding = ActivityEventsBinding.inflate(layoutInflater)
                setContentView(binding.root)
                val adapter = EventsAdapter()
                addEventDialog = BottomSheetDialog(this)
                addEventDialogBinding = EventNewDialogBinding.inflate(addEventDialog.layoutInflater)
                addEventDialogBinding.startDateTimeButton.setOnClickListener {
                    val picker = DatePickerDialog(this)
                    picker.setOnDateSetListener { _, year, month, day ->
                        TimePickerDialog(
                            this,
                            { _, hour, minute ->
                                startDate = LocalDateTime.of(year, month+1, day, hour, minute)
                                addEventDialogBinding.startDateTime.text = startDate!!.format(
                                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                                )
                            }, 0, 0, true
                        ).show()
                    }
                    picker.show()
                }
                addEventDialogBinding.endTimeButton.setOnClickListener {
                    val picker = DatePickerDialog(this)
                    picker.setOnDateSetListener { _, year, month, day ->
                        TimePickerDialog(
                            this,
                            { _, hour, minute ->
                                endDate = LocalDateTime.of(year, month+1, day, hour, minute)
                                addEventDialogBinding.endDateTime.text = endDate!!.format(
                                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                                )
                            }, 0, 0, true
                        ).show()
                    }
                    picker.show()
                }
                addEventDialogBinding.addButton.setOnClickListener {
                    addEventDialogBinding.eventTitle.text?.let { eventTitle ->
                        if (eventTitle.isBlank()) {
                            Snackbar.make(
                                this,
                                addEventDialogBinding.addButton,
                                "Укажите название мероприятия!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            startDate?.let { start ->
                                endDate?.let { end ->
                                    if (start.isAfter(end)) {
                                        Snackbar.make(
                                            this,
                                            addEventDialogBinding.addButton,
                                            "Конец мероприятия не может быть раньше начала!",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        if (eventsViewModel.getAllEvents(this).any { x ->
                                                x.title == eventTitle.toString() && (x.startDateTime == startDate) && x.endDateTime == endDate
                                            }) {
                                            Snackbar.make(
                                                this,
                                                addEventDialogBinding.addButton,
                                                "Данное мероприятие уже существует!",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            eventsViewModel.addEvents(
                                                this,
                                                listOf(
                                                    Event(
                                                        title = eventTitle.toString(),
                                                        startDateTime = start,
                                                        endDateTime = end
                                                    )
                                                )
                                            )
                                            addEventDialog.dismiss()
                                        }
                                    }
                                } ?: Snackbar.make(
                                    this,
                                    addEventDialogBinding.addButton,
                                    "Укажите время конца мероприятия!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } ?: Snackbar.make(
                                this,
                                addEventDialogBinding.addButton,
                                "Укажите время начала мероприятия!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } ?: Snackbar.make(
                        this,
                        addEventDialogBinding.addButton,
                        "Укажите название мероприятия!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                addEventDialog.setContentView(addEventDialogBinding.root)
                eventsViewModel.getAll(this)
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = adapter

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
                        if (eventsViewModel.checkCustomers(
                                this@EventsActivity,
                                adapter.getEvents()[position]
                            )
                        ) {
                            showErrorMessage(this@EventsActivity, "В мероприятии есть слушатели!", "Сперва удалите участников мероприятия!") { x, _ ->
                                x.dismiss()
                            }
                            adapter.notifyItemChanged(viewHolder.layoutPosition)
                        }
                        else{
                            eventsViewModel.dropEvents(
                                this@EventsActivity,
                                listOf(adapter.getEvents()[position])
                            )
                        }
                    }
                })
                itemTouchHelper.attachToRecyclerView(binding.recyclerView)

                binding.floatingActionButton.setOnClickListener {
                    addEventDialog.show()
                }
                eventsViewModel.data.observe(this) {
                    val productDiffUtilCallback =
                        EventsDiffUtil(adapter.getEvents(), it)
                    val productDiffResult =
                        DiffUtil.calculateDiff(productDiffUtilCallback)
                    adapter.setEvents(it)
                    productDiffResult.dispatchUpdatesTo(adapter)
                }
            } else {
                Log.e(CustomersActivity.TAG, "no camera permission")
                Toast.makeText(this, "no camera permission", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraContract.launch(Manifest.permission.CAMERA)


    }

    companion object{
        private val TAG = EventsActivity::class.java.simpleName
    }
}