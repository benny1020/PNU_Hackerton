package org.techtown.testrecyclerview.settings

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.android.synthetic.main.activity_private_fix.*
import kotlinx.android.synthetic.main.activity_water_setting.*
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R

class WaterSetting : AppCompatActivity() {
    val db = DBHelper(this, "food_nutri.db", null, 1)
    var clicked : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_setting)

        supportActionBar?.title = "목표 물 섭취량"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)


        val input1 = findViewById<EditText>(R.id.input1)
        var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(waterinput,0)
        waterinput.requestFocus()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting_detail, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when(item.itemId){
            R.id.fixActionBtn -> {
                if(waterinput.text.toString() == "")
                    db.updateUserInfo("target_water", db.getColValue(6,"user_info").toInt())
                else db.updateUserInfo("target_water", waterinput.text.toString().toInt())
                finish() }}


        return super.onOptionsItemSelected(item)
    }

}