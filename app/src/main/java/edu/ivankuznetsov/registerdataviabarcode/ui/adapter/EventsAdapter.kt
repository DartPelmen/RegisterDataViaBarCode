package edu.ivankuznetsov.registerdataviabarcode.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event
import edu.ivankuznetsov.registerdataviabarcode.databinding.EventItemBinding
import edu.ivankuznetsov.registerdataviabarcode.ui.activity.CustomersActivity
import java.time.format.DateTimeFormatter

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    private var data: MutableList<Event> = mutableListOf()

    /**
     * Занимается организацией отображения ячейки списка
     * */
    inner class ViewHolder(val binding: EventItemBinding) : RecyclerView.ViewHolder(binding.root)
    /**
     * Создаем "отображатель"
     * */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(
        EventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))

    /**
     * Число элементов списка
     * */
    override fun getItemCount(): Int = data.size
    /**
     * Связывает элемент RV и элемент списка
     * */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val start = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(data[position].startDateTime)
        val end = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(data[position].endDateTime)
        holder.binding.eventTitle.text = data[position].title
        holder.binding.eventStartDate.text = "$start - $end"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,CustomersActivity::class.java)
            intent.putExtra("eventId",data[position].idEvent.toString())
            intent.putExtra("eventName", data[position].title)

            holder.itemView.context.startActivity(intent)
        }
    }

    /**
     * Задает список. Почти не используется.
     * */
    fun setEvents(data: MutableList<Event>){
        this.data = data
    }

    fun getEvents():MutableList<Event> = data
}