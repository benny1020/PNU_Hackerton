package org.techtown.testrecyclerview.settings

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_private_fix.*
import kotlinx.android.synthetic.main.activity_tan_dan_ji_setting.*
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R

class TanDanJiSetting : AppCompatActivity() {
    val db = DBHelper(this, "food_nutri.db", null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tan_dan_ji_setting)

        supportActionBar?.title = "내 정보 수정하기"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        val input1 = findViewById<EditText>(R.id.input1)
        var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input1,0)
        input1.requestFocus()

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when(item.itemId){
            R.id.fixActionBtn -> {
                if (input1.text.toString() == "")
                    db.updateUserInfo("target_kcal", db.getColValue(8,"user_info").toInt())
                else db.updateUserInfo("target_kcal", input1.text.toString().toInt())
                if (input2.text.toString() == "")
                    db.updateUserInfo("target_cab", db.getColValue(9,"user_info").toInt())
                else db.updateUserInfo("target_cab", input2.text.toString().toInt())
                if (input3.text.toString() == "")
                    db.updateUserInfo("target_pro", db.getColValue(10,"user_info").toInt())
                else db.updateUserInfo("target_pro", input3.text.toString().toInt())
                if (input4.text.toString() == "")
                    db.updateUserInfo("target_fat", db.getColValue(11,"user_info").toInt())
                else db.updateUserInfo("target_fat", input4.text.toString().toInt())
                finish()
            }}
        return super.onOptionsItemSelected(item)
    }
}