package org.techtown.testrecyclerview

import android.content.Context
import android.util.Log
import java.io.*

class copyDB {
    val DB_PATH = "/data/data/org.techtown.testrecyclerview/databases/"
    val DB_NAME = "food_nutri.db"

    fun copyDataBaseFromAssets(context: Context) {

        var myInput: InputStream? = null
        var myOutput: OutputStream? = null
        try {

            val folder = context.getDatabasePath("databases")

            if (!folder.exists()) {
                if (folder.mkdirs()) folder.delete()
            }

            myInput = context.assets.open("$DB_NAME")
            val outFileName = DB_PATH + DB_NAME
            Log.e("Log1", outFileName)
            val f = File(outFileName)
            if (f.exists()){
                Log.e("Log1", "Log ----- 이미 COPY 완료")
                return
            }
            myOutput = FileOutputStream(outFileName)

            //transfer bytes from the inputfile to the outputfile
            val buffer = ByteArray(1024)
            var length: Int = myInput.read(buffer)

            while (length > 0) {
                myOutput!!.write(buffer, 0, length)
                length = myInput.read(buffer)
            }
            //Close the streams
            myOutput!!.flush()
            myOutput.close()
            myInput.close()


        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.e("Log1", "Log ----- 외부 DB COPY 완료")
    }
}