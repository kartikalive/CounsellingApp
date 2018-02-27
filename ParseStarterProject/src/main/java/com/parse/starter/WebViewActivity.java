package com.parse.starter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private Activity activity;
    private ProgressDialog progDailog;
    private FloatingActionMenu menuYellow;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private Handler mUiHandler = new Handler();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        activity = this;

        String url = "https://ruralhack.github.io";

        progDailog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
        progDailog.setCancelable(false);


        webView = findViewById(R.id.web);
        menuYellow = (FloatingActionMenu) findViewById(R.id.menu_yellow);

        fab1 = (FloatingActionButton) findViewById(R.id.fab12);
        fab2 = (FloatingActionButton) findViewById(R.id.fab22);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });

        webView.loadUrl(url);

        menuYellow.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                String text;
                if (opened) {
                    text = "Menu opened";
                } else {
                    text = "Menu closed";
                }
            }
        });

        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);



    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab12:
                    Intent intent = new Intent(WebViewActivity.this, ResumeAndSkillsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.fab22:
                    Intent intent2 = new Intent(WebViewActivity.this, CounsellorListActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
