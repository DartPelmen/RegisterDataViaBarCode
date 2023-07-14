package edu.ivankuznetsov.registerdataviabarcode.util

import androidx.recyclerview.widget.DiffUtil
import edu.ivankuznetsov.registerdataviabarcode.database.entity.Event

class EventsDiffUtil(private val oldList: List<Event>, private val newList: List<Event>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    //Проверяет, тот же ли элемент остался в RV. Если да, то вызывается areContentsTheSame, иначе уже можно менять элемент списка
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].idEvent == newList[newItemPosition].idEvent
    }

    //Проверяет, изменилось ли что-то в элементе, если да, то можно менять элемент.
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}