package com.sampathpatro.vipalsiddh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.chosen_symptoms_item.view.*

class ChosenSymptomsAdapter(private val chosenSymptomsList: MutableList<String>, private val clickListener: OnSymptomClickListener) : RecyclerView.Adapter<ChosenSymptomsAdapter.ViewHolder>(){

    private var removedPosition: Int = 0
    private lateinit var removedItem: String

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val chosenSymptom = itemView.chosenSymptomsName
        fun initialize(item: String, action: OnSymptomClickListener){
            chosenSymptom.text = item

            itemView.setOnClickListener{
                action.onChosenItemClick(item, adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chosen_symptoms_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return chosenSymptomsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initialize(chosenSymptomsList[position], clickListener)
    }

    interface OnSymptomClickListener {
        fun onChosenItemClick(item: String, position: Int)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder){
        removedItem = chosenSymptomsList[viewHolder.adapterPosition]
        removedPosition = viewHolder.adapterPosition
        chosenSymptomsList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

        Snackbar.make(viewHolder.itemView, "$removedItem deleted.", Snackbar.LENGTH_LONG).setAction("UNDO") {
            chosenSymptomsList.add(removedPosition, removedItem)
            notifyItemInserted(removedPosition)
        }.show()
    }

}
