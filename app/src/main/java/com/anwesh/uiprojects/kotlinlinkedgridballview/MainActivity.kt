package com.anwesh.uiprojects.kotlinlinkedgridballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.linkedgridballview.LinkedGridBallView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LinkedGridBallView = LinkedGridBallView.create(this)
        fullScreen()
        view.addOnCompleteListener({Toast.makeText(this, "animation number ${it+1} completed", Toast.LENGTH_SHORT).show()}) {
            Toast.makeText(this, "animation number ${it+1} has reset", Toast.LENGTH_SHORT).show()
        }
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}