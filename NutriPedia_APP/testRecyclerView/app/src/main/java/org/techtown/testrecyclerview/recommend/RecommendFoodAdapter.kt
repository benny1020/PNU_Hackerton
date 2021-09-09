package org.techtown.testrecyclerview.recommend

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R
import org.techtown.testrecyclerview.search.FoodData

class RecommendFoodAdapter (val context: Context, var foodList: ArrayList<FoodData>) :
    RecyclerView.Adapter<RecommendFoodAdapter.CustomViewHolder>() {
    val dbHelper = DBHelper(context, "food_nutri.db", null, 1)
    lateinit var db : SQLiteDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommend_row_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //holder.bind(foodList[position], context)
        val item = foodList[position]
        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it,position)
        }
        holder.apply {
            bind(item,context)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    interface OnItemClickListner {
        fun onClick(v:View, position: Int)
    }
    private lateinit var itemClickListner: OnItemClickListner

    fun setItemClickListner(itemClickListner: OnItemClickListner) {
        this.itemClickListner = itemClickListner
    }




    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val foodTitle = itemView.findViewById<TextView>(R.id.titleTv)
        val kcalTv = itemView.findViewById<TextView>(R.id.kcalTv)
        val nutri1 = itemView.findViewById<TextView>(R.id.nutri1)
        val nutri2 = itemView.findViewById<TextView>(R.id.nutri2)
        val nutri3 = itemView.findViewById<TextView>(R.id.nutri3)

        val thumbUp = itemView.findViewById<ImageView>(R.id.thumbUpIv)
        val thumbDown = itemView.findViewById<ImageView>(R.id.thumbDownIv)
        var thumbCount = 0
        var thumbUpClicked : Boolean = false
        var thumbDownClicked : Boolean = false

        fun bind (foodData:FoodData, context: Context) {

            /* 나머지 TextView와 String 데이터를 연결한다. */
            foodTitle.text = foodData.foodName
            kcalTv.text = foodData.calorie.toString()+"Kcal | "+foodData.amount.toString()+"g 기준"
            nutri1.text = foodData.nutri1.toString()+"Kcal"
            nutri2.text = foodData.nutri2.toString()+"Kcal"
            nutri3.text = foodData.nutri3.toString()+"Kcal"

            thumbUp.setOnClickListener {
                if (thumbUpClicked == true && thumbDownClicked == false) {
                    thumbCount--
                    thumbUpClicked = false
                    thumbUp.setImageResource(R.drawable.icons8_thumbs_up_24)
                    dbHelper.updatePriorityDown(foodData.foodName)

                } else if (thumbUpClicked == false && thumbDownClicked == false) {
                    thumbCount++
                    thumbUpClicked = true
                    thumbUp.setImageResource(R.drawable.icons8_thumbs_up_filled_24)
                    dbHelper.updatePriorityUp(foodData.foodName)                }
            }
            thumbDown.setOnClickListener {
                if (thumbUpClicked == false && thumbDownClicked == true) {
                    thumbCount++
                    thumbDownClicked = false
                    thumbDown.setImageResource(R.drawable.icons8_thumbs_down_24)
                    dbHelper.updatePriorityUp(foodData.foodName)
                }
                else if (thumbUpClicked == false && thumbDownClicked == false) {
                    thumbCount--
                    thumbDownClicked = true
                    thumbDown.setImageResource(R.drawable.icons8_thumbs_down_filled_24)
                    dbHelper.updatePriorityDown(foodData.foodName)
                }
            }

        }

    }
}