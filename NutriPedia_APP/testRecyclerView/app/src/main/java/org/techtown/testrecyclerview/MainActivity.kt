package org.techtown.testrecyclerview

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.ExifInterface
import android.media.tv.TvContract.Programs.Genres.decode
import android.net.Uri
import android.net.Uri.decode
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil.decode
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.card_layout.*
import kotlinx.android.synthetic.main.search_bar.view.*
import kotlinx.coroutines.*
import org.techtown.testrecyclerview.result.CameraResult
import org.techtown.testrecyclerview.result.FixItemActivity
import org.techtown.testrecyclerview.result.FoodResult
import org.techtown.testrecyclerview.tutorial.CurrentWeight
import java.io.*
import java.lang.Byte.decode
import java.lang.Integer.decode
import java.lang.Long.decode
import java.net.URLDecoder.decode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var db : SQLiteDatabase
    lateinit var photoURI: Uri
    val copy = copyDB()
    val REQUEST_IMAGE_CAPTURE = 1 //카메라 사진촬영 요청코드
    lateinit var curPhotoPath: String //문자열 형태의 사진 경로 값(초기값을 null로 시작하고 싶을 때)
    val REQUEST_CODE = 0

    lateinit var br: BroadcastReceiver



    init {
        instance = this

    }
    companion object {
        var instance: MainActivity? = null
        var arrayUse : ArrayList<FoodResult> = ArrayList<FoodResult>()
        fun gContext() : Context {
            return instance!!.applicationContext
        }
        var checkChange : Int = 1
        var pos = 0
    }
    private val fl: FrameLayout by lazy {
        findViewById(R.id.main_frame)
    }
    private val bn: BottomNavigationView by lazy{
        findViewById(R.id.bottomNavigationView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setPermission()// 권한을 체크하는 메소드 수행
        supportFragmentManager.beginTransaction().add(fl.id,FragmentOne()).commit()
        supportActionBar!!.hide()
        var isfirst : Int = 0
        intent.action = Intent.ACTION_OPEN_DOCUMENT_TREE
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        val preferences = getSharedPreferences("a", MODE_PRIVATE)
        var editor = preferences.edit()
        var firstViewShow : Boolean = preferences.getBoolean("hello", false)

        if (!firstViewShow) {
            editor.putBoolean("hello",true).apply()
            var firstIntent = Intent(applicationContext,CurrentWeight::class.java)
            copy.copyDataBaseFromAssets(this)
            startActivity(firstIntent)
        }


        bn.setOnNavigationItemSelectedListener {
            replaceFragment(
                when (it.itemId) {
                    R.id.home -> FragmentOne()
                    else -> FragmentTwo()

                }
            )
            true
        }
//
//        FileUploadUtils().receiveFromServer() // 받는 부분은 일단 구현 안함

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(1).isEnabled = false

        fab.setOnClickListener {
            showSelectCameraOrImage()
        }



    }


    fun test() {
        val frg = supportFragmentManager.findFragmentById(R.id.main_frame)
        val ft = supportFragmentManager.beginTransaction()
        ft.detach(frg!!).attach(frg).commit()
    }



    fun gallery() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,REQUEST_CODE)
    }

    private fun showSelectCameraOrImage() {
        CameraOrImageSelectDialog(object: CameraOrImageSelectDialog.OnClickSelectListener {
            override fun onClickCamera() {
                takeCapture()
            }
            override fun onClickImage() {
                gallery()
            }
        }).show(supportFragmentManager, "CameraOrImageSelectDialog")
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fl.id, fragment).commit()
    }


    fun takeCapture() { //카메라 촬영
        // 기본 카메라 앱 실행
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photofile: File? =try {
                    createImageFile()
                } catch(ex: IOException) {
                    null
                }
                Log.d("Check","picture")
                photofile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "org.techtown.testrecyclerview.fileprovider", //보안 서명
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    fun createImageFile(): File { // 이미지파일 생성
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd-HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.e("storageDir","$storageDir")
        return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir)
            .apply { curPhotoPath = absolutePath }
    }


    /*
    테드 퍼미션 설정
     */
    fun setPermission() {
        val permission = object : PermissionListener {
            override fun onPermissionGranted() { // 설정해놓은 권한 들이 허용 되었을 경우
                Toast.makeText(this@MainActivity, "권한이 허용 되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { // 설정해 놓은 권한들을 거부한 경우
                Toast.makeText(this@MainActivity, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("영양피디아를 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정] -> [권한] 항목에서 허용해주세요.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA)
            .check()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //startActivityForResult를 통해서 기본 카메라 앱으로 부터 받아온 사진 결과값
        super.onActivityResult(requestCode, resultCode, data)
        //사진을 성공적으로 가져 온 경우
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //val ivPhoto : ImageView = findViewById(R.id.ivPhoto)
            var bitmap: Bitmap
            val file = File(curPhotoPath) // 절대 경로인 사진이 저장된 값
            if (Build.VERSION.SDK_INT < 28) { // 안드로이드9.0(PIE) 버전보다 낮을 경우
                Log.d("Check",file.toString())
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                // 끌어온 비트맵을 넣음
            } else { //PIE버전 이상인 경우
                //contentResolver.takePersistableUriPermission(data!!.data!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val decode = ImageDecoder.createSource( //변환을 해서 가져옴
                    contentResolver,
                    Uri.fromFile(file)
                )

                Log.e("checku","${Uri.fromFile(file)}")
                bitmap = ImageDecoder.decodeBitmap(decode)
            }
            savePhoto(bitmap)
        }
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
            var uri = data?.data
            //val path : String? = data!!.data!!.path
            val path = getFullPath(uri!!)
            var input = contentResolver.openInputStream(uri!!)
            var image = BitmapFactory.decodeStream(input)
//            Log.e("path","$path")
//            Log.e("old uri","$uri")
//            uri = Uri.parse(path)
//            Log.e("new uri","$uri")
            val file : File = bitmapToFile(image,path)
            uri = bitmapToUri(image,99)
            Log.e("uuu","$uri")


//            CoroutineScope(Dispatchers.IO).launch {
//                var job = async {
//                    var sd : ArrayList<ServerData>
//                    var serverData = FileUploadUtils().send2Server(file)
//                    sd = serverData
//                    do {
//                        Log.d("hello","hello")
//                    }
//                    while (serverData.isEmpty())
//
//                    dataTOUse(sd, image) }.await()
//
//
//                Handler().postDelayed({
//                    var cameraIntent = Intent(applicationContext, CameraResult::class.java)
//                    cameraIntent.putExtra("uri", uri.toString())
//                    startActivity(cameraIntent)},1000)
//            }

            var serverData = FileUploadUtils().send2Server(file)
            Handler().postDelayed({ dataTOUse(serverData, image)
                var cameraIntent = Intent(applicationContext, CameraResult::class.java)
                cameraIntent.putExtra("uri", uri.toString())
                startActivity(cameraIntent)},4000)

        }

    }
//    @Multipart
//    @POST("/orders{order_idx}/file")
//        fun sendFIle(
//        @Path("order_idx") orderIdx: Int,
//        @Part file: MultipartBody.Part
//        ):Call<FileResponse>{
//        }



    fun savePhoto(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" //사진 폴더에 저장 경로 선언
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd-HHmmss").format(Date())
        val fileName = "${timeStamp}.jpeg"
        val folder = File(folderPath)
        if (!folder.isDirectory) { // 현재 해당 경로에 폴더가 존재하지 않는다면
            folder.mkdirs()
        }
        //실제적인 저장처리
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)

        val file = File("/storage/emulated/0/Pictures/${fileName}")


//        CoroutineScope(Dispatchers.IO).launch {
//            var job = async {
//                var sd : ArrayList<ServerData>
//                var serverData = FileUploadUtils().send2Server(file)
//                sd = serverData
//                do {
//                    Log.d("hello","hello")
//                }
//                while (serverData.isEmpty())
//
//                dataTOUse(sd, bitmap) }.await()
//            Handler().postDelayed(
//            {
//            var cameraIntent = Intent(applicationContext, CameraResult::class.java)
//            cameraIntent.putExtra("uri",photoURI.toString())
//            startActivity(cameraIntent)
//            }
//            ,1000)
//        }

        val serverData = FileUploadUtils().send2Server(file)
        Handler().postDelayed(
            {
            dataTOUse(serverData,bitmap)
            var cameraIntent = Intent(applicationContext, CameraResult::class.java)
            cameraIntent.putExtra("uri",photoURI.toString())
            startActivity(cameraIntent)
            }
            ,4000)
    }

    fun dataTOUse(serverData: ArrayList<ServerData>,bitmap: Bitmap) {
        dbHelper = DBHelper(this, "food_nutri.db", null, 1)

        var crop :Bitmap
        if (serverData.size != 0) {
            for (i in 0 until serverData.size) {
                val foodname = serverData[i].name
                arrayUse.add(dbHelper.getFoodInfo(foodname))
                crop = Bitmap.createBitmap(bitmap,serverData[i].x1.toInt(),serverData[i].y1.toInt(),
                    (serverData[i].x2-serverData[i].x1).toInt(),(serverData[i].y2-serverData[i].y1).toInt())
                arrayUse[i].uri = bitmapToUri(crop,i)
                Log.e("check-foodname",arrayUse[i].foodName)
            }
        }
    }

    fun bitmapToUri(bitmap: Bitmap, i:Int) :Uri {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" //사진 폴더에 저장 경로 선언
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd-HHmmss").format(Date())+"-$i"
        val fileName = "${timeStamp}.jpeg"
        val folder = File(folderPath)
        if (!folder.isDirectory) { // 현재 해당 경로에 폴더가 존재하지 않는다면
            folder.mkdirs()
        }
        //실제적인 저장처리
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)
        val file = File("/storage/emulated/0/Pictures/${fileName}")
        var uri = Uri.parse(file.absolutePath)
        Log.e("old","$uri")
//        val temp = Uri.decode(uri.toString())
//        uri = Uri.parse(temp)

        return uri
    }

    fun bitmapToFile(bitmap:Bitmap, path : String?) : File {
        val file = File(path)
        var out : OutputStream
        Log.e("path","$path")
        file.createNewFile()
        out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)
        out.close()
        return file
    }

    fun getGps(photoPath: String) {
        var exif: ExifInterface?= null
        try{
            exif = ExifInterface(photoPath)
        }catch (e: IOException) {
            e.printStackTrace()
        }
        val lat = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        // TAG_GPS_LATITUDE_REF: Indicates whether the latitude is north or south latitude
        val lat_ref = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val lon = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        // TAG_GPS_LONGITUDE_REF: Indicates whether the longitude is east or west longitude.
        val lon_ref = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

        Log.d("latitude",lat.toString())
        Log.d("longtitude",lon.toString())
    }

    fun getFullPath(uri: Uri) :String? {
        val context = applicationContext
        val contentResolver = context.contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath = (context.applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            /*  절대 경로를 getGps()에 넘겨주기   */
            getGps(file.getAbsolutePath())


        }
        return file.getAbsolutePath()
    }




    class MyAdapter(val context: Context, var foodList: ArrayList<RecordFoodData>): RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
      
        override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): MyViewHolder {
            var v: View = LayoutInflater.from(viewgroup.context).inflate(R.layout.card_layout, viewgroup, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            val item = foodList[position]
            holder.itemView.setOnClickListener {
                itemClickListner!!.onClick(it,position)
            }

            holder.apply {
                bind(item,context)
            }

        }

        override fun getItemCount(): Int {
            return foodList.size
        }
        interface OnItemClickListner {
            fun onClick(v:View, position: Int)
        }
        private var itemClickListner: OnItemClickListner? = null

        fun setItemClickListner(itemClickListner: OnItemClickListner) {
            this.itemClickListner = itemClickListner
        }

        class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
            var itemimage: ImageView = itemview.findViewById(R.id.item_image)
            var itemtitle: TextView = itemview.findViewById(R.id.item_title)
            var itemdetail: TextView = itemview.findViewById(R.id.item_detail)
            var cardTanTv: TextView = itemview.findViewById(R.id.cardTanTv)
            var cardDanTv: TextView = itemview.findViewById(R.id.cardDanTv)
            var cardJiTv: TextView = itemview.findViewById(R.id.cardJiTv)

            fun bind (foodData:RecordFoodData, context: Context) {

                itemView.setOnClickListener {
                    pos = adapterPosition
                    val cardViewIntent = Intent(context, FixItemActivity::class.java).apply{
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.run { context.startActivity(this) }
                }
                itemtitle.text = foodData.mealTime +" | "+foodData.calorie.toString()+"Kcal"
                itemdetail.text = foodData.foodName
                if (foodData.picture != null) {
                    var uri = Uri.parse(foodData.picture)
                    try {
//                        var input : InputStream? = context.contentResolver.openInputStream(uri)
//                        val bitmap = BitmapFactory.decodeStream(input)
//                        itemimage.setImageBitmap(bitmap)
                        itemimage.setImageURI(uri)

                    } catch (e : SecurityException) {
                        itemimage.setImageResource(R.drawable.ic_no_image)
                    }


                } else {
                    itemimage.setImageResource(R.drawable.ic_no_image)
                }

                cardTanTv.text = "탄 "+foodData.nutri1.toString()+"Kcal"
                cardDanTv.text = "단 "+foodData.nutri2.toString()+"Kcal"
                cardJiTv.text = "지 "+foodData.nutri3.toString()+"Kcal"
            }
        }
    }
}