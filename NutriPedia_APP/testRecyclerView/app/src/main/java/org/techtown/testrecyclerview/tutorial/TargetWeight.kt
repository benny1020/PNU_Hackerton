package org.techtown.testrecyclerview.tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R

class TargetWeight : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_weight)
        val db = DBHelper(this, "food_nutri.db", null, 1)


        val targetWeight = findViewById<TextView>(R.id.infoTv)
        val intentBtn = findViewById<Button>(R.id.intentBtn)
        intentBtn.text = "다음"

        var weightList: List<Int> = (150 downTo 35).toList()
        var weightStrConvertList = weightList.map { it.toString() }

        val targetNp = findViewById<NumberPicker>(R.id.infoNp)
//        np.minValue = 0
        targetNp.maxValue = weightStrConvertList.size - 1
        targetNp.wrapSelectorWheel = true
        targetNp.displayedValues = weightStrConvertList.toTypedArray()
        targetNp.value = 90
        var targetvalue = 90
        targetNp.setOnValueChangedListener { picker, oldVal, newVal ->
            targetvalue = newVal
        }

        intentBtn.setOnClickListener {
            db.updateUserInfo("target_weight", 150 - targetvalue)
            db.close()
            val intent = Intent( this, CurrentHeight::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

        }



    }
}