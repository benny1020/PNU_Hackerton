package org.techtown.testrecyclerview.calendar

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity


import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.item_schedule.*
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.FragmentTwo
import org.techtown.testrecyclerview.R
import org.techtown.testrecyclerview.previous.PreviousActivity
import org.techtown.testrecyclerview.settings.SettingActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewAdapter(val mainActivity: FragmentTwo) : RecyclerView.Adapter<ViewHolderHelper>() {

    val baseCalendar = BaseCalendar()

    lateinit var dbHelper : DBHelper
    lateinit var db : SQLiteDatabase

    var curList = ArrayList<String>()
    var preList = ArrayList<String>()
    var nextList = ArrayList<String>()
    var isRefresh : Int = 1


    lateinit var curMonth : String
    lateinit var preMonth : String
    lateinit var nextMonth : String

    init {
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        dbHelper = DBHelper(parent.context, "food_nutri.db", null, 1)
        db = dbHelper.readableDatabase

        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {

        val todayFormat = SimpleDateFormat("dd", Locale.KOREAN)
        var todayDay= todayFormat.format(Calendar.getInstance().time).toInt()

        if (baseCalendar.data[position] == todayDay && mainActivity.displayMon == mainActivity.todayMon && position >= baseCalendar.prevMonthTailOffset
            && position < baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.tv_date.setTextColor(Color.parseColor("#FFFFFF"))
            holder.tv_date.setBackgroundColor(Color.parseColor("#1075C0"))
            holder.dateBox.setBackgroundColor(Color.parseColor("#1075C0"))
        }
        else holder.tv_date.setTextColor(Color.parseColor("#000000"))

        if (position < baseCalendar.prevMonthTailOffset
            || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.tv_date.alpha = 0.3f
        } else {
            holder.tv_date.alpha = 1f
        }
        holder.tv_date.text = baseCalendar.data[position].toString()


        if(isRefresh == 1) {
            curList = dbHelper.getListSuccess(mainActivity.displayMon)
            preList = dbHelper.getListSuccess(mainActivity.displayMon - 1)
            nextList = dbHelper.getListSuccess(mainActivity.displayMon + 1)
            isRefresh = 0

            if (mainActivity.displayMon < 10) curMonth = "0${mainActivity.displayMon}"
            else curMonth = "${mainActivity.displayMon}"
            if (mainActivity.displayMon-1 < 10) preMonth = "0${mainActivity.displayMon-1}"
            else preMonth = "${mainActivity.displayMon-1}"
            if (mainActivity.displayMon+1 < 10) nextMonth = "0${mainActivity.displayMon+1}"
            else nextMonth = "${mainActivity.displayMon+1}"

            Log.d("checkkk querry",curList.toString())
            Log.d("checkkk querry",preList.toString())
            Log.d("checkkk querry",nextList.toString())
        }


        var curDay : String
        if (baseCalendar.data[position] < 10) curDay = "0${baseCalendar.data[position]}"
        else curDay = "${baseCalendar.data[position]}"

        if (baseCalendar.data[position] == todayDay && mainActivity.displayMon == mainActivity.todayMon && position >= baseCalendar.prevMonthTailOffset
            && position < baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            // 현재달 오늘
                holder.tv_test.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        else if ((mainActivity.displayMon == mainActivity.todayMon && position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate)
            || (mainActivity.displayMon == mainActivity.todayMon && baseCalendar.data[position] > todayDay)) {
                // 현재 달 오늘 이후 회색
            holder.tv_test.setCardBackgroundColor(Color.parseColor("#F7F7F7"))  // 회색
        }
        else if ((mainActivity.displayMon == mainActivity.todayMon && position < baseCalendar.prevMonthTailOffset)
            || (mainActivity.displayMon == mainActivity.todayMon && baseCalendar.data[position] < todayDay)) {
            //  현재 달 오늘 이전
                if (mainActivity.displayMon == mainActivity.todayMon && position < baseCalendar.prevMonthTailOffset) {
                    // 현재 달 이전 달
                    if("${mainActivity.displayYear}-${preMonth}-${curDay}" in preList)
                        holder.tv_test.setCardBackgroundColor(Color.parseColor("#1075C0"))  // 파랑
                    else holder.tv_test.setCardBackgroundColor(Color.parseColor("#DF4943")) // 빨강
                }
            else {
                // 현재 달 이번 달
                if("${mainActivity.displayYear}-${curMonth}-${curDay}" in curList)
                    holder.tv_test.setCardBackgroundColor(Color.parseColor("#1075C0"))  // 파랑
                else holder.tv_test.setCardBackgroundColor(Color.parseColor("#DF4943")) // 빨강
            }
        }
        else if (mainActivity.displayMon == mainActivity.todayMon+1 && position < baseCalendar.prevMonthTailOffset && baseCalendar.data[position] < todayDay) {
            // 다음 달 이전 달
            if("${mainActivity.displayYear}-${preMonth}-${curDay}" in preList)
                holder.tv_test.setCardBackgroundColor(Color.parseColor("#1075C0"))  // 파랑
            else holder.tv_test.setCardBackgroundColor(Color.parseColor("#DF4943")) // 빨강
        }
        else if (mainActivity.displayMon < mainActivity.todayMon) {
            if (position < baseCalendar.prevMonthTailOffset) {
                // 현재 달 이전의 이전 달
                if("${mainActivity.displayYear}-${curMonth}-${curDay}" in preList)
                    holder.tv_test.setCardBackgroundColor(Color.parseColor("#1075C0"))  // 파랑
                else holder.tv_test.setCardBackgroundColor(Color.parseColor("#DF4943")) // 빨강
            }
            else if (position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
                // 현재 달 이전의 다음 달
                if("${mainActivity.displayYear}-${curMonth}-${curDay}" in nextList)
                    holder.tv_test.setCardBackgroundColor(Color.parseColor("#1075C0"))  // 파랑
                else holder.tv_test.setCardBackgroundColor(Color.parseColor("#DF4943")) // 빨강
            }
            else {
                // 현재 달 이전의 달
                if("${mainActivity.displayYear}-${curMonth}-${curDay}" in curList)
                    holder.tv_test.setCardBackgroundColor(Color.parseColor("#1075C0"))  // 파랑
                else holder.tv_test.setCardBackgroundColor(Color.parseColor("#DF4943")) // 빨강
            }
        }
        else holder.tv_test.setCardBackgroundColor(Color.parseColor("#F7F7F7"))  // 회색

        holder.dateBox.setOnClickListener {
            var month : String
            var day : String
            if (baseCalendar.data[position] < 10) day = "0"+baseCalendar.data[position].toString()
            else day = baseCalendar.data[position].toString()

            var previousIntent: Intent = Intent( mainActivity.context , PreviousActivity::class.java)
            previousIntent.putExtra("년", mainActivity.displayYear)
            var box : Int
            if (position < baseCalendar.prevMonthTailOffset) {
                box = mainActivity.displayMon-1
            }
            else if(position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
                box = mainActivity.displayMon + 1
            }
            else {
                box = mainActivity.displayMon
            }
            if (box < 10) month = "0"+box.toString()
            else month = box.toString()
            previousIntent.putExtra("월",month )
            previousIntent.putExtra("일", day)
            mainActivity.startActivity(previousIntent)

        }
    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }

    private fun refreshView(calendar: Calendar) {
        isRefresh = 1

        notifyDataSetChanged()
        mainActivity.refreshCurrentMonth(calendar)
    }
}





