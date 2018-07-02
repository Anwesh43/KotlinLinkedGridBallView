package com.anwesh.uiprojects.kotlinlinkedgridballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linkedgridballview.LinkedGridBallView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedGridBallView.create(this)
    }
}
