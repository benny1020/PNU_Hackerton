package org.techtown.testrecyclerview.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.techtown.testrecyclerview.R
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.search.FoodAdapter
import org.techtown.testrecyclerview.search.FoodData
import java.util.*
import kotlin.collections.ArrayList

class FixSearchResult : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var db : SQLiteDatabase
    var foodList = arrayListOf<FoodData>()
    lateinit var recyclerView : RecyclerView
    val displayList = ArrayList<FoodData>()
    val mAdapter = FoodAdapter(this,displayList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_list)
        dbHelper = DBHelper(this, "food_nutri.db", null, 1)
        db = dbHelper.readableDatabase

        recyclerView = findViewById(R.id.mRecyclerView)
        fillData()

        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        recyclerView.setHasFixedSize(true)
        displayList.addAll(foodList)


        mAdapter.setItemClickListner(object : FoodAdapter.OnItemClickListner{
            override fun onClick(v: View, position: Int) {
                val fixPosition = intent.getIntExtra("fixPosition",1000)
                val uri = CameraResult.imageArray[fixPosition].uri
                CameraResult.imageArray.removeAt(fixPosition)
                CameraResult.imageArray.add(fixPosition,FoodResult(foodList[position].foodName,foodList[position].calorie,100,
                    foodList[position].nutri1,foodList[position].nutri2,foodList[position].nutri3,uri,true))
                finish()
            }

        })
    }
    private fun fillData() {
        var cursor: Cursor = db.rawQuery("SELECT * FROM real_nutri", null)
        while(cursor.moveToNext()) {
            foodList.add(FoodData(cursor.getString(1), cursor.getString(2).toInt(),100,cursor.getString(3).toInt(), cursor.getString(4).toInt(), cursor.getString(5).toInt()))
        }

//        foodLiscursor.close()
//        db.close()t.add(FoodData("gimchi",1000,100,100,100,100))
//        foodList.add(FoodData("bob",1000,100,100,100,100))
//        foodList.add(FoodData("banana",1000,100,100,100,100))
//        foodList.add(FoodData("salad",1000,100,100,100,100))
//        foodList.add(FoodData("bulgogi",1000,100,100,100,100))
//        foodList.add(FoodData("chicken",1000,100,100,100,100))
//        foodList.add(FoodData("pizza",2000,100,100,100,100))
//        foodList.add(FoodData("bread",1000,100,100,100,100))
//        foodList.add(FoodData("meat",1000,100,100,100,100))
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