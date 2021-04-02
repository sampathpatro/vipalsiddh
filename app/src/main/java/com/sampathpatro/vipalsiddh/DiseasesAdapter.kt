package com.sampathpatro.vipalsiddh

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiseasesAdapter(private val diseasesList: List<Disease>, private val onClickListener: onDiseaseClickListener) :
    RecyclerView.Adapter<DiseasesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val diseaseName = itemView.findViewById(R.id.diseaseName) as TextView
        private val disSeriousness = itemView.findViewById(R.id.imgSeriousness) as ImageView
        private val disSpread = itemView.findViewById(R.id.txtSpread) as TextView
        @SuppressLint("SetTextI18n")
        fun initialize(item: Disease, action: onDiseaseClickListener){
            if (item.name.length > 20){
                diseaseName.textSize = 20F
            }
            diseaseName.text = item.name
            when (item.seriousness){
                1 -> disSeriousness.setImageResource(R.drawable.safe_indic_15)
                2 -> disSeriousness.setImageResource(R.drawable.safe_indic_25)
                3 -> disSeriousness.setImageResource(R.drawable.safe_indic_35)
                4 -> disSeriousness.setImageResource(R.drawable.safe_indic_45)
                5 -> disSeriousness.setImageResource(R.drawable.safe_indic)
            }

            when (item.spread){
                1 -> {
                    disSpread.text = "Very Rare"
                }
                2 -> {
                    disSpread.text = "Fairly Rare"
                }
                3 -> {
                    disSpread.text = "Rare"
                }
                4 -> {
                    disSpread.text = "Common"
                }
                5 -> {
                    disSpread.text = "Very Common"
                }
            }

            itemView.setOnClickListener{
                action.onDiseaseClick(item, adapterPosition)
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.disease_row_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = diseasesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initialize(diseasesList[position], onClickListener)
    }

    interface onDiseaseClickListener {
        fun onDiseaseClick(item: Disease, position: Int){

        }
    }

}