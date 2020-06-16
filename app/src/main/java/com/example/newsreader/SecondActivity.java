package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SecondActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

         webView = findViewById(R.id.webView);
         webView.getSettings().setJavaScriptEnabled(true);
         webView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
         webView.loadUrl(MainActivity.urls.get(intent.getIntExtra("News clicked", 1)));
    }
}
