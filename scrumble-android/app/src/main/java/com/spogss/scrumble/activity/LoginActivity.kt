package com.spogss.scrumble.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.spogss.scrumble.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var login = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()

        text_view_switch_login_register.setOnClickListener { switchLoginRegister() }
        login_button.setOnClickListener {
            if(login)
                login()
            else
                signUp()
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

    private fun login() {
        if(validate()) {
            login_button.startAnimation()
            Toast.makeText(this, "login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUp() {
        if(validate()) {
            login_button.startAnimation()
            Toast.makeText(this, "register", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(): Boolean {
        val username = login_username.text.toString().trimStart().trimEnd()
        val password = login_password.text.toString().trimStart().trimEnd()

        var success = true

        if(username.length < 3 || username.length > 30) {
            login_username.error = resources.getString(R.string.error_username)
            success = false
        }
        if(password.length < 5 || password.length > 30) {
            login_password.error = resources.getString(R.string.error_password)
            success = false
        }
        if(!login && (login_password_confirm.text.toString().trimStart().trimEnd().isEmpty() || login_password_confirm.text.toString().trimStart().trimEnd() != password)) {
            login_password_confirm.error = resources.getString(R.string.error_confirm_password)
            success = false
        }

        return success
    }
}
