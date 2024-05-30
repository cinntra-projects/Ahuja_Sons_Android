package com.ahuja.sons.activity


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import com.ahuja.sons.databinding.TestPdfBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity

class OpenPdfView:MainBaseActivity() {
    var id = ""
    var TypeFlag = ""
    var url = ""

    lateinit var testPdfBinding: TestPdfBinding
     @SuppressLint("SetJavaScriptEnabled")
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         testPdfBinding = TestPdfBinding.inflate(layoutInflater)
        setContentView(testPdfBinding.root)
         setSupportActionBar(testPdfBinding.toolbarview.toolbar)
        testPdfBinding.toolbarview.backPress.setOnClickListener {
            onBackPressed()
        }
         testPdfBinding.toolbarview.heading.text="Attachment"

         //todo hit and try--

         id = intent.getStringExtra("id").toString()
         TypeFlag = intent.getStringExtra("Type").toString()


        val PDfFrom: String = intent.getStringExtra("PDFLink").toString()

  /*       if (TypeFlag == "Installation"){
             url = "${Global.REPORT_PDF_URL}$id"
         }
         else if (TypeFlag == "Preventive Maintenance" || TypeFlag == "Servicing"){
             url = "${Global.SERVICING_PDF_URL}$id"
         }
         else if (TypeFlag == "Site Survey"){
             url = "${Global.SITE_SERVEY_PDF_URL}$id"
         }else{

             url = Global.Image_URL + PDfFrom
         }
*/

         url = Global.Image_URL + PDfFrom
         Log.e("url",url)

         testPdfBinding.webViewMain.webViewClient = WebViewClient()

         // this will load the url of the website
         testPdfBinding.webViewMain.loadUrl(url)


         testPdfBinding.loading.visibility = View.VISIBLE

         val webSettings: WebSettings = testPdfBinding.webViewMain.settings

         testPdfBinding.webViewMain.settings.loadsImagesAutomatically = true
         testPdfBinding.webViewMain.settings.domStorageEnabled = true
         testPdfBinding.webViewMain.settings.javaScriptEnabled = true
         webSettings.javaScriptEnabled = true
        // testPdfBinding.webViewMain.settings.setAppCacheEnabled(false)
        testPdfBinding.webViewMain.loadUrl(url)
         testPdfBinding.webViewMain.webViewClient = MyBrowser()
//        savePdfBtn.visibility = View.GONE
    }

    private inner class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

            testPdfBinding.loading.visibility = View.GONE
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            testPdfBinding.loading.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
        }
    }


}