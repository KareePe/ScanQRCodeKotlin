package com.example.stockcartkn

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.stockcartkn.MainActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    internal var LoginURL = "http://10.4.22.72/registerdemo/login.php"
    private var etusername: EditText? = null
    private var etpassword: EditText? = null
    private var btnlogin: Button? = null
//    private var tvreg: TextView? = null
    private val LoginTask = 1
    private var preferenceHelpers : PreferenceHelper? = null
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferenceHelpers = PreferenceHelper(this)

        etusername = findViewById<View>(R.id.etusername) as EditText
        etpassword = findViewById<View>(R.id.etpassword) as EditText

        btnlogin = findViewById<Button>(R.id.btnSerach) as Button
//        tvreg = findViewById<View>(R.id.tvreg) as TextView

//        tvreg!!.setOnClickListener {
//            val intent = Intent(this@LoginActivity,MainActivity::class.java)
//            startActivity(intent)
//        }

        btnlogin!!.setOnClickListener {
            try{
                login()
            }catch (e: IOException){
                e.printStackTrace()
            }catch (e: JSONException){
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun login() {
        showSimpleProgressDialog(this@LoginActivity,null,"Loading....",false)

        try{
            Fuel.post(LoginURL, listOf(
                "username" to etusername!!.text.toString()
                ,"password" to etpassword!!.text.toString()
            )).responseJson{ request, response, result ->
                Log.d("plzzzz",result.get().content)
                onTaskCompleted(result.get().content,LoginTask)
            }
        }catch (e: Exception){

        }finally {

        }
    }

    private fun onTaskCompleted(response: String, task: Int){
        Log.d("responsejson",response)
        removeSimpleProgressDialog()
        when(task){
            LoginTask -> if (isSuccess(response)){
                saveInfo(response)
                Toast.makeText(this@LoginActivity,"Login Successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity,MainActivity::class.java)

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            }else{
                Toast.makeText(this@LoginActivity,getErrorMessage(response),Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun  saveInfo(response: String){
        preferenceHelpers!!.putIsLogin(true)
        try{
            val jsonObject = JSONObject(response)
            if(jsonObject.getString("status") == "true"){
                val dataArray = jsonObject.getJSONArray("data")
                for(i in 0 until dataArray.length()){
                    val dataobj = dataArray.getJSONObject(i)
                    preferenceHelpers!!.putName(dataobj.getString("username"))
                    preferenceHelpers!!.putHobby(dataobj.getString("first_name"))
                }
            }
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }

    fun isSuccess(response: String): Boolean {
        try {
            val jsonObject = JSONObject(response)
            return if (jsonObject.optString("status") == "true") {
                true
            } else {

                false
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return false
    }

    fun getErrorMessage(response: String): String {
        try {
            val jsonObject = JSONObject(response)
            return jsonObject.getString("message")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return "No data"
    }

    fun showSimpleProgressDialog(context: Context, title: String?, msg: String, isCancelable: Boolean) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg)
                mProgressDialog!!.setCancelable(isCancelable)
            }
            if (!mProgressDialog!!.isShowing) {
                mProgressDialog!!.show()
            }

        } catch (ie: IllegalArgumentException) {
            ie.printStackTrace()
        } catch (re: RuntimeException) {
            re.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog!!.isShowing) {
                    mProgressDialog!!.dismiss()
                    mProgressDialog = null
                }
            }
        } catch (ie: IllegalArgumentException) {
            ie.printStackTrace()

        } catch (re: RuntimeException) {
            re.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
