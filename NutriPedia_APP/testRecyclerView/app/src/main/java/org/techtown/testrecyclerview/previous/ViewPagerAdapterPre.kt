package org.techtown.testrecyclerview.previous

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import org.techtown.testrecyclerview.DBHelper
import org.techtown.testrecyclerview.R
import org.techtown.testrecyclerview.nutrientRate
import org.techtown.testrecyclerview.recommendedKcal


class ViewPagerAdapterPre(date:String): PagerAdapter() {
    private var mContext: Context?=null

    fun ViewPagerAdapter(context: Context){
        mContext=context;
    }
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    val time :String = date



    //position에 해당하는 페이지 생성
    lateinit var view:View
    @RequiresApi(Build.VERSION_CODES.O)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        dbHelper = DBHelper(container.context, "food_nutri.db", null, 1)
        database = dbHelper.writableDatabase


        if (position == 0) {
            view = LayoutInflater.from(container.context).inflate(R.layout.page,container,false)
            val title = view.findViewById<TextView>(R.id.title)
            title.text = dbHelper.getPreWeight(time).toString() + "kg"

            val kcalPb = view.findViewById<ProgressBar>(R.id.calPb)
            val cabPb = view.findViewById<ProgressBar>(R.id.tanPb)
            val proPb = view.findViewById<ProgressBar>(R.id.danPb)
            val fatPb = view.findViewById<ProgressBar>(R.id.giPb)

            val kcalTv = view.findViewById<TextView>(R.id.calTv3)
            val tanTv = view.findViewById<TextView>(R.id.tanTv)
            val danTv = view.findViewById<TextView>(R.id.danTv)
            val giTv = view.findViewById<TextView>(R.id.giTv)



            kcalPb.progress = dbHelper.getKcal(time) * 100 / dbHelper.getColValue(8,"user_info").toInt()
            cabPb.progress = dbHelper.getNutri(7,time) * 100 / dbHelper.getNutriRate(1)
            proPb.progress = dbHelper.getNutri(8,time) * 100 / dbHelper.getNutriRate(2)
            fatPb.progress = dbHelper.getNutri(9,time) * 100 / dbHelper.getNutriRate(3)

            kcalTv.text = dbHelper.getKcal(time).toString()+"/"+dbHelper.getColValue(8,"user_info")+"Kcal"
            tanTv.text = dbHelper.getNutri(7,time).toString()+"/"+dbHelper.getNutriRate(1).toString()+"Kcal"
            danTv.text = dbHelper.getNutri(8,time).toString()+"/"+dbHelper.getNutriRate(2).toString()+"Kcal"
            giTv.text = dbHelper.getNutri(9,time).toString()+"/"+dbHelper.getNutriRate(3).toString()+"Kcal"
        }

        else {
            view = LayoutInflater.from(container.context).inflate(R.layout.pagewater,container,false)
            val waterTv = view.findViewById<TextView>(R.id.waterTv)

            waterTv.text = dbHelper.getPreWater(time).toString() + "/" + dbHelper.getColValue(6, "user_info") + "ml"

            val calPb2 = view.findViewById<ProgressBar>(R.id.calPb2)
            calPb2.progress = dbHelper.getPreWater(time) * 100 / dbHelper.getColValue(6, "user_info").toInt()

        }


//        tanPb.progress = value

//        danPb.progress = value

//        giPb.progress = value

        container.addView(view)
        return view
    }

    //position에 위치한 페이지 제거
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }


    //사용가능한 뷰 개수 리턴
    override fun getCount(): Int {
        return 2
    }

    //페이지뷰가 특정 키 객체(key object)와 연관 되는지 여부
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view==`object`)
    }
}