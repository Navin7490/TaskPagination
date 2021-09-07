package com.example.taskpagination.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpagination.R
import com.example.taskpagination.`interface`.Deleted
import com.example.taskpagination.model.ModelPagination
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdapterPagination(
    val list: ArrayList<ModelPagination>,
    private val listner: Deleted<ModelPagination>
) : RecyclerView.Adapter<AdapterPagination.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_pagination, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == list.size - 1) {
            listner.didScrollEnd(position)
        }
        with(holder) {

            dataBind(list[position])

            itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(itemView.context).setTitle("Do you want remove")
                    .setPositiveButton("yes") { dialog, whitch ->

                        listner.onDeletedItem(list[position])
                        notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    .setNegativeButton("no") { dialog, whitch ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
        }
    }

    fun updateDataSource(modelPagination: List<ModelPagination>) {
        if (!list.containsAll(modelPagination)) {
            list.addAll(modelPagination)
            notifyDataSetChanged()
        }


    }

    fun updateDatReal(modelPagination: List<ModelPagination>) {
        list.clear()
        list.addAll(modelPagination)
        notifyDataSetChanged()
    }

    fun insertNewData(modelPagination: ModelPagination) {

        if (!list.containsAll(listOf(modelPagination))) {
            list.add(list.size, modelPagination)
            notifyItemInserted(list.size)
            notifyDataSetChanged()
        }


    }

    fun updateData(modelPagination: ModelPagination) {
        val index = list.indexOfLast { it.id == modelPagination.id }

        if (index > -1) {
            list[index] = modelPagination
            notifyItemChanged(index)
            notifyDataSetChanged()
        }


    }

    fun removeData(id: String) {
        val index = list.indexOfFirst { it.id == id }
        if (index > -1) {
            list.removeAt(index)
            notifyItemRemoved(index)
            notifyDataSetChanged()

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        val textView = itemView.findViewById<TextView>(R.id.textView)
        fun dataBind(modelPagination: ModelPagination) {
            textView.text = modelPagination.number
        }
    }

}