package org.techtown.testrecyclerview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_camera_result.*
import kotlinx.android.synthetic.main.fragment_one.*
import kotlinx.android.synthetic.main.page.*
import kotlinx.android.synthetic.main.search_bar.*
import kotlinx.android.synthetic.main.search_bar.view.*
import org.techtown.testrecyclerview.recommend.RecommendFoodAdapter
import org.techtown.testrecyclerview.recommend.RecommendList
import org.techtown.testrecyclerview.recommend.RecommendResult
import org.techtown.testrecyclerview.result.CameraResult
import org.techtown.testrecyclerview.result.FixItemActivity
import org.techtown.testrecyclerview.result.FoodResult
import org.techtown.testrecyclerview.search.FoodData
import org.techtown.testrecyclerview.search.SearchList
import org.techtown.testrecyclerview.settings.SettingActivity
import org.w3c.dom.Text
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentOne.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentOne : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val REQUEST_IMAGE_CAPTURE = 1 //카메라 사진촬영 요청코드
    lateinit var curPhotoPath: String //문자열 형태의 사진 경로 값(초기값을 null로 시작하고 싶을 때)
    var mealtime = arrayOf("아침","아점","점심","점저","저녁","간식")
    lateinit var dbHelper : DBHelper
    lateinit var db : SQLiteDatabase

    val displayList = ArrayList<RecordFoodData>()
    lateinit var v : View



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_one,container,false)


        dbHelper = DBHelper(context, "food_nutri.db", null, 1)
        db = dbHelper.readableDatabase

        var recyclerView = v.findViewById<RecyclerView>(R.id.recyclerview_main) // recyclerview id

        var now = LocalDate.now()
        var strnow :String = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        displayList.clear()
        cardList.clear()
        fillFoodData(strnow)

        var recommendBtn = v.findViewById<Button>(R.id.recommendBtn)
        var layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

//        var remainTv = v.findViewById<TextView>(R.id.textView12)
        var calTv = v.findViewById<TextView>(R.id.textView14)
        var recommendedKcal : Int
        if (dbHelper.getColValue(8,"user_info") != "")
            recommendedKcal = dbHelper.getColValue(8,"user_info").toInt()
        else recommendedKcal = 0

        if (MainActivity.checkChange == 1) {
            if(dbHelper.getKcal(strnow) >= (recommendedKcal * 0.85) && dbHelper.getKcal(strnow) <= (recommendedKcal * 1.15))
                dbHelper.updateSuccess(1)
            else dbHelper.updateSuccess(0)
            MainActivity.checkChange = 0
            Log.d("reco",dbHelper.getColValue(1,"success"))
            Log.d("reco",(recommendedKcal * 0.85).toString())
        }

        calTv.text = "${recommendedKcal - dbHelper.getKcal(strnow)}Kcal"
        Log.d("reco1",(recommendedKcal).toString())
        Log.d("reco2",(dbHelper.getKcal(strnow)).toString())
        Log.d("reco3",(recommendedKcal - dbHelper.getKcal(strnow)).toString())

        recyclerView.setHasFixedSize(true)
        displayList.addAll(cardList)

        var adapter = MainActivity.MyAdapter(requireContext(),displayList)
        recyclerView.adapter = adapter


        val viewAdapter= ViewPagerAdapter()
        val pagerTest = v.findViewById<ViewPager>(R.id.pager)
        pagerTest.adapter = viewAdapter
        pagerTest.pageMargin = 30

        pagerTest.addOnAdapterChangeListener { viewPager, oldAdapter, newAdapter ->  }
//        pagerTest.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            val act = activity as MainActivity
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//            }
//            override fun onPageSelected(position: Int) {
//                pagerTest.adapter!!.notifyDataSetChanged()
//                pagerTest.invalidate()
//                act.test()
//            }
//
//        })


        var searchView = v.findViewById<View>(R.id.search_bar1)
        var searchTv = searchView.findViewById<TextView>(R.id.search_tv)
        var searchIv = searchView.findViewById<ImageView>(R.id.search_image)
        var settingBtn = searchView.findViewById<ImageView>(R.id.setting)
//        searchTv.auto_tv.setAdapter(arrayAdapter)
//        searchTv.auto_tv.threshold = 0

        searchIv.setOnClickListener {
            val intent = Intent(context,SearchList::class.java)
//            intent.putExtra("send", auto_tv.text)
            startActivity(intent)
        }
        settingBtn.setOnClickListener {
            var settingIntent: Intent = Intent(context, SettingActivity::class.java)
            startActivity(settingIntent)
        }

//        searchTv.auto_tv.setOnItemClickListener { parent, view, position, id ->
//            val setName : String ?= arrayAdapter.getItem(position)
//            searchTv.auto_tv.setText("${setName}")
//        }
        searchTv.setOnClickListener {
            val intent = Intent(context,SearchList::class.java)
//            intent.putExtra("send", auto_tv.text)
            startActivity(intent)
        }

//        adapter.setItemClickListner(object : MainActivity.MyAdapter.OnItemClickListner {
//
//            override fun onClick(v: View, position: Int) {
//                val cardViewIntent = Intent(context, FixItemActivity::class.java)
//
//                startActivityForResult(cardViewIntent, 123)
//            }
//
//        })


