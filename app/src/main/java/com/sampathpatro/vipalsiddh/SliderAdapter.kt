package com.sampathpatro.vipalsiddh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SliderAdapter(
    private val headingsList: ArrayList<String>,
    private val imageList: ArrayList<Int>,
    private val descList: ArrayList<String>,
    private val buttonClickListener: OnButtonClick
) : RecyclerView.Adapter<SliderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val headingTxt = itemView.findViewById(R.id.sliderTitleTxt) as TextView
        private val slideImg = itemView.findViewById(R.id.sliderImg) as ImageView
        private val descImg = itemView.findViewById(R.id.sliderDescTxt) as TextView
        private val startedBtn = itemView.findViewById(R.id.started_btn) as Button

        fun intialize (hText: String, slideImgId: Int, dText: String, action: OnButtonClick){

            if (hText == "Nearby Hospitals"){
                startedBtn.visibility = View.VISIBLE
                startedBtn.setOnClickListener {
                    action.buttonClick(hText, adapterPosition)
                }
            }

            headingTxt.text = hText
            slideImg.setImageResource(slideImgId)
            descImg.text = dText
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
            .inflate(R.layout.slide_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.intialize(headingsList[position], imageList[position], descList[position], buttonClickListener)
    }

    override fun getItemCount(): Int {
        return headingsList.size
    }

    interface OnButtonClick{
        fun buttonClick(heading: String, position: Int)
    }

}