package org.techtown.testrecyclerview.result

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.testrecyclerview.R
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Camera
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.search.FoodAdapter
import org.techtown.testrecyclerview.search.FoodData
import org.techtown.testrecyclerview.search.SearchResult
import java.util.*
import kotlin.collections.ArrayList

class ResultSearchActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var db : SQLiteDatabase
    var foodList = arrayListOf<FoodData>()
    lateinit var recyclerView : RecyclerView
    val displayList = ArrayList<FoodData>()
    val mAdapter = FoodAdapter(this,displayList)
    lateinit var foodName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_search)
        dbHelper = DBHelper(this, "food_nutri.db", null, 1)
        db = dbHelper.readableDatabase

        recyclerView = findViewById(R.id.mRecyclerView)
        fillData()

        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        recyclerView.setHasFixedSize(true)
        displayList.addAll(foodList)

        var calorie : Int = 0
        var amount : Int = 0
        var nutri1 : Int = 0
        var nutri2 : Int = 0
        var nutri3 : Int = 0


        mAdapter.setItemClickListner(object : FoodAdapter.OnItemClickListner{
            override fun onClick(v: View, position: Int) {
                //val intent = Intent(applicationContext, AddResult::class.java)

                foodName = displayList[position].foodName
                calorie = displayList[position].calorie
                nutri1 = displayList[position].nutri1
                nutri2 = displayList[position].nutri2
                nutri3 = displayList[position].nutri3

                CameraResult.imageArray.add(
                    CameraResult.imageArray.size-1,FoodResult(foodName,calorie,100,nutri1,nutri2,nutri3,null,true)
                )


                intent.putExtra("foodName",foodName)
                intent.putExtra("calorie",calorie)
                intent.putExtra("nutri1",nutri1)
                intent.putExtra("nutri2",nutri2)
                intent.putExtra("nutri3",nutri3)
                Log.e("size", "${CameraResult.imageArray.size}")
               // startActivityForResult(intent,101)
                finish()
            }

        })
    }



    private fun fillData() {
        var cursor: Cursor = db.rawQuery("SELECT * FROM real_nutri", null)
        while(cursor.moveToNext()) {
            foodList.add(FoodData(cursor.getString(1), cursor.getString(2).toInt(),100,cursor.getString(3).toInt(), cursor.getString(4).toInt(), cursor.getString(5).toInt()))

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        supportActionBar?.title = "음식 검색"
        val menuItem = menu!!.findItem(R.id.action_search)
        if(menuItem != null) {
            val searchView = menuItem.actionView as SearchView
            searchView.queryHint = "칼로리가 궁금한 음식을 검색해보세요."
            searchView.setBackgroundColor(Color.WHITE)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        foodList.forEach {
                            if (it.foodName.toLowerCase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)
                            }
                        }
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    else {
                        displayList.clear()
                        displayList.addAll(foodList)
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }

                    return true
                }

            })
        }
        return super.onCreateOptionsMenu(menu)
    }


}