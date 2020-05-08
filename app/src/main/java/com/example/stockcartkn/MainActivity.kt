package com.example.stockcartkn

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity(), ResultHandler {

    // CHECK SCAN QR //
    private val REQUES_CAMERA = 1
    private var scannerView : ZXingScannerView?= null
    private var txtResult : TextView ?= null
    private var editText3 : TextView ?= null
    // CHECK SCAN QR //

    internal var CheckVinURL = "http://10.4.22.72/registerdemo/checkvinno.php"
    private var btnCheck: Button? = null
    private val CheckTask = 1
    private var preferenceHelper: PreferenceHelper? = null
    private var mProgressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceHelper = PreferenceHelper(this)

        editText3 = findViewById<View>(R.id.editText3) as EditText

        txtResult = findViewById<View>(R.id.txtResult) as EditText
        btnCheck = findViewById<Button>(R.id.btnCheck) as Button

        editText3!!.text = preferenceHelper!!.getNames()

        scannerView = findViewById(R.id.scanner)

        if(!checkPermission())
            requestPermission()

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)

        btnLogout!!.setOnClickListener {
            preferenceHelper!!.putIsLogin(false)
            val intent = Intent(this@MainActivity,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Toast.makeText(this@MainActivity,"Logout Success!",Toast.LENGTH_SHORT).show()
            this@MainActivity.finish()
        }

        btnCheck!!.setOnClickListener {
            try{
                checkvinno()
            }catch (e: IOException){
                e.printStackTrace()
            }catch (e: JSONException){
                e.printStackTrace()
            }
        }
    }

    private fun checkPermission() : Boolean{
        return ContextCompat.checkSelfPermission(this@MainActivity,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),REQUES_CAMERA)
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()){
            if (scannerView == null){
                scannerView = findViewById(R.id.scanner)
                setContentView(scannerView)
            }
            scannerView?.setResultHandler(this)
            scannerView?.startCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView?.stopCamera()
    }

    override fun handleResult(p0: Result?) {
        val result:String? = p0?.text
        val vibrator: Vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
        txtResult?.text = result
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()

//        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
//        builder.setTitle("Result")
//        builder.setPositiveButton("OK"){dialog, which ->
//            scannerView?.resumeCameraPreview(this@MainActivity)
//            startActivity(intent)
//        }
//        builder.setMessage(result)
//        val alert :AlertDialog = builder.create()
//        alert.show()
    }

    @Throws(IOException::class,JSONException::class)
    private fun checkvinno(){
        showSimpleProgressDialog(this@MainActivity,null,"Loading...",false)
        try{
            Fuel.post(CheckVinURL, listOf("vinno" to txtResult!!.text.toString()
            )).responseJson { request, response, result ->
                Log.d("plzzzzz",result.get().content)
                onTaskCompleted(result.get().content,CheckTask)
            }
        }catch (e: Exception){

        }finally {

        }
    }

    private fun onTaskCompleted(response: String,task: Int){
        Log.d("responsejson",response)
        removeSimpleProgressDialog()
        when(task){
            CheckTask -> if (isSuccess(response)){
                saveInfo(response)
                Toast.makeText(this@MainActivity,"Found Data!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity,ShowData::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            }else{
                Toast.makeText(this@MainActivity,getErrorMessage(response),Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveInfo(response: String){
        preferenceHelper!!.putIsLogin(true)
        try{
            val jsonObject = JSONObject(response)
            if(jsonObject.getString("status") == "true"){
                val dataArray = jsonObject.getJSONArray("data")
                for (i in 0 until dataArray.length()) {

                    val dataobj = dataArray.getJSONObject(i)
                    preferenceHelper!!.putUser(dataobj.getString("lastupdateuserid"))
                    preferenceHelper!!.putIsCheckVinno(dataobj.getString("vin_no"))
                    preferenceHelper!!.putLcStockID(dataobj.getString("stocklocation_id"))
                    preferenceHelper!!.putLcStockDes(dataobj.getString("locationstock"))
                    preferenceHelper!!.putParklot(dataobj.getString("parkinglot"))
                    preferenceHelper!!.putCarid(dataobj.getString("car_id"))
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
            val rscode = txtResult?.text.toString()
            val intent = Intent(this@MainActivity,InsertActivity::class.java)
            intent.putExtra("Code",rscode)
            startActivity(intent)
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
