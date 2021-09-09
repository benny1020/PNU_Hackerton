package org.techtown.testrecyclerview.settings

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_setting.*
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R
import org.techtown.testrecyclerview.nutrientRate
import org.techtown.testrecyclerview.recommendedKcal

class SettingActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        dbHelper = DBHelper(this, "food_nutri.db", null, 1)
        database = dbHelper.writableDatabase
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "정보수정"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        tanDanJi.setOnClickListener {
            var tanDanJiIntent = Intent(applicationContext,TanDanJiSetting::class.java)
            startActivityForResult(tanDanJiIntent,10)
        }

        water.setOnClickListener{
            var waterIntent = Intent(applicationContext,WaterSetting::class.java)
            startActivityForResult(waterIntent,10)
        }

        var gender : String
        if (dbHelper.getColValue(3,"user_info")=="0")
            gender = "남자"
        else
            gender = "여자"

        infoTv.text = dbHelper.getColValue(4, "user_info") + "cm | " + gender + " | " + dbHelper.getColValue(2,"user_info") + "세"
        cWeight.text = dbHelper.getColValue(0,"user_info") + "kg"
        tWeight.text = dbHelper.getColValue(1,"user_info") + "kg"
        recokcal.text = dbHelper.getColValue(8,"user_info") + "kcal"
        twater.text = dbHelper.getColValue(6,"user_info") + "ml"

        nrate.text = dbHelper.getColValue(9,"user_info") + ":" + dbHelper.getColValue(10,"user_info") + ":" + dbHelper.getColValue(11,"user_info")

    }

    override fun onResume() {
        super.onResume()
        var gender : String
        if (dbHelper.getColValue(3,"user_info")=="0")
            gender = "남자"
        else
            gender = "여자"

        infoTv.text = dbHelper.getColValue(4, "user_info") + "cm | " + gender + " | " + dbHelper.getColValue(2,"user_info") + "세"
        cWeight.text = dbHelper.getColValue(0,"user_info") + "kg"
        tWeight.text = dbHelper.getColValue(1,"user_info") + "kg"
        recokcal.text = dbHelper.getColValue(8,"user_info") + "kcal"
        twater.text = dbHelper.getColValue(6,"user_info") + "ml"

        nrate.text = dbHelper.getColValue(9,"user_info") + ":" + dbHelper.getColValue(10,"user_info") + ":" + dbHelper.getColValue(11,"user_info")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.fixBtn) {
            var privateIntent = Intent(applicationContext,PrivateFix::class.java)
            startActivity(privateIntent)
        }
        else{}
        return super.onOptionsItemSelected(item)
    }
}

