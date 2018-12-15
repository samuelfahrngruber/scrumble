package com.spogss.scrumble.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.spogss.scrumble.R
import com.spogss.scrumble.controller.ScrumbleController
import com.spogss.scrumble.controller.SharedPreferencesController
import com.spogss.scrumble.data.User
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private var login = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()

        if(intent.getBooleanExtra("logout", false)) {
            SharedPreferencesController.deleteCredentialsFromSharedPreferences(this)
            initUI()
        }
        else {
            val user = SharedPreferencesController.loadCredentialsFromSharedPreferences(this)
            if (user == null)
                initUI()
            else
                login(user.name, user.password, true)
        }
    }

    private fun initUI(){
        setContentView(R.layout.activity_login)

        text_view_switch_login_register.setOnClickListener { switchLoginRegister() }
        login_button.setOnClickListener {
            if(validate()) {
                if (login)
                    login(login_username.text.toString().trim(), login_password.text.toString(), false)
                else
                    signUp(login_username.text.toString().trim(), login_password.text.toString())
            }

            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun switchLoginRegister() {
        if(login) {
            login_password_confirm.visibility = View.VISIBLE
            login_password_confirm.setText("")
            text_view_switch_login_register.text = resources.getString(R.string.sign_up_message)
            login_button.text = resources.getString(R.string.sign_up)
        }
        else {
            login_password_confirm.visibility = View.GONE
            text_view_switch_login_register.text = resources.getString(R.string.login_message)
            login_button.text = resources.getString(R.string.login)
        }
        login_username.setText("")
        login_password.setText("")
        login = !login
    }

    private fun login(username: String, password: String, automatic: Boolean) {
            val user = User(-1, username, password)

            if(!automatic) {
                login_button.startAnimation()
                disableEverything()
            }
            ScrumbleController.login(user, {
                user.id = it
                ScrumbleController.currentUser = user

                SharedPreferencesController.saveCredentialsToSharedPreferences(user.name, user.password, this)

                startMainActivity()
            }, {
                if(!automatic) {
                    login_username.error = it
                    login_password.error = it
                    login_button.revertAnimation()
                    enableEverything()
                }
                else
                    initUI()
            })
    }

    private fun signUp(username: String, password: String) {
            login_button.startAnimation()

            val user = User(-1, username, password)

            disableEverything()
            ScrumbleController.register(user, {
                user.id = it
                ScrumbleController.currentUser = user

                startMainActivity()
            }, {
                login_username.error = it
                login_button.revertAnimation()
                enableEverything()
            })
    }

    private fun validate(): Boolean {
        val username = login_username.text.toString().trimStart().trimEnd()
        val password = login_password.text.toString()

        var success = true

        if(username.length < 3 || username.length > 30) {
            login_username.error = resources.getString(R.string.error_username)
            success = false
        }
        if(password.length < 5 || password.length > 30) {
            login_password.error = resources.getString(R.string.error_password)
            success = false
        }
        if(username.contains(" ")) {
            login_username.error = "No whitespaces please"
            success = false
        }
        if(!login && (login_password_confirm.text.toString().trimStart().trimEnd().isEmpty() || login_password_confirm.text.toString().trimStart().trimEnd() != password)) {
            login_password_confirm.error = resources.getString(R.string.error_confirm_password)
            success = false
        }

        return success
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun disableEverything() {
        this.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun enableEverything() {
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
