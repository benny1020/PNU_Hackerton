package org.techtown.testrecyclerview.tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R

class CurrentHeight : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_height)
        val db = DBHelper(this, "food_nutri.db", null, 1)

        val currentHeight = findViewById<TextView>(R.id.infoTv)
        val intentBtn = findViewById<Button>(R.id.intentBtn)
        intentBtn.text = "다음"

        var heightList: List<Int> = (200 downTo 135).toList()
        var heightStrConvertList = heightList.map { it.toString() }

        val heightNp = findViewById<NumberPicker>(R.id.infoNp)
//        np.minValue = 0
        heightNp.maxValue = heightStrConvertList.size - 1
        heightNp.wrapSelectorWheel = true
        heightNp.displayedValues = heightStrConvertList.toTypedArray()
        heightNp.value = 40
        var heightvalue = 40
        heightNp.setOnValueChangedListener { picker, oldVal, newVal ->
            heightvalue = newVal
        }


        intentBtn.setOnClickListener {
            db.updateUserInfo("current_height", 200 - heightvalue)
            db.close()
            val intent = Intent( this, Age::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

        }


    }
}