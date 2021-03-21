package com.example.kidsdrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.kidsdrawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibBrush.setOnClickListener {

            showBrushSizeChooserDialog()

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

}