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

class ShowData : AppCompatActivity() {

    internal var UpdateURL = "http://10.4.22.72/registerdemo/update.php"
    private var tvUser: TextView? = null
    private var tvname: TextView? = null
    private var tvlcstock: Spinner? = null
    private var tvparkinglot: TextView? = null
    private var tvcarid: TextView? = null
    private var btnUpdate: Button? = null
    private var btnLogout: Button? = null
    private var preferenceHelper: PreferenceHelper? = null
    private val UpdateTask = 1
    private var mProgressDialog: ProgressDialog? = null

    private var option: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_data)

        preferenceHelper = PreferenceHelper(this)

        tvUser = findViewById<View>(R.id.tvUser) as EditText
        tvname = findViewById<View>(R.id.tvname) as EditText
        tvlcstock = findViewById<View>(R.id.tvlcstock) as Spinner
        tvparkinglot = findViewById<View>(R.id.tvparkinglot) as EditText
        tvcarid = findViewById<View>(R.id.tvcarid) as EditText


        btnUpdate = findViewById<View>(R.id.btnUpdate) as Button

        tvUser!!.text = preferenceHelper!!.getNames()
        tvname!!.text = preferenceHelper!!.getCheckVinno()
        tvparkinglot!!.text = preferenceHelper!!.getParkinglot()
        tvcarid!!.text = preferenceHelper!!.getCarid()


        val options = arrayOf(
            preferenceHelper!!.getlcstockID() + " // " + preferenceHelper!!.getlcstockdes()
            ,"1 // SURE"
            ,"2 // HO"
            ,"3 // NR"
            ,"4 // MR"
            ,"5 // BD"
            ,"6 // TKW"
        )
        tvlcstock!!.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options)




        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)

        btnLogout!!.setOnClickListener {
            preferenceHelper!!.putIsLogin(false)
            val intent = Intent(this@ShowData,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Toast.makeText(this@ShowData,"Logout Success!",Toast.LENGTH_SHORT).show()
            this@ShowData.finish()
        }

        val backBtn = findViewById<ImageButton>(R.id.imageButtonBack)
        backBtn.setOnClickListener {
            val intent = Intent(this@ShowData,MainActivity::class.java)
            startActivity(intent)
        }

        btnUpdate!!.setOnClickListener {
            try{
                update()
            }catch (e: IOException){
                e.printStackTrace()
            }catch (e: JSONException){
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun update(){
        showSimpleProgressDialog(this@ShowData,null,"Loading...",false)

        try{
            Fuel.post(UpdateURL, listOf("user" to tvUser!!.text.toString()
                                        ,"vinno" to tvname!!.text.toString()
                                        ,"stocklocationid" to tvlcstock!!.selectedItem.toString()
                                        ,"parkinglot" to tvparkinglot!!.text.toString()
                                        ,"carid" to tvcarid!!.text.toString()
                        )).responseJson { request, response, result ->
                Log.d("plzzzzz",result.get().content)
                onTaskCompleted(result.get().content,UpdateTask)
            }
        }catch (e: Exception){

        }finally {

        }
    }

    private fun onTaskCompleted(response:String,task:Int){
        Log.d("responsejson", response)
        removeSimpleProgressDialog()
        when(task){
            UpdateTask -> if (isSuccess(response)){
                saveInfo(response)
                Toast.makeText(this@ShowData,"Update Successfully!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ShowData,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            }else{
                Toast.makeText(this@ShowData,getErrorMessage(response),Toast.LENGTH_SHORT).show()
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
                    preferenceHelper!!.putUser(dataobj.getString("lastupdateuserid"))
                    preferenceHelper!!.putIsCheckVinno(dataobj.getString("vin_no"))
                    preferenceHelper!!.putLcStockID(dataobj.getString("stocklocation_id"))
                    preferenceHelper!!.putParklot(dataobj.getString("parkinglot"))
                    preferenceHelper!!.putCarid(dataobj.getString("car_id"))
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
