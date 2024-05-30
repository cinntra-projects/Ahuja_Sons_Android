package com.ahuja.sons.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.github.loadingview.LoadingView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.ActivityReportBinding
import com.ahuja.sons.globals.Global

class ReportActivity : AppCompatActivity() {
    lateinit var binding: ActivityReportBinding
    var id = ""
    var TypeFlag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id = intent.getStringExtra("id").toString()
        TypeFlag = intent.getStringExtra("Type").toString()

        binding.toolbarReport.toolbarAnnouncement.title = resources.getString(R.string.report)

        binding.toolbarReport.toolbarAnnouncement.setOnClickListener {
            finish()
        }


////        val webView = findViewById<WebView>(R.id.webView)


        binding.webView.webViewClient = object:WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

            binding.loader.start()
               //binding.loader.
              //  progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.loader.stop()
              //  progressBar.visibility = View.GONE
            }
        } // Ensure links open within the WebView

 //       Enable JavaScript (optional)
        binding.webView.settings.javaScriptEnabled = true
//
//        // Load the desired URL
        if (TypeFlag == "Installation"){
            val url = "${Global.REPORT_PDF_URL}$id"
            binding.webView.loadUrl(url)
        }
        else if (TypeFlag == "Preventive Maintenance" || TypeFlag == "Servicing"){
            val url = "${Global.SERVICING_PDF_URL}$id"
            binding.webView.loadUrl(url)
        }
        else if (TypeFlag == "Site Survey"){
            val url = "${Global.SITE_SERVEY_PDF_URL}$id"
            binding.webView.loadUrl(url)
        }


        //  setUpWebViewDialog(binding.webView,url,true,binding.loader)


    }


    private fun setUpWebViewDialog(
        webView: WebView,
        url: String,
        isZoomAvailable: Boolean,
        dialog: LoadingView
    ) {
        webView.settings.defaultZoom = WebSettings.ZoomDensity.MEDIUM
        webView.settings.builtInZoomControls = isZoomAvailable
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        // webView.getSettings().setAppCacheEnabled(false);
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        // Setting we View Client
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, btm: Bitmap) {
                super.onPageStarted(view, url, null)
                dialog.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                // initializing the printWeb Object
                dialog.visibility = View.GONE
                //dialogWeb = webView
            }
        }
        webView.loadUrl(url)
    }


}