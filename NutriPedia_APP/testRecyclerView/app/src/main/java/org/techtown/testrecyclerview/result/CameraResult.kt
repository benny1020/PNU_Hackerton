package org.techtown.testrecyclerview.result

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_camera_result.*
import kotlinx.android.synthetic.main.activity_camera_result.nutri1_Tv
import kotlinx.android.synthetic.main.activity_camera_result.nutri2_Tv
import kotlinx.android.synthetic.main.activity_camera_result.nutri3_Tv
import kotlinx.android.synthetic.main.activity_search_result.*
import org.techtown.testrecyclerview.DBHelper

import org.techtown.testrecyclerview.MainActivity
import org.techtown.testrecyclerview.R
import org.techtown.testrecyclerview.ServerData
import org.techtown.testrecyclerview.search.SearchList
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt


class CameraResult : AppCompatActivity(){

    companion object RVarray{
        var imageArray = arrayListOf<FoodResult>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_result)
        var pos = 0
        //clickInit()
        var dbHelper = DBHelper(this, "food_nutri.db", null, 1)
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일")
        val now = System.currentTimeMillis()
        val date = Date(now)


        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = sdf.format(date)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFF")))


        var uri = intent.getStringExtra("uri")
        var totalUri = Uri.parse(uri)
        imageArray.clear()
        imageArray.addAll(MainActivity.arrayUse)

//        Log.d("Check result", )
        imageArray.add(imageArray.size,FoodResult("인식 실패",0,100, 0,0,0,null,false))

        Log.e("size","${imageArray.size}")

        mainIv.setImageURI(imageArray[0].uri)
        MainActivity.arrayUse.clear()

        foodTv1.text = imageArray[0].foodName
        kcalTv.text = imageArray[0].calorie.toString() + "Kcal"
        nutri1_Tv.text = imageArray[0].nutri1.toString() + "Kcal"
        nutri2_Tv.text = imageArray[0].nutri2.toString() + "Kcal"
        nutri3_Tv.text = imageArray[0].nutri3.toString() + "Kcal"
        textView10.text = imageArray[0].foodName+"이 아닌가요?"

        var total = 0
        for (i in 0 until imageArray.size) {
            total += (imageArray[i].calorie * imageArray[i].amount / 100)
        }
        totalCal.text = total.toString() + "Kcal"

        var tempList: List<Int> = (50 downTo 5).toList()
        var gramList = ArrayList<Int>()
        for (i in 0 until tempList.size) {
            gramList.add(tempList[i]*10)
        }
        var gramStrConvertList = gramList.map { it.toString() }


        val currentNp = findViewById<NumberPicker>(R.id.np_gram)

        currentNp.maxValue = gramStrConvertList.size - 1
        currentNp.wrapSelectorWheel = true
        currentNp.displayedValues = gramStrConvertList.toTypedArray()
        currentNp.value = 40
        var currentvalue = 40




        currentNp.setOnValueChangedListener { picker, oldVal, newVal ->
            currentvalue = newVal

//            imageArray[pos].nutri1 = (imageArray[pos].nutri1 * (50-newVal) / 10)
//            imageArray[pos].nutri2 = (ceil((imageArray[pos].nutri2 * (50-newVal) / (50-oldVal)).toDouble())).toInt()
//            imageArray[pos].nutri3 = (ceil((imageArray[pos].nutri3 * (50-newVal) / (50-oldVal)).toDouble())).toInt()
//            imageArray[pos].calorie = imageArray[pos].calorie * (50-newVal) / (50-oldVal)
            imageArray[pos].amount = (50-newVal) * 10
            nutri1_Tv.text = (imageArray[pos].nutri1 * imageArray[pos].amount / 100).toString()+ "Kcal"
            nutri2_Tv.text = (imageArray[pos].nutri2 * imageArray[pos].amount / 100).toString()+ "Kcal"
            nutri3_Tv.text = (imageArray[pos].nutri3 * imageArray[pos].amount / 100).toString()+ "Kcal"
            kcalTv.text = (imageArray[pos].calorie * imageArray[pos].amount / 100).toString()+ "Kcal"
            total = total - (imageArray[pos].calorie * (50 - oldVal) / 10) + (imageArray[pos].calorie * (50 - newVal) / 10)
            totalCal.text = total.toString() + "Kcal"
            Log.e("check","${imageArray[pos].amount},${imageArray[pos].nutri1 * imageArray[pos].amount / 100},${imageArray[pos].nutri2 * imageArray[pos].amount / 100},${imageArray[pos].nutri3 * imageArray[pos].amount / 100}")
        }

