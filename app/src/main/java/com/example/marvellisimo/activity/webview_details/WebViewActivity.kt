package com.example.marvellisimo.activity.webview_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProviders
import com.example.marvellisimo.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    lateinit var viewModel: WebViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        viewModel = ViewModelProviders.of(this).get(WebViewModel::class.java)

        viewModel.urlDetails = intent.getStringExtra("url") ?: "https://www.google.com/"
        viewModel.name = intent.getStringExtra("name") ?: "Marvellisimo"
        supportActionBar!!.title = viewModel.name

        webView_details.settings.javaScriptEnabled = true
        webView_details.settings.domStorageEnabled = true
        webView_details.webViewClient = WebViewClient()

        webView_details.loadUrl(viewModel.urlDetails)
    }

    override fun onBackPressed() {
        if(webView_details.canGoBack()){
            webView_details.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
