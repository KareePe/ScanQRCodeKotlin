package com.example.stockcar

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler

class MainActivity : AppCompatActivity() , ResultHandler{

    private val REQUES_CAMERA = 1
    private var scannerView : ZXingScannerView ?= null
    private var txtResult : TextView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resultCode = findViewById<EditText>(R.id.txtResult)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        scannerView = findViewById(R.id.scanner)
        txtResult = findViewById(R.id.txtResult)

        if(!checkPermission())
            requestPermission()

        submitBtn.setOnClickListener {
            val rscode = resultCode.text.toString()

            if(rscode == ""){
                Toast.makeText(applicationContext,"ช่องนี้ไม่สามารถว่างได้",Toast.LENGTH_LONG).show()
            }else{
                val intent = Intent(this@MainActivity,informationActivity::class.java)
                intent.putExtra("Code",rscode)
                startActivity(intent)
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
        val vibrator:Vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
}
