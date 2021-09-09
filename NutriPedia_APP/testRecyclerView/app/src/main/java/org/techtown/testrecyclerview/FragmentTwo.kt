package org.techtown.testrecyclerview

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_two.*
import kotlinx.android.synthetic.main.pagewater.view.*
import org.techtown.testrecyclerview.calendar.BaseCalendar
import org.techtown.testrecyclerview.calendar.OnSwipeTouchListener
import org.techtown.testrecyclerview.calendar.RecyclerViewAdapter
import org.techtown.testrecyclerview.search.FoodData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentTwo.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentTwo : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var scheduleRecyclerViewAdapter: RecyclerViewAdapter
    lateinit var linelist: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    var displayMon = 0
    var displayYear = 0

    var isset = 0
    var todayMon =0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var current_month : TextView

    lateinit var schedule : RecyclerView
    lateinit var back : ImageButton
    lateinit var forward : ImageButton
    lateinit var successDate : TextView
    lateinit var changeWeight : TextView

    val baseCalendar = BaseCalendar()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dbHelper = DBHelper(context, "food_nutri.db", null, 1)
        database = dbHelper.readableDatabase

        var V2 : View = inflater.inflate(R.layout.fragment_two, container, false)
        current_month = V2.findViewById<TextView>(R.id.tv_current_month)

        schedule = V2.findViewById<RecyclerView>(R.id.rv_schedule)
        back = V2.findViewById<ImageButton>(R.id.backIb)
        forward = V2.findViewById<ImageButton>(R.id.forwardIb)

        successDate = V2.findViewById<TextView>(R.id.successTv)
        changeWeight = V2.findViewById<TextView>(R.id.changeWeightTv)
        var linechart = V2.findViewById<LineChart>(R.id.lineChart)
        initView()


        var now = LocalDate.now()
        var year :String = now.format(DateTimeFormatter.ofPattern("yyyy"))
        var month :String = now.format(DateTimeFormatter.ofPattern("MM"))
        var day :String = now.format(DateTimeFormatter.ofPattern("dd"))
        var strnow :String = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))



        val fir : Int = dbHelper.getColValue(7,"user_info").toInt()
        val cur : Int = dbHelper.getColValue(0,"user_info").toInt()
        changeWeight.text = (cur - fir).toString()+"kg"
        successDate.text = dbHelper.getSuccess(year,month).toString()+" / "+baseCalendar.max.toString()


        var xindex : Float = 0f
        var maxWeight : Float = fir.toFloat()
        var minWeight : Float = fir.toFloat()
        var halfWeight : Float = 0f
        var valueList = dbHelper.getListChangeWeight()
        linelist = ArrayList()
        linelist.add(Entry(xindex, fir.toFloat()))

        var checkFirst : Int = 0
        var checkTwice : Float = 0f

        Log.d("check",valueList.toString())
//        Log.d("check",valueList[0].toString())
        for (i in valueList){
            if (checkFirst == 0) {
                checkFirst = 1
                if(i.toInt() == fir) continue
            }

            if (i != checkTwice)
                xindex += 5
                linelist.add(Entry(xindex, i))
            checkTwice = i
            if (i >= maxWeight) maxWeight = i
            if (i <= minWeight) minWeight = i
            Log.d("check",i.toString())
        }
        halfWeight = (maxWeight + minWeight) /2

        lineDataSet = LineDataSet(linelist, "Weight")
        lineData = LineData(lineDataSet)
        linechart.data = lineData


        lineDataSet.color = Color.parseColor("#5CC485")
//        lineDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.valueTextSize = 12f
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = Color.parseColor("#DEF3E7")
        lineDataSet.lineWidth = 4f


        linechart.run {
            data = lineData
            description.isEnabled = false // 하단 Description Label 제거함
            invalidate() // refresh
        }

        val maxLine = LimitLine(maxWeight).apply {
            lineWidth = 1.5F
            isEnabled = true
            lineColor = Color.DKGRAY
        }

        val minLine = LimitLine(minWeight).apply {
            lineWidth = 1.5F
            isEnabled = true
            lineColor = Color.DKGRAY
        }

        val averageLine = LimitLine(halfWeight).apply {
            lineWidth = 1.5F
            isEnabled = true
            lineColor = Color.DKGRAY

//            label =
        }

        linechart.apply {
            setTouchEnabled(false)
        }

        // 범례
        linechart.legend.apply {
            isEnabled = false // 사용하지 않음
        }
        // Y 축
        linechart.axisLeft.apply {
            // 라벨, 축라인, 그리드 사용하지 않음
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)

            // 한계선 추가
            removeAllLimitLines()
            addLimitLine(averageLine)
            addLimitLine(maxLine)
            addLimitLine(minLine)


            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            labelCount = 2

        }
        linechart.axisRight.apply {
            // 우측 Y축은 사용하지 않음
            isEnabled = false
        }
        var yAxis: YAxis = linechart.getAxisLeft()
        yAxis.axisMaximum = maxWeight
        yAxis.axisMinimum = minWeight

        val testToday = 31

        // X 축
        linechart.xAxis.apply {
            // x축 값은 투명으로
            textColor = Color.BLACK
            // 축라인, 그리드 사용하지 않음
            setDrawLabels(false)
            setDrawAxisLine(true)
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM

        }

        return V2
    }

    fun initView() {
        scheduleRecyclerViewAdapter = RecyclerViewAdapter(this)
        schedule.layoutManager = GridLayoutManager(context, BaseCalendar.DAYS_OF_WEEK)
        schedule.adapter = scheduleRecyclerViewAdapter
//        val swip = OnSwipeTouchListener(requireContext())

        schedule.setOnTouchListener(object :
            OnSwipeTouchListener(requireContext()) {   // 캘린더 날짜 부분 스와이프 리스너
            override fun onSwipeLeft() {
//                  왼쪽에서 오른쪽으로 스와이프 이전달로
                scheduleRecyclerViewAdapter.changeToNextMonth()
            }

            override fun onSwipeRight() {
//                  오른쪽에서 왼쪽으로 스와이프 다음달로
                scheduleRecyclerViewAdapter.changeToPrevMonth()
            }
        })

        back.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth()
        }

        forward.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
        }


//        back.setOnClickListener(object :
//            OnSwipeTouchListener(requireContext()) {
//            override fun onSwipeLeft() {
////                  왼쪽에서 오른쪽으로 스와이프 이전달로
//                scheduleRecyclerViewAdapter.changeToNextMonth()
//            }
//        })
//
//        forward.setOnClickListener {
//            fun swip.onSwipeRight() {
//                scheduleRecyclerViewAdapter.changeToPrevMonth()
//            }
//        }
    }

//        fun testText(str: String) {
//            test.text = str
//        }

        fun refreshCurrentMonth(calendar: Calendar) {
            val sdf = SimpleDateFormat("yyyy년 MM월", Locale.KOREAN)
            current_month.text = sdf.format(calendar.time)
            val sdfMon = SimpleDateFormat("MM", Locale.KOREAN)
            displayMon = sdfMon.format(calendar.time).toInt()
            val sdfYear = SimpleDateFormat("yyyy", Locale.KOREAN)
            displayYear = sdfYear.format(calendar.time).toInt()
            if (isset == 0 ) {
                todayMon = displayMon
                isset = 1
            }
//            Tvtest.text = "${displayMon} / ${todayMon}"   //  현재 달 페이지 상 달 테스트 출력
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentTwo.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentTwo().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}