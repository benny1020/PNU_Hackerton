package org.techtown.testrecyclerview.result

import android.net.Uri

data class FoodResult(var foodName: String, var calorie: Int, var amount : Int, var nutri1: Int,
                      var nutri2: Int, var nutri3: Int, var uri: Uri?, val check : Boolean)
