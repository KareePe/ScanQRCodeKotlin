package com.example.stockcartkn

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.lang.Exception

class InsertActivity : AppCompatActivity() {

    internal var InsertURL = "http://10.4.22.72/registerdemo/insert.php"
    private var vinno: EditText? = null
    private var kakuid: EditText? = null
    private var stockloid: Spinner? = null
    private var parkinglot: TextView? =null
    private var btnInsert: Button? = null
    private var editText3 : TextView ?= null
    private var preferenceHelper: PreferenceHelper? = null
    private var InsertTask = 1
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        preferenceHelper = PreferenceHelper(this)

        var intent = intent
        val code = intent.getStringExtra("Code")

        val resultTv = findViewById<TextView>(R.id.vinno)
        resultTv.text = code

        editText3 = findViewById<View>(R.id.editText3) as EditText
        editText3!!.text = preferenceHelper!!.getNames()

        val backBtn = findViewById<ImageButton>(R.id.imageButtonBack)
        backBtn.setOnClickListener {
            val intent = Intent(this@InsertActivity,MainActivity::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout!!.setOnClickListener {
            preferenceHelper!!.putIsLogin(false)
            val intent = Intent(this@InsertActivity,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Toast.makeText(this@InsertActivity,"Logout Success!",Toast.LENGTH_SHORT).show()
            this@InsertActivity.finish()
        }

        vinno = findViewById<View>(R.id.vinno) as EditText
        kakuid = findViewById<View>(R.id.kakuid) as EditText
        stockloid = findViewById<View>(R.id.stockloid) as Spinner
        parkinglot = findViewById<View>(R.id.parkinglot) as EditText
        btnInsert = findViewById<View>(R.id.btnInsert) as Button

        val options = arrayOf("1 // SURE","2 // HO","3 // NR","4 // MR","5 // BD","6 // TKW")
        stockloid!!.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options)

        btnInsert!!.setOnClickListener {
            try{
                insertdata()
            }catch(e: IOException){
                e.printStackTrace()
            }catch (e: JSONException){
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun insertdata(){
        showSimpleProgressDialog(this@InsertActivity,null,"Loading...",false)

        try{
            Fuel.post(InsertURL, listOf("user" to editText3!!.text.toString()
                                        ,"vinno" to vinno!!.text.toString()
                                        ,"kakudai" to kakuid!!.text.toString()
                                        ,"stocklocation" to stockloid!!.selectedItem.toString()
                                        ,"parkinglot" to parkinglot!!.text.toString()
            )).responseJson { request, response, result ->
                Log.d("plzzzz",result.get().content)
                onTaskCompleted(result.get().content,InsertTask)
            }
        }catch (e: Exception){

        }finally {

        }
    }

    private fun onTaskCompleted(response:String,task:Int){
        Log.d("responsejson",response)
        removeSimpleProgressDialog()
        when(task){
            InsertTask -> if(isSuccess(response)){
                saveInfo(response)
                Toast.makeText(this@InsertActivity,"Insert Successfully!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@InsertActivity,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            }else{
                Toast.makeText(this@InsertActivity,getErrorMessage(response),Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveInfo(response: String){
        preferenceHelper!!.putIsLogin(true)
        try{
            val jsonObject = JSONObject(response)
            if(jsonObject.getString("status") == "true"){
                val dataArray = jsonObject.getJSONArray("data")
                for(i in 0 until dataArray.length()){
                    val dataobj = dataArray.getJSONObject(i)
                    preferenceHelper!!.putIsCheckVinno(dataobj.getString("vin_no"))
                    preferenceHelper!!.putkakudai(dataobj.getString("kakudai_no"))
                    preferenceHelper!!.putLcStockID(dataobj.getString("stocklocation_id"))
                    preferenceHelper!!.putParklot(dataobj.getString("parkinglot"))
                }
            }
        }catch (e: JSONException){
            e.printStackTrace()
        }
    }

    fun isSuccess(response: String): Boolean{
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
