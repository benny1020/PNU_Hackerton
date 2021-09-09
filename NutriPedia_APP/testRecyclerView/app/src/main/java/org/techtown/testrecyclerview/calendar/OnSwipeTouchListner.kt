package org.techtown.testrecyclerview.calendar

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector

    companion object {
        private val SWIPE_THRESHOLD = 50
        private val SWIPE_VELOCITY_THRESHOLD = 50
    }

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        try {

            return gestureDetector.onTouchEvent(event)
        } catch (e: Exception) {
            // Error Handling
        }
        return false
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {


        //override fun onDown(e: MotionEvent): Boolean {
          //  Log.e("touch","touch!!!")
            //return true
        //}



        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                Log.e("test", "${diffX},${diffY}")
                if (Math.abs(diffX) >= Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            Log.d("MainActivity: ","--------------plz-------------------")

                            onSwipeRight()
                        } else {
                            Log.d("MainActivity: ","--------------plz-------------------")
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else {
                    clickListner()
                }
//                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
//                    if (diffY > 0) {
//                        onSwipeBottom()
//                    } else {
//                        onSwipeTop()
//                    }
//                    result = true
//                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }


    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun clickListner() {}

    open fun onSwipeTop() {}

    open fun onSwipeBottom() {}
}