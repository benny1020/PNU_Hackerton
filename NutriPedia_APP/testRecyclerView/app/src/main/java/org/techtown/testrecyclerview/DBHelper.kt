package org.techtown.testrecyclerview

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import androidx.annotation.RequiresApi
import org.techtown.testrecyclerview.result.FoodResult
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {


    override fun onCreate(db: SQLiteDatabase) {
        var sql1: String = "CREATE TABLE if not exists user_info (" +
                "current_weight integer," +
                "target_weight integer," +
                "age integer," +
                "sex integer," +
                "current_height integer," +
                "idx integer," +
                "target_water integer," +
                "first_weight integer," +
                "target_kcal integer," +
                "target_cab integer," +
                "target_pro integer," +
                "target_fat integer" +
                ");"


        var sql2: String = "CREATE TABLE if not exists record (" +
                "date DATE," +
                "mealtime TEXT," +
                "foodname TEXT," +
                "imgPath TEXT," +
                "photoGuide TEXT," +
                "amount INT," +
                "kcal INT," +
                "cab INT," +
                "pro INT," +
                "fat INT" +
                ");"

        var sql3: String = "CREATE TABLE if not exists water (" +
                "date DATE," +
                "amount INT" +
                ");"

        var sql4 : String = "CREATE TABLE if not exists success (" +
                "date DATE," +
                "issuccess INT" +
                ");"

        var sql5 : String = "CREATE TABLE if not exists change (" +
                "date DATE," +
                "weight INT" +
                ");"

        db.execSQL(sql1)
        db.execSQL(sql2)
        db.execSQL(sql3)
        db.execSQL(sql4)
        db.execSQL(sql5)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql1: String = "DROP TABLE if exists user_info"
        val sql2: String = "DROP TABLE if exists record"
        val sql3: String = "DROP TABLE if exists water"
        db.execSQL(sql1)
        db.execSQL(sql2)
        db.execSQL(sql3)
        onCreate(db)
    }

    fun isEmpty(tablename : String): Boolean {
        var db: SQLiteDatabase = writableDatabase
        var query ="SELECT * FROM "+tablename+" where date = (SELECT date('now','localtime'))"
        var cursor: Cursor = db.rawQuery(query, null)
        var cnt = 0
        var ret = true
        while(cursor.moveToNext()) {
            cnt++
        }

        cursor.close()

        if (cnt > 0) ret = false
        return ret
    }

    fun insertChange() {
        var db: SQLiteDatabase = writableDatabase
        var query = "INSERT INTO change VALUES ((SELECT date('now','localtime')), 0);"
        db.execSQL(query)
        db.close()
    }

    fun insertUserInfo() {
        var db: SQLiteDatabase = writableDatabase
        var query = "INSERT INTO user_info VALUES ('0', '0', '0', '0', '0', '0', 2000,'0',0,0,0,0);"
        db.execSQL(query)
        db.close()
    }

    fun insertRecord() {
        var db: SQLiteDatabase = writableDatabase
        var query =
            "INSERT INTO record VALUES ((SELECT date('now','localtime')), NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0);"
        db.execSQL(query)
        db.close()
    }

    fun insertFoodRecord1(_mealtime: String?, _foodname: String?, _amount: Int, _kcal: Int, _cab: Int, _pro: Int, _fat: Int) {
        var db: SQLiteDatabase = writableDatabase
        var query =
            "INSERT INTO record VALUES ((SELECT date('now','localtime')), '${_mealtime}', '${_foodname}',NULL, NULL, '${_amount}', '${_kcal}', '${_cab}', '${_pro}', '${_fat}');"
        db.execSQL(query)
        db.close()
    }

    fun insertFoodRecord2(_mealtime: String?, _foodname: String?, _uri: Uri?, _totaluri: Uri?, _amount: Int, _kcal: Int, _cab: Int, _pro: Int, _fat: Int) {
        var db: SQLiteDatabase = writableDatabase
        var query =
            "INSERT INTO record VALUES ((SELECT date('now','localtime')), '${_mealtime}', '${_foodname}', '${_uri}', '${_totaluri}', '${_amount}', '${_kcal}', '${_cab}', '${_pro}', '${_fat}');"
        db.execSQL(query)
        db.close()
    }

    fun insertWater() {
        var db: SQLiteDatabase = writableDatabase
        var query = "INSERT INTO water VALUES ((SELECT date('now','localtime')), 0);"
        db.execSQL(query)
    }

    fun insertSuccess() {
        var db: SQLiteDatabase = writableDatabase


        var query = "INSERT INTO success VALUES ((SELECT date('now','localtime')), 0);"
        Log.d("check",query)
        db.execSQL(query)

    }

    fun updateSuccess(value : Int) {
        var db: SQLiteDatabase = writableDatabase

        if(isEmpty("success")) insertSuccess()

        var query = "UPDATE success SET issuccess = "+value+" WHERE date = (SELECT date('now','localtime'));"
        db.execSQL(query)

    }

    fun updateChange( value: Int) {
        var db: SQLiteDatabase = writableDatabase

        db.execSQL(
            "UPDATE change SET weight = " + value  + " WHERE date = (SELECT date('now','localtime'));"
        )

        db.close()
    }

    fun updateWater(value:Int ) {
        var db: SQLiteDatabase = writableDatabase

        db.execSQL(
            "UPDATE water SET amount = " + value  + " WHERE date = (SELECT date('now','localtime'));"
        )

        db.close()
    }

    fun updateUserInfo(field: String, value: Int) {
        var db: SQLiteDatabase = writableDatabase

        db.execSQL(
            "UPDATE user_info SET " + field + "= " + value + " WHERE idx = " + 0 + ";"
        )

        db.close()
    }



//    fun updatewater(
//        field: String, value: Int, date: String
//    ) {
//        var db: SQLiteDatabase = writableDatabase
//
//        db.execSQL(
//            "UPDATE water SET " + field + " = " + value + " WHERE date = '" + date + "';"
//        )
//
//        db.close()
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWater(): Int {
        var now = LocalDate.now()
        var Strnow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var db: SQLiteDatabase = readableDatabase

        val query = "SELECT * FROM water"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue = 0
        var exist = 0

        while (cursor.moveToNext()) {
            if (cursor.getString(0) == Strnow) {
                returnvalue = cursor.getString(1).toInt()
                exist = 1
            }
        }

        if (exist == 0) {
            insertWater()
            var cursor1: Cursor = db.rawQuery(query, null)
            while (cursor1.moveToNext()) {
                if (cursor1.getString(0) == Strnow) {
                    returnvalue = cursor1.getString(1).toInt()
                    exist = 1
                }
            }
            cursor1.close()
        }

        cursor.close()
        db.close()
        return returnvalue
    }

    fun getPreWater(time : String): Int {

        var db: SQLiteDatabase = readableDatabase

        val query = "SELECT * FROM water where date = '${time}'"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue :Int = 0

        while(cursor.moveToNext()) {
            returnvalue += cursor.getString(1).toInt()
            break
        }

        cursor.close()
        db.close()
        return returnvalue
    }

    fun getNutriRate(pos : Int): Int {

        var db: SQLiteDatabase = readableDatabase

        var sumAll :Int = 0
        var returnvalue : Int
        var kcal: Int = getColValue(8,"user_info").toInt()
        var cab: Int = getColValue(9,"user_info").toInt()
        var pro: Int = getColValue(10,"user_info").toInt()
        var fat: Int = getColValue(11,"user_info").toInt()

        sumAll = cab + pro + fat
        if (pos == 1) returnvalue = (kcal * cab / sumAll)
        else if (pos == 2) returnvalue = (kcal * pro / sumAll)
        else returnvalue = (kcal * fat / sumAll)

        db.close()
        return returnvalue
    }

    fun getPreWeight(date: String): Int {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM change where date <= '${date}' ORDER by date desc"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue :Int = 0

        while(cursor.moveToNext()) {
            returnvalue += cursor.getString(1).toInt()
            break
        }
        if (returnvalue == 0) returnvalue = getColValue(0,"user_info").toInt()

        cursor.close()
        db.close()
        return returnvalue
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getListChangeWeight() : ArrayList<Float> {
        var db: SQLiteDatabase = readableDatabase
        var now = LocalDate.now()
        var year :String = now.format(DateTimeFormatter.ofPattern("yyyy"))
        var month :String = now.format(DateTimeFormatter.ofPattern("MM"))
        var day :String = now.format(DateTimeFormatter.ofPattern("dd"))
        var strnow :String = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var strstart : String = ""

        if (month.toInt()-1 < 10) strstart = "${year}-0${month.toInt()-1}-${day}"
        else strstart ="${year}-${month.toInt()-1}-${day}"

        var returnList:ArrayList<Float> = ArrayList()

        Log.d("check in dbh",strstart)
        Log.d("check in dbh","${strnow}")

        var cursor: Cursor = db.rawQuery("SELECT * FROM change where date Between '${strstart}' and '${strnow}' ORDER by date asc", null)
        while(cursor.moveToNext()) {
            returnList.add(cursor.getFloat(1))
            Log.d("check in dbh","123")
            Log.d("check in dbh",cursor.getString(1))
        }

        cursor.close()
        db.close()
        Log.d("check in dbh",returnList.toString())
        return returnList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getListSuccess(pos : Int) : ArrayList<String> {
        var db: SQLiteDatabase = readableDatabase
        var now = LocalDate.now()
        var year :String = now.format(DateTimeFormatter.ofPattern("yyyy"))
        var month :String = now.format(DateTimeFormatter.ofPattern("MM"))
        var strstart : String = ""


        if (pos < 10) strstart = "${year}-0${pos}"
        else strstart = "${year}-${pos}"

//        if(pos == 0) {
//            if (month.toInt() - 1 < 10) strstart = "${year}-0${month.toInt() - 1}"
//            else strstart = "${year}-${month.toInt() - 1}"
//        }
//        else if(pos == 1) {
//            if (month.toInt()< 10) strstart = "${year}-0${month.toInt()}"
//            else strstart = "${year}-${month.toInt()}"
//        }
//        else if(pos == 2) {
//            if (month.toInt() + 1 < 10) strstart = "${year}-0${month.toInt() + 1}"
//            else strstart = "${year}-${month.toInt() + 1}"
//        }

        var returnList:ArrayList<String> = ArrayList()

        Log.d("checkk querry",strstart)

        var cursor: Cursor = db.rawQuery("SELECT * FROM success where date Between '${strstart}-01' and '${strstart}-31' and issuccess = 1", null)
        while(cursor.moveToNext()) {
            returnList.add(cursor.getString(0))
        }

        cursor.close()
        db.close()
        Log.d("checkk in dbh",returnList.toString())
        return returnList
    }

    fun getSuccess(year:String, month:String) :Int {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM success where date BETWEEN '${year}-${month}-01' AND '${year}-${month}-31'"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue :Int = 0

        while(cursor.moveToNext()) {
            if (cursor.getInt(1) == 1) returnvalue++
        }

        cursor.close()
        db.close()
        return returnvalue
    }

    fun getKcal(date: String): Int {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM record where date = '${date}'"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue :Int = 0

        while(cursor.moveToNext()) {
            returnvalue += cursor.getString(6).toInt()
        }

        cursor.close()
        db.close()
        return returnvalue
    }

    fun getNutri(field:Int, date:String) : Int {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM record where date = '${date}'"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue :Int = 0

        while(cursor.moveToNext()) {
            returnvalue += cursor.getString(field).toInt()
        }

        cursor.close()
        db.close()
        return returnvalue
    }


    fun getColValue(colindex: Int, tablename: String): String {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM " + tablename;
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue = ""

        while (cursor.moveToNext()) {
            returnvalue = cursor.getString(colindex)
        }

        cursor.close()
        db.close()
        return returnvalue
    }

    fun getResultFood(colindex: Int, name: String): Int {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM real_nutri_91"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue = 0
        Log.e("check result1", returnvalue.toString())
        while (cursor.moveToNext()) {
            Log.e("test","${name.length},${name},${cursor.getString(1).length},${cursor.getString(1)}")
            if (name == cursor.getString(1)) {
                returnvalue = cursor.getInt(colindex)
                Log.e("check result2", cursor.getString(colindex))
                break
            }
        }
        Log.e("check result3", returnvalue.toString())

        cursor.close()
        db.close()
        return returnvalue
    }

    fun getColValue2(colindex: Int, name: String, mt: String?): String {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM record WHERE foodname = '${name}' and mealtime = '${mt}'"
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue = ""

        while (cursor.moveToNext()) {
            returnvalue = cursor.getString(colindex)
        }

        cursor.close()
        db.close()
        return returnvalue
    }

    /////////// test
    fun getColValueTest(columnIndex: Int, colindex: Int, tablename: String): String {
        var db: SQLiteDatabase = readableDatabase
        val query = "SELECT * FROM " + tablename
        var cursor: Cursor = db.rawQuery(query, null)
        var returnvalue = ""

//        while(cursor.moveToNext()) {
//            returnvalue = cursor.getString(colindex)
//        }

        for (i in 0..columnIndex) {
            cursor.moveToNext()
            returnvalue = cursor.getString(colindex)
        }
        cursor.close()
        db.close()
        return returnvalue
    }

    // 좋아요 기능(START)
    // name은 해당하는 음식의 이름
    fun updatePriorityUp(name: String) {
        var db: SQLiteDatabase = writableDatabase
        var query = "SELECT * FROM real_nutri_91"
        var cursor: Cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            if (name == cursor.getString(1)) {
                val value = cursor.getString(6).toInt() + 1
                db.execSQL(
                    "UPDATE real_nutri_91 SET " + "priority" + " = " + value + " WHERE name = '" + name + "';"
                )
                break
            }
        }
        cursor.close()
        db.close()
    }
    // 좋아요 기능(FINISH)

    // 싫어요 기능(START)
    fun updatePriorityDown(name: String) {
        var db: SQLiteDatabase = writableDatabase
        var query = "SELECT * FROM real_nutri_91"
        var cursor: Cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            if (name == cursor.getString(1)) {
                val value = cursor.getString(6).toInt() - 1
                db.execSQL(
                    "UPDATE real_nutri_91 SET " + "priority" + " = " + value + " WHERE name = '" + name + "';"
                )
                break
            }
        }
        cursor.close()
        db.close()
    }
    // 싫어요 기능(FINISH)

    fun getFoodInfo(name: String): FoodResult {

        Log.e("check-foodname",name)

        var kcal : Int = getResultFood(2,name)
        var cab :Int = getResultFood(5,name)
        var pro : Int = getResultFood(3,name)
        var fat : Int = getResultFood(4,name)
        var retoutput = FoodResult(name, kcal,100,cab,pro,fat, null, true)
        Log.e("check-foodname",retoutput.toString())

        return retoutput
    }
}