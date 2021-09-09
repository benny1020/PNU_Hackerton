package org.techtown.testrecyclerview.recommend

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recommend_list.*
import org.techtown.testrecyclerview.*
import org.techtown.testrecyclerview.search.FoodAdapter
import org.techtown.testrecyclerview.search.FoodData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

class RecommendList : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var db : SQLiteDatabase
    var foodList = arrayListOf<FoodData>()
    lateinit var recyclerView : RecyclerView
    val displayList = ArrayList<FoodData>()
    val mAdapter = RecommendFoodAdapter(this,foodList)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_list)
        dbHelper = DBHelper(this, "food_nutri.db", null, 1)
        db = dbHelper.readableDatabase

//        val remainNutriTv = findViewById<TextView>(R.id.textView7)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        recyclerView = findViewById(R.id.mRecyclerView)

        foodList.clear()
        displayList.clear()
        fillData()

        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        displayList.addAll(foodList)


        mAdapter.setItemClickListner(object : RecommendFoodAdapter.OnItemClickListner {
            override fun onClick(v: View, position: Int) {
                val intent = Intent(applicationContext, RecommendResult::class.java)
                intent.putExtra("name" ,foodList[position].foodName)
                intent.putExtra("calorie" ,foodList[position].calorie)
                intent.putExtra("nutri1" ,foodList[position].nutri1)
                intent.putExtra("nutri2" ,foodList[position].nutri2)
                intent.putExtra("nutri3" ,foodList[position].nutri3)
                startActivityForResult(intent, 101)
                MainActivity.checkChange =1
                finish()
            }

        })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fillData() {
        /////////// FoodCalculator - START

//        println("현재 몸무게, 목표 몸무게, 키 : ${dbHelper.getColValue(0, "user_info").toInt()}, ${dbHelper.getColValue(1, "user_info").toInt()}, ${dbHelper.getColValue(4, "user_info").toInt()}")
        // 1. 하루 권장 칼로리 계산

        var now = LocalDate.now()
        var strnow :String = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        // 부족한 영양소 계산
        var remainCab :Int = dbHelper.getNutriRate(1) - dbHelper.getNutri(7,strnow)
        var remainPro :Int = dbHelper.getNutriRate(2) - dbHelper.getNutri(8,strnow)
        var remainFat :Int = dbHelper.getNutriRate(3) - dbHelper.getNutri(9,strnow)

        val remainRate : Triple<Double, Double, Double> = rateOfScarceNutrient(remainCab,remainPro,remainFat)
//        var maxRemain : Double = maxOf(remainRate.first,remainRate.second,remainRate.third)
        var query1 : String
        var query2 : String
        var query3 : String
        var Top5Food = arrayListOf<String>()
        var cnt : Int = 0

        if (remainRate.second < remainRate.first) {
            // 탄수화물 제일 부족
            query1 = "SELECT * from real_nutri_91  where cab >= kcal * 0.4 and kcal >= 150 ORDER by priority DESC, kcal DESC"
            query2 = "SELECT * from real_nutri_91  where cab >= kcal * 0.4 and priority = 3 ORDER by kcal DESC"
            query3 = "SELECT * from real_nutri_91  where cab >= kcal * 0.4 and kcal <= 80 ORDER by priority DESC"
            textView7.text = "탄수화물 식사량이 부족"
        }
        else {
            // 단백질 제일 부족
            query1 = "SELECT * from real_nutri_91  where protein >= kcal * 0.4 and kcal >= 150 ORDER by priority DESC, kcal DESC"
            query2 = "SELECT * from real_nutri_91  where protein >= kcal * 0.4 and priority = 3 ORDER by kcal DESC"
            query3 = "SELECT * from real_nutri_91  where protein >= kcal * 0.4 and kcal <= 80 ORDER by priority DESC"
            textView7.text = "단백질 식사량이 부족"
        }

        dbHelper = DBHelper(this, "food_nutri.db", null, 1)
        db = dbHelper.readableDatabase

        var cursor: Cursor = db.rawQuery(query1, null)
        while (cursor.moveToNext()) {
            if (cnt == 2) break
            if (cursor.getString(1) in Top5Food) continue

            Top5Food.add(cursor.getString(1))
            foodList.add(
                FoodData(
                    cursor.getString(1),
                    cursor.getString(2).toInt(),
                    100,
                    cursor.getString(5).toInt(),
                    cursor.getString(3).toInt(),
                    cursor.getString(4).toInt()
                )
            )
            cnt++
            Log.d("reco",cursor.getString(1))
            Log.d("reco",cnt.toString())
        }
        cursor.close()
        Log.d("reco",Top5Food.toString())
        var cursor2: Cursor = db.rawQuery(query2, null)
        while (cursor2.moveToNext()) {
            if (cnt == 4) break
            if (cursor2.getString(1) in Top5Food) continue

            Top5Food.add(cursor2.getString(1))
            foodList.add(
                FoodData(
                    cursor2.getString(1),
                    cursor2.getString(2).toInt(),
                    100,
                    cursor2.getString(5).toInt(),
                    cursor2.getString(3).toInt(),
                    cursor2.getString(4).toInt()
                )
            )
            cnt++
            Log.d("reco",cursor2.getString(1))
            Log.d("reco",cnt.toString())
        }
        cursor2.close()
        Log.d("reco",Top5Food.toString())
        var cursor3: Cursor = db.rawQuery(query3, null)
        while (cursor3.moveToNext()) {
            if (cnt == 5) break
            if (cursor3.getString(1) in Top5Food) continue

            Top5Food.add(cursor3.getString(1))
            foodList.add(
                FoodData(
                    cursor3.getString(1),
                    cursor3.getString(2).toInt(),
                    100,
                    cursor3.getString(5).toInt(),
                    cursor3.getString(3).toInt(),
                    cursor3.getString(4).toInt()
                )
            )
            cnt++
            Log.d("reco",cursor3.getString(1))
            Log.d("reco",cnt.toString())
        }
        cursor3.close()


    }
}