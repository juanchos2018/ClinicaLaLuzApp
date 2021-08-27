package com.clinicalaluz.clinicaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*

class LogutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logut)
    }



//    private var loginButton: LoginButton? = null
//    private var callbackManager: CallbackManager? = null
//    fun ss(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(android.R.layout.activity_login)
//        loginButton = findViewById<View>(android.R.id.login_button) as LoginButton
//        loginButton.setReadPermissions(
//            Arrays.asList(
//                "public_profile",
//                "email",
//                "user_birthday",
//                "user_friends"
//            )
//        )
//        callbackManager = CallbackManager.Factory.create()
//        // Callback registration
//        loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(loginResult: LoginResult) {
////ode
//                val request = GraphRequest.newMeRequest(
//                    loginResult.accessToken
//                ) { `object`, response ->
//                    Log.v("LoginActivity", response.toString())
//                    // Application code
//                    val email = `object`.getString("email")
//                    val birthday = `object`.getString("birthday")
//                    // 01/31/1980 format
//                }
//                val parameters = Bundle()
//                parameters.putString("fields", "id,name,email,gender,birthday")
//                request.parameters = parameters
//                request.executeAsync()
//            }
//
//            override fun onCancel() { // App code
//                Log.v("LoginActivity", "cancel")
//            }
//
//            override fun onError(exception: FacebookException) {
//
//                Log.v("LoginActivity", exception.cause.toString())
//            }
//        })
//    }



}