//        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener{
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                try {
//                    val child = recyclerView.findChildViewUnder(e.x, e.y)
//                    val cardViewIntent = Intent(context, FixItemActivity::class.java)
//                    position = recyclerView.getChildAdapterPosition(child!!)
//                    startActivityForResult(cardViewIntent, 123)
//                } catch (e : NullPointerException) {
//
//                }
//                return false
//            }
//
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//            }
//
//        })

        recommendBtn.setOnClickListener {
            var intentRecommend = Intent(context, RecommendList::class.java)
            startActivity(intentRecommend)
        }
        return v
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        var recyclerView = v.findViewById<RecyclerView>(R.id.recyclerview_main) // recyclerview id
        var now = LocalDate.now()
        var strnow :String = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        dbHelper = DBHelper(context, "food_nutri.db", null, 1)
        db = dbHelper.readableDatabase

        displayList.clear()
        cardList.clear()
        fillFoodData(strnow)

        var layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        recyclerView.setHasFixedSize(true)
        displayList.addAll(cardList)

        var adapter = MainActivity.MyAdapter(MainActivity.gContext(),displayList)
        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
        recyclerView.invalidate()

        var calTv = v.findViewById<TextView>(R.id.textView14)

        var recommendedKcal : Int
        if (dbHelper.getColValue(8,"user_info") != "")
            recommendedKcal = dbHelper.getColValue(8,"user_info").toInt()
        else recommendedKcal = 0

        if (MainActivity.checkChange == 1) {
            if(dbHelper.getKcal(strnow) >= (recommendedKcal * 0.85) && dbHelper.getKcal(strnow) <= (recommendedKcal * 1.15))
                dbHelper.updateSuccess(1)
            else dbHelper.updateSuccess(0)
            MainActivity.checkChange = 0
            Log.d("update",dbHelper.getColValue(1,"success"))
            Log.d("update",(recommendedKcal * 0.85).toString())
        }

        calTv.text = "${recommendedKcal - dbHelper.getKcal(strnow)}Kcal"

        val viewAdapter= ViewPagerAdapter()
        val pagerTest = v.findViewById<ViewPager>(R.id.pager)
        pagerTest.adapter = viewAdapter
        val dapter = pagerTest.adapter
        pagerTest.pageMargin = 30

        dapter!!.notifyDataSetChanged()
        pagerTest.invalidate()

        super.onResume()
    }


    fun fillFoodData(time: String) {
        resultList.clear()
        cardList.clear()
        for(i in 0..5) {
            Log.d("Log1",time)
            Log.d("Log1",mealtime[i])
//            foodList.clear()
            var cursor: Cursor = db.rawQuery("SELECT * FROM record where date = '${time}' and mealtime = '${mealtime[i]}'", null)
            var mealKcal : Int = 0
            var mealAmount : Int =0
            var mealCab : Int =0
            var mealPro:Int =0
            var mealFat:Int =0
            var cnt : Int = 0
            var names = arrayListOf<String>()

            var total :String? = null
            var mealTime : String
            var foodName : String
            var picture : String?
            var calorie : Int
            var amount : Int
            var nutri1 : Int
            var nutri2 : Int
            var nutri3 : Int
            var extra : String = ""
            while (cursor.moveToNext()) {
                mealKcal += cursor.getString(6).toInt()
                mealAmount += cursor.getString(5).toInt()
                mealCab += cursor.getString(7).toInt()
                mealPro += cursor.getString(8).toInt()
                mealFat += cursor.getString(9).toInt()
                names.add(cursor.getString(2))
                cnt++
                mealTime = mealtime[i]
                foodName = cursor.getString(2)
                picture = cursor.getString(3)
                calorie = cursor.getString(6).toInt()
                amount = cursor.getString(5).toInt()
                nutri1 = cursor.getString(7).toInt()
                nutri2 = cursor.getString(8).toInt()
                nutri3 = cursor.getString(9).toInt()
                if (picture != null) {
                    total = cursor.getString(4)
                }
                resultList.add(
                    RecordFoodData(
                        mealTime,
                        foodName,
                        picture,
                        calorie,
                        amount,
                        nutri1,
                        nutri2,
                        nutri3
                    )
                )

            }

            if (cnt>0) {
                Log.d("Log1","good")
                var nameStr: String = ""
                if (cnt == 1) {
                    nameStr = names[0]
                }
                else if (cnt == 2) {
                    nameStr = names[0]
                    extra += " 외 ${cnt-1}개"
                }
                else {
                    nameStr = names[0]
                    extra += " 외 ${cnt-1}개"
                }
                nameStr += extra
                //            if (nameStr.length >15)

                cardList.add(
                    RecordFoodData(
                        mealtime[i],
                        nameStr,
                        total,
                        mealKcal,
                        mealAmount,
                        mealCab,
                        mealPro,
                        mealFat
                    )
                )

            }
        }


    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentOne.
         */
        // TODO: Rename and change types and number of parameters
        var resultList = arrayListOf<RecordFoodData>()
        var position = 0
        val cardList : ArrayList<RecordFoodData> = ArrayList<RecordFoodData>()
        @JvmStatic fun newInstance(param1: String, param2: String) =
            FragmentOne().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}