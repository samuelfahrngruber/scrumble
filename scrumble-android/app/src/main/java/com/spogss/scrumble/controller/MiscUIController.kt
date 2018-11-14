package com.spogss.scrumble.controller

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import com.spogss.scrumble.R
import com.spogss.scrumble.activity.MainActivity

object MiscUIController {
    fun showError(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun startLoadingAnimation(view: View, context: Context) {
        val progressBarHolder = view.findViewById<FrameLayout>(R.id.progress_bar_holder)
        progressBarHolder.visibility = View.VISIBLE
        (context as MainActivity).window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun stopLoadingAnimation(view: View, context: Context) {
        val progressBarHolder = view.findViewById<FrameLayout>(R.id.progress_bar_holder)
        progressBarHolder.visibility = View.GONE
        (context as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}