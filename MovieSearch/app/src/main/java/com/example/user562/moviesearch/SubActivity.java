package com.example.user562.moviesearch;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.example.user562.moviesearch.databinding.ActivitySubBinding;

public class SubActivity extends AppCompatActivity {

    ActivitySubBinding subLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subLayout = DataBindingUtil.setContentView(this, R.layout.activity_sub);

        Intent intent = getIntent();
        String url = intent.getStringExtra("LINK");

        subLayout.webView.setWebViewClient(new WebViewClient());
        WebSettings settings = subLayout.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        subLayout.webView.loadUrl(url);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