        val mAdapter = ResultAdapter(this,imageArray)
        addRecyclerView.adapter = mAdapter

        var adapter = addRecyclerView.adapter
        addRecyclerView.invalidate()
        adapter!!.notifyDataSetChanged()
        val lm = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        addRecyclerView.layoutManager = lm
        addRecyclerView.setHasFixedSize(true)

        addRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = addRecyclerView.findChildViewUnder(e.x, e.y)
                try {
                    val position : Int = addRecyclerView.getChildAdapterPosition(child!!)
                    if (position == imageArray.size-1) { // 마지막거

                    } else if (imageArray[position].uri != null) {
                        mainIv.setImageURI(imageArray[position].uri)
                        foodTv1.text = imageArray[position].foodName
                        textView10.text = imageArray[position].foodName +"이 아닌가요?"
                        kcalTv.text = (imageArray[position].calorie * imageArray[position].amount / 100).toString()+ "Kcal"
                        nutri1_Tv.text = (imageArray[position].nutri1 * imageArray[position].amount / 100).toString()+ "Kcal"
                        nutri2_Tv.text = (imageArray[position].nutri2 * imageArray[position].amount / 100).toString()+ "Kcal"
                        nutri3_Tv.text = (imageArray[position].nutri3 * imageArray[position].amount / 100).toString()+ "Kcal"
                        currentNp.value = 50 - (imageArray[position].amount / 10)
                        pos = position
                        var total = 0
                        for (i in 0 until imageArray.size) {
                            total += (imageArray[i].calorie * imageArray[i].amount / 100)
                        }
                        totalCal.text = total.toString() + "Kcal"
                    } else
                    {
                        mainIv.setImageResource(R.drawable.ic_no_image)
                        foodTv1.text = imageArray[position].foodName
                        textView10.text = imageArray[position].foodName + "이 아닌가요?"
                        kcalTv.text = (imageArray[position].calorie * imageArray[position].amount / 100).toString()+ "Kcal"
                        nutri1_Tv.text = (imageArray[position].nutri1 * imageArray[position].amount / 100).toString()+ "Kcal"
                        nutri2_Tv.text = (imageArray[position].nutri2 * imageArray[position].amount / 100).toString()+ "Kcal"
                        nutri3_Tv.text = (imageArray[position].nutri3 * imageArray[position].amount / 100).toString()+ "Kcal"
                        currentNp.value = 50 - (imageArray[position].amount / 10)
                        pos = position
                        var total = 0
                        for (i in 0 until imageArray.size) {
                            total += (imageArray[i].calorie * imageArray[i].amount / 100)
                        }
                        totalCal.text = total.toString() + "Kcal"
                    }
                } catch (e : NullPointerException) {

                }


                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })
        fix_search.setOnClickListener {
            if (imageArray.size != 1) {
                val intent = Intent(applicationContext,FixSearchResult::class.java)
                var fixPosition : Int = 1000 // 1000은 예외처리
                for (i in 0 until imageArray.size) {
                    if (foodTv1.text == imageArray[i].foodName) {
                        fixPosition = i
                        break
                    }
                }
                intent.putExtra("fixPosition",fixPosition)
                startActivity(intent)
            }
        }

        button.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()
            mAlertDialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            mAlertDialog.window!!.setGravity(Gravity.BOTTOM)
            mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val breakfast = mDialogView.findViewById<AppCompatButton>(R.id.breakfast)
            val brunch = mDialogView.findViewById<AppCompatButton>(R.id.brunch)
            val lunch = mDialogView.findViewById<AppCompatButton>(R.id.lunch)
            val linner = mDialogView.findViewById<AppCompatButton>(R.id.linner)
            val dinner = mDialogView.findViewById<AppCompatButton>(R.id.dinner)
            val snack = mDialogView.findViewById<AppCompatButton>(R.id.snack)
            val register = mDialogView.findViewById<AppCompatButton>(R.id.registerBtn)

            var clicked: Boolean = false
            var idCheck: AppCompatButton? = null
            breakfast.setOnClickListener {
                if (idCheck == null || clicked == false) { // 아무 것도 클릭 안되어 있음
                    idCheck = breakfast
                    clicked = true
                    breakfast.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    breakfast.setTextColor(Color.rgb(92, 196, 133))
                } else if (idCheck == breakfast) { // 자기가 클릭 되있음
                    clicked = false
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                } else { // 다른거 클릭 되있음
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                    idCheck = breakfast
                    breakfast.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    breakfast.setTextColor(Color.rgb(92, 196, 133))
                }
            }

            brunch.setOnClickListener {
                if (idCheck == null || clicked == false) { // 아무 것도 클릭 안되어 있음
                    idCheck = brunch
                    clicked = true
                    brunch.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    brunch.setTextColor(Color.rgb(92, 196, 133))
                } else if (idCheck == brunch) { // 자기가 클릭 되있음
                    clicked = false
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                } else { // 다른거 클릭 되있음
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                    idCheck = brunch
                    brunch.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    brunch.setTextColor(Color.rgb(92, 196, 133))
                }
            }

            lunch.setOnClickListener {
                if (idCheck == null || clicked == false) { // 아무 것도 클릭 안되어 있음
                    idCheck = lunch
                    clicked = true
                    lunch.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    lunch.setTextColor(Color.rgb(92, 196, 133))
                } else if (idCheck == lunch) { // 자기가 클릭 되있음
                    clicked = false
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                } else { // 다른거 클릭 되있음
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                    idCheck = lunch
                    lunch.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    lunch.setTextColor(Color.rgb(92, 196, 133))
                }
            }

            linner.setOnClickListener {
                if (idCheck == null || clicked == false) { // 아무 것도 클릭 안되어 있음
                    idCheck = linner
                    clicked = true
                    linner.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    linner.setTextColor(Color.rgb(92, 196, 133))
                } else if (idCheck == linner) { // 자기가 클릭 되있음
                    clicked = false
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                } else { // 다른거 클릭 되있음
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                    idCheck = linner
                    linner.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    linner.setTextColor(Color.rgb(92, 196, 133))
                }
            }

            dinner.setOnClickListener {
                if (idCheck == null || clicked == false) { // 아무 것도 클릭 안되어 있음
                    idCheck = dinner
                    clicked = true
                    dinner.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    dinner.setTextColor(Color.rgb(92, 196, 133))
                } else if (idCheck == dinner) { // 자기가 클릭 되있음
                    clicked = false
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                } else { // 다른거 클릭 되있음
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                    idCheck = dinner
                    dinner.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    dinner.setTextColor(Color.rgb(92, 196, 133))
                }
            }

            snack.setOnClickListener {
                if (idCheck == null || clicked == false) { // 아무 것도 클릭 안되어 있음
                    idCheck = snack
                    clicked = true
                    snack.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    snack.setTextColor(Color.rgb(92, 196, 133))
                } else if (idCheck == snack) { // 자기가 클릭 되있음
                    clicked = false
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                } else { // 다른거 클릭 되있음
                    idCheck!!.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.eat_clicked
                        )
                    )
                    idCheck!!.setTextColor(Color.rgb(102, 102, 102))
                    idCheck = snack
                    snack.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.btn_background
                        )
                    )
                    snack.setTextColor(Color.rgb(92, 196, 133))
                }
            }

            register.setOnClickListener {
                mAlertDialog.dismiss()
                var mt: String? = null
                mt = when(idCheck) {
                    breakfast -> "아침"
                    brunch -> "아점"
                    lunch -> "점심"
                    linner -> "점저"
                    dinner -> "저녁"
                    snack -> "간식"
                    else -> null
                }
                for (i in 0 until imageArray.size-1) {
                    dbHelper.insertFoodRecord2(
                        mt,
                        imageArray[i].foodName,
                        imageArray[i].uri,
                        totalUri,
                        imageArray[i].amount,
                        (imageArray[i].calorie * imageArray[i].amount / 100),
                        (imageArray[i].nutri1 * imageArray[i].amount / 100),
                        (imageArray[i].nutri2 * imageArray[i].amount / 100),
                        (imageArray[i].nutri3 * imageArray[i].amount / 100)
                    )
                }
                MainActivity.checkChange = 1
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        var adapter = addRecyclerView.adapter
        addRecyclerView.invalidate()
        adapter!!.notifyDataSetChanged()


        mainIv.setImageURI(imageArray[0].uri)

        foodTv1.text = imageArray[0].foodName
        kcalTv.text = (imageArray[0].calorie * imageArray[0].amount / 100).toString() + "Kcal"
        nutri1_Tv.text = (imageArray[0].nutri1 * imageArray[0].amount / 100).toString() + "Kcal"
        nutri2_Tv.text = (imageArray[0].nutri2 * imageArray[0].amount / 100).toString() + "Kcal"
        nutri3_Tv.text = (imageArray[0].nutri3 * imageArray[0].amount / 100).toString() + "Kcal"
        var total = 0
        for (i in 0 until imageArray.size) {
            total += (imageArray[i].calorie * imageArray[i].amount / 100)
        }
        totalCal.text = total.toString() + "Kcal"


    }


}