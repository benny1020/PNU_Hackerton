package org.techtown.testrecyclerview.result

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_result.*
import org.techtown.testrecyclerview.R

class AddResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_result)


        var foodName = intent.getStringExtra("foodName")
        var calorie = intent.getDoubleExtra("calorie",0.0)
        var amount = intent.getIntExtra("amount",0)
        var nutri1 = intent.getDoubleExtra("nutri1",0.0)
        var nutri2 = intent.getDoubleExtra("nutri2",0.0)
        var nutri3 = intent.getDoubleExtra("nutri3",0.0)


        some_id.text = foodName
        nutri1_Tv.text = nutri1.toString() + "g"
        nutri2_Tv.text = nutri2.toString() + "g"
        nutri3_Tv.text = nutri3.toString() + "g"
        calorie_Tv.text = calorie.toString() + "Kcal"
        np_gram.value = amount
        kcal.text = calorie.toString() + "Kcal"
        Log.e("amount","$calorie")
        Log.e("amount","$amount")

        val registerBtn: Button = findViewById(R.id.registerBtn)
        registerBtn.setOnClickListener {
            intent.putExtra("person",np_person.value)
            intent.putExtra("gram",np_gram.value)
            intent.putExtra("plate",np_plate.value)
            finish()
        }
    }


}