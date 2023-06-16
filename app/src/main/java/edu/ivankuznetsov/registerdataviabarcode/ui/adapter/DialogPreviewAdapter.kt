package edu.ivankuznetsov.registerdataviabarcode.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ivankuznetsov.registerdataviabarcode.database.entity.DataModel
import edu.ivankuznetsov.registerdataviabarcode.databinding.DialogListItemBinding

class DialogPreviewAdapter : RecyclerView.Adapter<DialogPreviewAdapter.ViewHolder>() {
    private var data: MutableList<DataModel> = mutableListOf()

    /**
     * Занимается организацией отображения ячейки списка
     * */
    inner class ViewHolder(val binding: DialogListItemBinding) : RecyclerView.ViewHolder(binding.root)
    /**
     * Создаем "отображатель"
     * */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(
        DialogListItemBinding.inflate(
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

        holder.binding.lnameField.text = data[position].lname
        holder.binding.fnameField.text = data[position].fname
        holder.binding.snameField.text = data[position].sname
        holder.binding.phoneField.text = data[position].phone
        holder.binding.officeField.text = data[position].office
        holder.binding.rankField.text = data[position].rank

//        holder.itemView.setOnClickListener {
//            val intent = Intent(holder.itemView.context,ConcreteCameraActivity::class.java)
//            intent.putExtra("title",cameras[position].title)
//            intent.putExtra("url",cameras[position].url)
//            holder.itemView.context.startActivity(intent)
//        }
    }

    /**
     * Задает список. Почти не используется.
     * */
    fun setCameras(data: MutableList<DataModel>){
        this.data = data
    }

    fun getData():MutableList<DataModel> = data
}