package com.example.animebook

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.animebook.databinding.ActivityAnimeBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class AnimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedBitmap : Bitmap? = null

    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("Animes", MODE_PRIVATE,null)

        registerLauncher()

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info.equals("new")){
            binding.animeNameText.setText("")
            binding.authorNameText.setText("")
            binding.yearText.setText("")

            val selectedImageBg = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectimage)
            binding.selectImageView.setImageBitmap(selectedImageBg)

            binding.saveBtn.visibility = View.VISIBLE
            binding.updateBtn.visibility = View.INVISIBLE

        } else{
            binding.saveBtn.visibility = View.INVISIBLE
            binding.updateBtn.visibility = View.VISIBLE

            val selectedId = intent.getIntExtra("id",0)

            val cursor = database.rawQuery("select * from animes where id = ?", arrayOf(selectedId.toString()))
            val animeNameIx = cursor.getColumnIndex("animename")
            val authorNameIx = cursor.getColumnIndex("authorname")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()){
                binding.animeNameText.setText(cursor.getString(animeNameIx))
                binding.authorNameText.setText(cursor.getString(authorNameIx))
                binding.yearText.setText(cursor.getString(yearIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.selectImageView.setImageBitmap(bitmap)
            }
            cursor.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.delete_anime, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_anime){
            val selectedId = intent.getIntExtra("id",0)
            database.execSQL("delete from animes where id = ? ", arrayOf(selectedId.toString()))

            val intent = Intent(this@AnimeActivity, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun saveButtonClicked(view : View){
        val animeName = binding.animeNameText.text.toString()
        val authorName = binding.authorNameText.text.toString()
        val year = binding.yearText.text.toString()

        if (selectedBitmap != null){
            val smallBitmap = makeSmallBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap!!.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                database.execSQL("create table if not exists animes (id integer primary key, animename varchar, authorname varchar, year varchar, image blob)")

                val sqlString = "insert into animes (animename, authorname, year, image) values (?,?,?,?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, animeName)
                statement.bindString(2, authorName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)
                statement.execute()

            } catch (e: Exception){
                e.printStackTrace()
            }

            val intent = Intent(this@AnimeActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }
    }

    fun updateButtonClicked(view: View){
        val selectedId = intent.getIntExtra("id",0)
        val animeName = binding.animeNameText.text.toString()
        val authorName = binding.authorNameText.text.toString()
        val year = binding.yearText.text.toString()

        if (selectedBitmap != null){
            val smallBitmap = makeSmallBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap!!.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                database.execSQL("create table if not exists animes (id integer primary key, animename varchar, authorname varchar, year varchar, image blob)")

                val sqlString = "update animes set animename = ?, authorname = ?, year = ?, image = ? where id = ?"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, animeName)
                statement.bindString(2, authorName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)
                statement.bindDouble(5,selectedId.toDouble())
                statement.execute()

            } catch (e: Exception){
                e.printStackTrace()
            }

            val intent = Intent(this@AnimeActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        } else{
            val cursor = database.rawQuery("select image from animes where id = ?", arrayOf(selectedId.toString()))
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()){
                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.selectImageView.setImageBitmap(bitmap)
            }
            cursor.close()

            try {

                database.execSQL("create table if not exists animes (id integer primary key, animename varchar, authorname varchar, year varchar, image blob)")

                val sqlString = "update animes set animename = ?, authorname = ?, year = ? where id = ?"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, animeName)
                statement.bindString(2, authorName)
                statement.bindString(3, year)
                statement.bindDouble(4,selectedId.toDouble())
                statement.execute()

            } catch (e: Exception){
                e.printStackTrace()
            }

            val intent = Intent(this@AnimeActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }

    }

    private fun makeSmallBitmap(image : Bitmap, maximumSize : Int): Bitmap? {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble()/ height.toDouble()

        if (bitmapRatio > 1 ){
            width = maximumSize
            val scaledHeight = width/bitmapRatio
            height = scaledHeight.toInt()
        }else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun selectedImage(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this@AnimeActivity,android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@AnimeActivity,android.Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                }else {
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else {
                //intent To Gallery
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }else{
            if (ContextCompat.checkSelfPermission(this@AnimeActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@AnimeActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else {
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else {
                //intent To Gallery
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }



    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(this@AnimeActivity.contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.selectImageView.setImageBitmap(selectedBitmap)
                        }else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this@AnimeActivity.contentResolver, imageData)
                            binding.selectImageView.setImageBitmap(selectedBitmap)
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }

            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else {
                //permission denied
                Toast.makeText(this@AnimeActivity, "Permission needed!!", Toast.LENGTH_LONG).show()
            }
        }
    }
}