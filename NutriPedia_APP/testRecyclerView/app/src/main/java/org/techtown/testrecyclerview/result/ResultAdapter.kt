package org.techtown.testrecyclerview.result

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorSpace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import org.techtown.testrecyclerview.R
import org.techtown.testrecyclerview.result.ResultSearchActivity

class ResultAdapter(val context: Context, val foodResult: ArrayList<FoodResult>) :
    RecyclerView.Adapter<ResultAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.result_rv_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //holder.bind(foodList[position], context)
        val item = foodResult[position]

        holder.itemView.setOnClickListener {
            if (itemClickListner != null) {
                itemClickListner!!.onClick(it,position)
            }

        }
        holder.apply {
            bind(item,context)
        }
    }
    override fun getItemCount(): Int {
        return foodResult.size
    }

    interface OnItemClickListner {
        fun onClick(v: View, position: Int)
    }
    private var itemClickListner: OnItemClickListner? = null

    fun setItemClickListner(itemClickListner: OnItemClickListner) {
        this.itemClickListner = itemClickListner
    }




    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<CircleImageView>(R.id.smallIv)
        fun bind (food: FoodResult, context: Context) {
            /* 나머지 TextView와 String 데이터를 연결한다. */
            if (food.uri != null && food.uri != "null".toUri()) { // uri 있는거
                image.setImageURI(food.uri)
                image.setOnClickListener {
                }
            } else if (food.check){ // 마지막인거 , context = cameraResult
                image.setImageResource(R.drawable.result_item_border)
                image.setCircleBackgroundColorResource(R.color.black)
                image.setOnClickListener {
                }
            } else { // 중간에 있는데 추가해서 생긴거
                image.setImageResource(R.drawable.ic_result_add)
                image.setOnClickListener {
                    val intent = Intent(context, ResultSearchActivity::class.java)
                    startActivity(context,intent,null)
                }
            }

        }
    }

}