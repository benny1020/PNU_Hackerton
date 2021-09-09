package org.techtown.testrecyclerview.tutorial

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R
import java.io.*

class CurrentWeight : AppCompatActivity() {

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_weight)
        dbHelper = DBHelper(this, "food_nutri.db", null, 1)
        database = dbHelper.writableDatabase
        dbHelper.insertUserInfo()
//        dbHelper.insertRecord()

        val currentWeight = findViewById<TextView>(R.id.infoTv)
        val intentBtn = findViewById<Button>(R.id.intentBtn)
        intentBtn.text = "다음"

        var weightList: List<Int> = (150 downTo 35).toList()
        var weightStrConvertList = weightList.map { it.toString() }

        val currentNp = findViewById<NumberPicker>(R.id.infoNp)
//        np.minValue = 0
        currentNp.maxValue = weightStrConvertList.size - 1
        currentNp.wrapSelectorWheel = true
        currentNp.displayedValues = weightStrConvertList.toTypedArray()
        currentNp.value = 90
        var currentvalue = 90
        currentNp.setOnValueChangedListener { picker, oldVal, newVal ->
            currentvalue = newVal
        }


//        fun saveData() {
//            val currentWeightSave = getSharedPreferences()
//        }


        intentBtn.setOnClickListener {
            dbHelper.updateUserInfo("first_weight", 150 - currentvalue)
            dbHelper.updateUserInfo("current_weight", 150 - currentvalue)
            dbHelper.close()
            val intent = Intent( this, TargetWeight::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

        }

    }


}