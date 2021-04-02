package com.sampathpatro.vipalsiddh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class SymptomsAdapter(
    private val symptomsList: ArrayList<String>,
    private var clickListener: OnSymptomClickListener?
) : RecyclerView.Adapter<SymptomsAdapter.ViewHolder>(), Filterable {

    var symptomFilterList: ArrayList<String>

    init {
        symptomFilterList = symptomsList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val symptomName = itemView.findViewById(R.id.symptomsName) as TextView
        private val checkBox = itemView.findViewById(R.id.check_box) as CheckBox
        fun initializeClick(
            item: String,
            action: OnSymptomClickListener?
        ) {
            symptomName.text = item
            checkBox.isChecked = chosenSymptomsList.contains(item)

            checkBox.setOnClickListener {
                when {
                    action != null -> {
                        action.onItemClick(item, adapterPosition)
                    }
                }

            }
            itemView.setOnClickListener {

                when {
                    action != null -> {
                        action.onItemClick(item, adapterPosition)
                        checkBox.isChecked = !checkBox.isChecked
                    }
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return symptomFilterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initializeClick(symptomFilterList[position], clickListener)
    }

    interface OnSymptomClickListener {
        fun onItemClick(item: String, position: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                symptomFilterList = if (charSearch.isEmpty()) {
                    symptomsList
                } else {
                    val resultList = ArrayList<String>()
                    for (row in symptomsList) {
                        if (row.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = symptomFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                symptomFilterList = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }

        }
    }

}