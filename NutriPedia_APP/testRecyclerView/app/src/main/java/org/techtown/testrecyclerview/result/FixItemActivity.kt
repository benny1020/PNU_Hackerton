package org.techtown.testrecyclerview.result

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.NumberPicker
import android.widget.Button
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_camera_result.*
import kotlinx.android.synthetic.main.activity_camera_result.view.*
import kotlinx.android.synthetic.main.activity_fix_item.*
import kotlinx.android.synthetic.main.activity_fix_item.addRecyclerView
import kotlinx.android.synthetic.main.activity_fix_item.button
import kotlinx.android.synthetic.main.activity_fix_item.foodTv1
import kotlinx.android.synthetic.main.activity_fix_item.kcalTv
import kotlinx.android.synthetic.main.activity_fix_item.mainIv
import kotlinx.android.synthetic.main.activity_fix_item.nutri1_Tv
import kotlinx.android.synthetic.main.activity_fix_item.nutri2_Tv
import kotlinx.android.synthetic.main.activity_fix_item.nutri3_Tv
import kotlinx.android.synthetic.main.activity_fix_item.totalCal
import kotlinx.android.synthetic.main.activity_search_result.*
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.FragmentOne
import org.techtown.testrecyclerview.MainActivity
import org.techtown.testrecyclerview.R
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class FixItemActivity : AppCompatActivity() {

    val fixArray = ArrayList<FoodResult>()
    var pos = 0
    var mt: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fix_item)
        var dbHelper = DBHelper(this, "food_nutri.db", null, 1)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = "----년--월--일"                 //디비 날짜 불러오기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFF")))

        mt = FragmentOne.cardList[MainActivity.pos].mealTime

        fixArray.clear()
        for (i in 0 until FragmentOne.resultList.size) {
            if (FragmentOne.resultList[i].mealTime == mt) {
                var uri : Uri?
                if (FragmentOne.resultList[i].picture != null) {
                    uri = Uri.parse(FragmentOne.resultList[i].picture)
                } else {
                    uri = null
                }
                fixArray.add(
                    FoodResult(
                        FragmentOne.resultList[i].foodName,
                        FragmentOne.resultList[i].calorie,
                        FragmentOne.resultList[i].amount,
                        FragmentOne.resultList[i].nutri1,
                        FragmentOne.resultList[i].nutri2,
                        FragmentOne.resultList[i].nutri3,
                        uri,
                        true
                    )
                )
            }
        }




        if(fixArray.size != 0) {
            if (fixArray[0].uri != null) {
                mainIv.setImageURI(fixArray[0].uri)
            } else {
                mainIv.setImageResource(R.drawable.ic_no_image)
            }
            foodTv1.text = fixArray[0].foodName
            kcalTv.text = fixArray[0].calorie.toString() + "Kcal"
            nutri1_Tv.text = fixArray[0].nutri1.toString() + "Kcal"
            nutri2_Tv.text = fixArray[0].nutri2.toString() + "Kcal"
            nutri3_Tv.text = fixArray[0].nutri3.toString() + "Kcal"
            var total : Double = 0.0
            for (i in 0 until fixArray.size) {
                total += fixArray[i].calorie
            }
            totalCal.text = total.toString() + "Kcal"
            _amount.text = dbHelper.getColValue2(5,  fixArray[0].foodName, mt)
        }

        val mAdapter = ResultAdapter(this, fixArray)
        addRecyclerView.adapter = mAdapter
        addRecyclerView.isHorizontalScrollBarEnabled = false
        var adapter = addRecyclerView.adapter
        addRecyclerView.invalidate()
        adapter!!.notifyDataSetChanged()
        val lm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        addRecyclerView.layoutManager = lm
        addRecyclerView.setHasFixedSize(true)

        addRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

                try {
                    val child = addRecyclerView.findChildViewUnder(e.x, e.y)
                    val position : Int = addRecyclerView.getChildAdapterPosition(child!!)
                    var uri = fixArray[position].uri
                    if ( uri != null && uri != "null".toUri()) {
                        Log.e("uri?","${uri}")
                        pos = position

                        mainIv.setImageURI(fixArray[position].uri)
                        foodTv1.text = fixArray[position].foodName
                        kcalTv.text = fixArray[position].calorie.toString() + "Kcal"
                        nutri1_Tv.text = fixArray[position].nutri1.toString() + "Kcal"
                        nutri2_Tv.text = fixArray[position].nutri2.toString() + "Kcal"
                        nutri3_Tv.text = fixArray[position].nutri3.toString() + "Kcal"
                        var total : Double = 0.0
                        for (i in 0 until fixArray.size) {
                            total += fixArray[i].calorie
                        }
                        totalCal.text = total.toString() + "Kcal"
                        _amount.text = dbHelper.getColValue2(5,  fixArray[position].foodName, mt)
                    } else
                    {
                        pos = position
                        mainIv.setImageResource(R.drawable.ic_no_image)
                        foodTv1.text =fixArray[position].foodName
                        kcalTv.text = fixArray[position].calorie.toString() + "Kcal"
                        nutri1_Tv.text = fixArray[position].nutri1.toString() + "Kcal"
                        nutri2_Tv.text = fixArray[position].nutri2.toString() + "Kcal"
                        nutri3_Tv.text = fixArray[position].nutri3.toString() + "Kcal"
                        var total : Double = 0.0
                        for (i in 0 until fixArray.size) {
                            total += fixArray[i].calorie
                        }
                        totalCal.text = total.toString() + "Kcal"
                        _amount.text = dbHelper.getColValue2(5,  fixArray[position].foodName, mt)
                    }
                } catch (e : NullPointerException) {

                }
                return false
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })
        button.setOnClickListener {

            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        var adapter = addRecyclerView.adapter
        addRecyclerView.invalidate()
        adapter!!.notifyDataSetChanged()

        if(fixArray.size != 0) {
            if (fixArray[0].uri != null) {
                mainIv.setImageURI(fixArray[0].uri)
            } else {
                mainIv.setImageResource(R.drawable.ic_no_image)
            }


            foodTv1.text = fixArray[0].foodName
            kcalTv.text = fixArray[0].calorie.toString() + "Kcal"
            nutri1_Tv.text = fixArray[0].nutri1.toString() + "Kcal"
            nutri2_Tv.text = fixArray[0].nutri2.toString() + "Kcal"
            nutri3_Tv.text = fixArray[0].nutri3.toString() + "Kcal"
            var total : Double = 0.0
            for (i in 0 until fixArray.size) {
                total += fixArray[i].calorie
            }
            totalCal.text = total.toString() + "Kcal"
        }
    }




}