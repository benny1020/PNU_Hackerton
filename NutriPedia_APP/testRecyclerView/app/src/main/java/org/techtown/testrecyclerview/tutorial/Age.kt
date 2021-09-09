package org.techtown.testrecyclerview.tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R

class Age : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)
        val db = DBHelper(this, "food_nutri.db", null, 1)

        val age = findViewById<TextView>(R.id.infoTv)
        val intentBtn = findViewById<Button>(R.id.intentBtn)
        intentBtn.text = "다음"

        var ageList: List<Int> = (100 downTo 10).toList()
        var ageStrConvertList = ageList.map { it.toString() }

        val ageNp = findViewById<NumberPicker>(R.id.infoNp)
//        np.minValue = 0
        ageNp.maxValue = ageStrConvertList.size - 1
        ageNp.wrapSelectorWheel = true
        ageNp.displayedValues = ageStrConvertList.toTypedArray()
        ageNp.value = 75
        var agevalue = 75
        ageNp.setOnValueChangedListener { picker, oldVal, newVal ->
            agevalue = newVal
        }

        intentBtn.setOnClickListener {
            db.updateUserInfo("age", 100 - agevalue)
            db.close()
            val intent = Intent( this, Sex::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

        }



    }
}