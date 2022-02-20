package com.clinicalaluz.clinicaapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.clinicalaluz.clinicaapp.databinding.ActivityLoginBinding
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    private  lateinit var mCallbackManager :CallbackManager
    val RC_SIGN_IN =0

    lateinit  var mGoogleSignInClient : GoogleSignInClient
    lateinit var  gso : GoogleSignInOptions
    private lateinit var  acctount : GoogleSignIn
    private  lateinit var cuenta :GoogleSignInAccount
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            val user = binding.loginUser.text.toString()
            val pass = binding.loginPass.text.toString()

            if (TextUtils.isEmpty(user)){
                binding.loginUser.setError("El documento no puede star vacio")

            }else if (TextUtils.isEmpty(pass)){
                binding.loginPass.setError("La contraseña no puede estar vacio")
            }
            else{

                var progres = ProgressDialog(this)
                progres.setMessage("Cargando...")
                progres.show()

                val url = "http://161.132.198.52:8080/app_laluz/pdoLogin.php?doc=$user&clave=$pass"
                val rq = Volley.newRequestQueue(this)
                val jst = JsonObjectRequest(
                    Request.Method.GET, url,null,
                    { response: JSONObject ->
                        val json = response.optJSONArray("usuario")
                        Log.e("respsonse ", json.toString())
                        progres.dismiss()
                        var jsonObject: JSONObject? = null
                        try {
                            jsonObject = json.getJSONObject(0)
                            var existe =jsonObject.optString("existe")
                            var logueo =jsonObject.optString("LOGUEO")

                            if (existe.equals("existe")){
                                var nombres =jsonObject.optString("NOM_PACIENTE")
                                var  apellidos =jsonObject.optString("APE_PATERNO")
                                var  id=jsonObject.optString("COD_PACIENTE")
                                var  estado =jsonObject.optString("ESTADO")
                                var  correo =jsonObject.optString("CORREO")
                                var  cod_auxiliar =jsonObject.optString("COD_AUXILIAR")
                            //    if (estado.equals("1")){
                                if (logueo=="gmail"){
                                    Toast.makeText(this, "Usted se logueo con su Gmail ", Toast.LENGTH_SHORT).show()

                                }else{
                                    val preferences: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                                    val editor = preferences.edit()
                                    editor.putString("id", id)
                                    editor.putString("nombres", nombres)
                                    editor.putString("dni", user)
                                    editor.putString("cod_auxiliar", cod_auxiliar)
                                    editor.putString("NUM_DOC_IDENTIDAD", user)
                                    editor.putString("correo", correo)
                                    editor.putString("logueo", "dni")
                                    if(binding.idcheckorecordar.isChecked()){
                                        editor.putString("recordar", "true")
                                    }else{
                                        editor.putString("recordar", "false")
                                    }
                                    editor.putString("nombres", nombres)
                                    editor.putString("apellidos", apellidos)
                                    editor.putString("clave", pass)
                                    editor.commit()

                                    val intent = Intent(this@LoginActivity, MenuNuevoActivity::class.java)
                                    intent.putExtra("TipoSesion","dni")
                                    startActivity(intent)
                                   // Toast.makeText(this, "Bienvenido "+nombres, Toast.LENGTH_SHORT).show()
                                    finish()

                                }
                               // }else{
                              //      warning("Tu correo no esta verificado")
                               // }

                            }else{
                                if (logueo=="gmail"){
                                    Toast.makeText(this, "Usted se logueo con su Gmail ", Toast.LENGTH_SHORT).show()
                                }else if(logueo=="facebook"){
                                    Toast.makeText(this, "Usted se logueo con su Facebook ", Toast.LENGTH_SHORT).show()

                                }else{
                                    fail("No Existe Usuario")
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            progres.dismiss()
                        }
                    },
                    object : Response.ErrorListener {
                        override fun onErrorResponse(volleyError: VolleyError) {
                            progres.dismiss()
                            Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                        }
                    })
                rq.add(jst)
            }
        }
        binding.returnMain.setOnClickListener {
            finish()
        }

        //Gmail login
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso)

        var acct1 = GoogleSignIn.getLastSignedInAccount(this)
        if (acct1 != null) {

            val intent = Intent(this, MenuNuevoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("TipoSesion","gmail")
            startActivity(intent)

            finish()
        }
        else if(AccessToken.getCurrentAccessToken() != null){
            goMainScreen2()

        }
        else {

            val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
            var dniuser = preferences.getString("dni", null)
            var logueo = preferences.getString("logueo", null)
            if (dniuser!=null){
                var recordar = preferences.getString("recordar", null)
                var password = preferences.getString("clave", null)
                if(recordar.equals("true")){
                    binding.loginUser.setText(dniuser)
                    binding.loginPass.setText(password)
                    binding.idcheckorecordar.isChecked=true
                }
                if (logueo=="dni"){
                    val intent = Intent(this, MenuNuevoActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("TipoSesion","dni")
                    startActivity(intent)
                    finish()
                }
            }else{
               // Toast.makeText(this, "Datos vacios (DNI) ", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        binding.signInButton.setOnClickListener{
            val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        //facebbok
        mCallbackManager = CallbackManager.Factory.create()

        binding.loginButton.setReadPermissions("email", "public_profile", "user_friends")
        binding.loginButton.registerCallback(mCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("msg", "facebook:onSuccess:$loginResult")
                Log.d("token", "tok :"+loginResult.accessToken)
                Log.d("GlobalVars.TAG", "Tokenssss::" + loginResult.getAccessToken().getToken())
                val request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken()
                ) { `object`, response ->
                    if (response != null) {
                        try {

                            val preferences: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("logueo", "facebook")
                            editor.commit()

                            var email =""
                            var id = ""
                            var birthday=""
                            if (  `object`.getString("email")!=null){
                                email = `object`.getString("email")
                            }
                            if ( `object`.getString("birthday")!=null)
                            {
                                birthday =`object`.getString("birthday")
                            }
                            id =  `object`.getString("id")

                            val number = `object`.getString("phone")

                            handleSingFacebook(number,birthday,id,email)
                           // Toast.makeText(applicationContext, "Ca :"+ number, Toast.LENGTH_LONG).show()

                        } catch (e: JSONException) {

                            Toast.makeText(applicationContext, "Ca :"+ e.message, Toast.LENGTH_LONG).show()
                            Log.e("errr",e.message.toString())
                        }
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "email,birthday")
                request.parameters = parameters
                request.executeAsync()
          //      Toast.makeText(applicationContext, "facebook:onSuccess:$loginResult", Toast.LENGTH_LONG).show()
               // goMainScreen(loginResult.accessToken)
        }
            override fun onCancel() {
                Log.d("ms", "facebook:onCancel")
                Toast.makeText(applicationContext, "facebook:onCancel", Toast.LENGTH_LONG).show()
            }
            override fun onError(error: FacebookException) {
                Log.d("TAG", "facebook:onError", error)
                Toast.makeText(applicationContext, "facebook:onError "+error, Toast.LENGTH_LONG).show()
            }
        })

    }


    private fun goMainScreen(token: AccessToken,email:String) {
//        var birthday:String=""
//        val request = GraphRequest.newMeRequest(
//            token
//        ) { `object`, response ->
//            if (response != null) {
//                try {
//
//                    val email = `object`.getString("email")
//                    Toast.makeText(applicationContext, "ee: "+email, Toast.LENGTH_LONG).show()
//
//                    val intent = Intent(this, MenuNuevoActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    intent.putExtra("TipoSesion","facebook")
//                    intent.putExtra("emm",birthday)
//                    startActivity(intent)
//                } catch (e: JSONException) {
//                    Toast.makeText(applicationContext, "Ca :"+ e.message, Toast.LENGTH_LONG).show()
//                   Log.e("errr",e.message.toString())
//                }
//            }
//        }
//        val parameters = Bundle()
//        parameters.putString("fields", "email")
//        request.parameters = parameters
//        request.executeAndWait()

        // Toast.makeText(applicationContext, "ee: "+email, Toast.LENGTH_LONG).show()
        val preferences: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("logueo", "facebook")
        editor.commit()

        val intent = Intent(this, MenuNuevoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("TipoSesion","facebook")
        intent.putExtra("email",email)
        startActivity(intent)


//            val request = GraphRequest.newMeRequest( token) { _, response ->
//                val jsonObject = response.jsonObject
//                try {
//
//                    birthday = jsonObject.getString("email")
//                    Toast.makeText(applicationContext, "cumple: "+birthday, Toast.LENGTH_LONG).show()
//
//                    val intent = Intent(this, MenuNuevoActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    intent.putExtra("TipoSesion","facebook")
//                    intent.putExtra("emm",birthday)
//                    startActivity(intent)
//
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                    Toast.makeText(applicationContext, "Ca :"+ e.message, Toast.LENGTH_LONG).show()
//                    Log.e("errr",e.message.toString())
//                }
//            }
//            request.executeAsync();


//        val preferences: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
//        val editor = preferences.edit()
//        editor.putString("logueo", "facebook")
//        editor.commit()
//
//        val intent = Intent(this, MenuNuevoActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.putExtra("TipoSesion","facebook")
//        intent.putExtra("token",birthday)
//        startActivity(intent)
    }


    private fun goMainScreen2() {
        val intent = Intent(this, MenuNuevoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("TipoSesion","facebook")
        startActivity(intent)
        finish()
    }


    override fun onStart() {
        super.onStart()

       // mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
       // if (cuenta != null) {
       //     val intent = Intent(this, MenuActivity::class.java)
       //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
       //     intent.putExtra("TipoSesion","gmail")
       //     startActivity(intent)
       // }else if  (true){
       // }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

        //gemail
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else{
            //facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

//        if (requestCode == RC_SIGN_IN) {
//
//            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }else{
//            //mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }
    }

    private  fun handleSingFacebook(number:String,birthday:String,id:String,personEmail:String){
        //   handleSingFacebook(number,birthday,id,email)
        try {

            var progres = ProgressDialog(this)
            progres.setMessage("Cargando...")
            progres.show()

            val url = "http://161.132.198.52:8080/app_laluz/pdoSelectUser.php?CORREO=$personEmail"
            val rq = Volley.newRequestQueue(this)
            val jst = JsonObjectRequest(
                Request.Method.GET, url,null,
                { response: JSONObject ->
                    val json = response.optJSONArray("usuario")
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = json.getJSONObject(0)
                        var existe =jsonObject.optString("existe")
                        if (existe.equals("existe")){
                            var COD_PACIENTE =jsonObject.optString("COD_PACIENTE")
                            var NUM_HC=jsonObject.optString("NUM_HC")
                            var nombres =jsonObject.optString("NOM_PACIENTE")
                            val preferences: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("id", COD_PACIENTE)
                            editor.putString("nombres", nombres)
                            editor.putString("dni", NUM_HC)
                            editor.putString("cumple", birthday)
                            editor.putString("logueo", "facebook")
                            editor.putString("correo", personEmail)
                            editor.commit()

                            val intent = Intent(this@LoginActivity, MenuNuevoActivity::class.java)
                            intent.putExtra("TipoSesion","facebook")
                            startActivity(intent)
                            finish()

                        }else{

                            val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
                            preferences.edit().remove("dni").commit()

                            val preferences2: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences2.edit()
                            editor.putString("logueo", "facebook")
                            editor.commit()
                            finish()
                            val intent = Intent(this@LoginActivity, MenuNuevoActivity::class.java)
                            intent.putExtra("TipoSesion","facebook")
                            startActivity(intent)
                        }
                        progres.dismiss()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progres.dismiss()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                        progres.dismiss()
                        Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                    }
                })
            rq.add(jst)
        } catch (e: ApiException) {
            Log.w("mensaje error", "signInResult:failed code=" + e.statusCode)

        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val personEmail = account.email
            var progres = ProgressDialog(this)
            progres.setMessage("Cargando...")
            progres.show()

            val url = "http://161.132.198.52:8080/app_laluz/pdoSelectUser.php?CORREO=$personEmail"
            val rq = Volley.newRequestQueue(this)
            val jst = JsonObjectRequest(
                Request.Method.GET, url,null,
                { response: JSONObject ->
                    val json = response.optJSONArray("usuario")
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = json.getJSONObject(0)
                        var existe =jsonObject.optString("existe")
                        if (existe.equals("existe")){
                            var COD_PACIENTE =jsonObject.optString("COD_PACIENTE")
                            var NUM_HC=jsonObject.optString("NUM_HC")
                            var nombres =jsonObject.optString("NOM_PACIENTE")
                            val preferences: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("id", COD_PACIENTE)
                            editor.putString("nombres", nombres)
                            editor.putString("dni", NUM_HC)
                            editor.putString("logueo", "gmail")
                            editor.putString("correo", personEmail)
                            editor.commit()

                            val intent = Intent(this@LoginActivity, MenuNuevoActivity::class.java)
                            intent.putExtra("TipoSesion","gmail")
                            startActivity(intent)
                            finish()

                        }else{

                            val preferences = getSharedPreferences("datosuser", MODE_PRIVATE)
                            preferences.edit().remove("dni").commit()

                            val preferences2: SharedPreferences = this@LoginActivity.getSharedPreferences("datosuser", MODE_PRIVATE)
                            val editor = preferences2.edit()
                            editor.putString("logueo", "gmail")
                            editor.commit()

                            val intent = Intent(this@LoginActivity, MenuNuevoActivity::class.java)
                            intent.putExtra("TipoSesion","gmail")
                            startActivity(intent)
                            finish()
                        }
                        progres.dismiss()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progres.dismiss()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                        progres.dismiss()
                        Toast.makeText(applicationContext, "error,"+volleyError.toString()+"", Toast.LENGTH_LONG).show()
                    }
                })
            rq.add(jst)
        } catch (e: ApiException) {
            Log.w("mensaje error", "signInResult:failed code=" + e.statusCode)
        }
    }
    private fun warning(mensaje: String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        val  v = LayoutInflater.from(applicationContext).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_warning, null)
        val animationView: LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_viewcheck)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje)
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    private fun fail(mensaje: String){
        val dialogBuilder = AlertDialog.Builder(this)
        val btcerrrar: Button
        val tvmensaje: TextView
        val  v = LayoutInflater.from(applicationContext).inflate(com.clinicalaluz.clinicaapp.R.layout.dialogo_fail, null)
        val animationView: LottieAnimationView = v.findViewById(com.clinicalaluz.clinicaapp.R.id.animation_fail)
        animationView.playAnimation()
        dialogBuilder.setView(v)
        btcerrrar = v.findViewById(com.clinicalaluz.clinicaapp.R.id.idbtncerrardialogo2) as Button
        tvmensaje = v.findViewById(com.clinicalaluz.clinicaapp.R.id.tvmensaje2)
        tvmensaje.text = mensaje
        val alert = dialogBuilder.create()
        btcerrrar.setOnClickListener {
            alert.dismiss()
        }
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

}