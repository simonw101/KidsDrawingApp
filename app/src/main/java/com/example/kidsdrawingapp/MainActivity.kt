package com.example.kidsdrawingapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.kidsdrawingapp.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var mImageButtonCurrentPaint : ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.setSizeForBrush(20.toFloat())

        mImageButtonCurrentPaint = binding.llPaintColors[1] as? ImageButton

        mImageButtonCurrentPaint!!.setImageDrawable(

                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)

        )

        binding.ibBrush.setOnClickListener {

            showBrushSizeChooserDialog()

        }

        binding.ibGallery.setOnClickListener {

            if (isReadStorageAllowed()) {

                //  run our code to get the image from the gallery

                val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(pickPhotoIntent, GALLERY)

            } else {

                requestStoragePermission()

            }

        }

        binding.ibUndo.setOnClickListener {

            binding.drawingView.onClickUndo()

        }

        binding.ibSave.setOnClickListener {

            if (isReadStorageAllowed()) {

                BitmapAsyncTask(getBitMapFromView(binding.flDrawingViewContainer)).execute()

            } else {

                requestStoragePermission()

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GALLERY) {

                try {

                    if (data!!.data != null) {

                        binding.ivBackground.visibility = View.VISIBLE

                        binding.ivBackground.setImageURI(data.data)

                    } else {

                        Toast.makeText(this@MainActivity, "Error in parsing image", Toast.LENGTH_LONG).show()

                    }

                } catch (e: Exception) {

                    e.printStackTrace()

                }

            }

        }

    }

    private fun showBrushSizeChooserDialog() {

        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")

        val smallBtn = findViewById<ImageButton>(R.id.ib_small_brush)
        smallBtn.setOnClickListener {

            binding.drawingView.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()

        }

        val mediumBtn = findViewById<ImageButton>(R.id.ib_medium_brush)
        smallBtn.setOnClickListener {

            binding.drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()

        }

        val largeBtn = findViewById<ImageButton>(R.id.ib_large_brush)
        smallBtn.setOnClickListener {

            binding.drawingView.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()

        }

        brushDialog.show()

    }

    fun paintClicked(view: View) {

        if (view != mImageButtonCurrentPaint) {

            val imageButton = view as ImageButton

            val colorTag = imageButton.tag.toString()

            binding.drawingView.setColor(colorTag)

            imageButton.setImageDrawable(

                    ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )

            mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_normal))
            mImageButtonCurrentPaint = view
        }

    }

    private fun requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())) {

            Toast.makeText(this, "We need storage permission", Toast.LENGTH_LONG).show()

        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted you can know", Toast.LENGTH_LONG).show()

            } else {

                Toast.makeText(this, "Oops you have just denied the permission", Toast.LENGTH_LONG).show()

            }

        }

    }

    private fun isReadStorageAllowed() : Boolean {

        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED

    }

    private fun getBitMapFromView(view: View) : Bitmap {

        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(returnedBitmap)

        val bgDrawable = view.background

        if (bgDrawable != null) {

            bgDrawable.draw(canvas)

        } else {

            canvas.drawColor(Color.WHITE)

        }

        view.draw(canvas)

        return returnedBitmap

    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap): AsyncTask<Any, Void, String>() {

        override fun doInBackground(vararg params: Any?): String {

            var result = ""

            if (mBitmap != null) {

                try {

                    val bytes = ByteArrayOutputStream()

                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    val f = File(externalCacheDir!!.absoluteFile.toString() + File.separator +

                            "kidsDrawingApp_" + System.currentTimeMillis() / 1000 + ".png"

                    )

                    val fos = FileOutputStream(f)

                    fos.write(bytes.toByteArray())

                    fos.close()

                    result = f.absolutePath

                } catch (e: Exception) {

                    result = ""

                    e.printStackTrace()

                }

            }

            return result

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result!!.isNotEmpty()) {

                Toast.makeText(this@MainActivity, "File save successfully $result", Toast.LENGTH_LONG).show()

            } else {

                Toast.makeText(this@MainActivity, "Something went wrong while saving the file", Toast.LENGTH_LONG).show()

            }
        }


    }

    companion object {

        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2

    }

}