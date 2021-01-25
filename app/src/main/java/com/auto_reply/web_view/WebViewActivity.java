package com.auto_reply.web_view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.annotation.TargetApi;

import androidx.databinding.DataBindingUtil;

import com.auto_reply.R;
import com.auto_reply.base.BaseActivity;
import com.auto_reply.databinding.WebViewActivityBinding;

public class WebViewActivity extends BaseActivity {

    String webUrl;

    WebViewActivityBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.web_view_activity);
        webUrl = getIntent().getStringExtra("url");
        mBinding.webView.getSettings().setJavaScriptEnabled(true); // enable javascript

        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);

        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Activity activity = this;
        setTitle(getString(R.string.app_name));
        mBinding.webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        mBinding.webView.loadUrl(webUrl);

    }

}