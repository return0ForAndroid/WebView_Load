package com.wuxl.web;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Created by WUXL on 2016/6/23.
 */
public class WebviewFragment extends Fragment {

    private static String TAG = WebviewFragment.class.getName();

    public static int WEB_BACK = 0x000001;

    private WebView mWebView;

    private View mLoadView;
    private ProgressBar mLoadingPgb;
    private View mLoadfailedView;
    private Button mReloadBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        initView(rootView);
        initWebView();
        return rootView;
    }

    private void initView(View rootView) {
        Log.i(TAG, "initView");
        this.mWebView = (WebView) rootView.findViewById(R.id.web);
        this.mLoadView = rootView.findViewById(R.id.view_load);
        this.mLoadingPgb = (ProgressBar) this.mLoadView.findViewById(R.id.pgb_load_loading);
        this.mLoadfailedView = this.mLoadView.findViewById(R.id.rel_load_failed);
        this.mReloadBtn = (Button) this.mLoadfailedView.findViewById(R.id.btn_load_reload);

        this.mReloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
            }
        });
        Log.i(TAG, "initView end");
    }

    private void initWebView() {
        Log.i(TAG, "initWebView");
        // 设置WebView对JavaScript的支持
        mWebView.getSettings().setJavaScriptEnabled(true);
        //支持javascript打开窗体页面
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.getSettings().setAllowContentAccess(true);

        mWebView.getSettings().setSupportZoom(false);

        mWebView.getSettings().setAllowContentAccess(true);

        //需要加上这个对标签的支持
        mWebView.getSettings().setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWebView.getSettings().supportMultipleWindows();

        mWebView.getSettings().setNeedInitialFocus(true);

        mWebView.getSettings().setLoadsImagesAutomatically(true);

        mWebView.getSettings().setUseWideViewPort(true);

        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.getSettings().setDisplayZoomControls(false);

        //设置缓存
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(TAG, "onPageStarted");
                super.onPageStarted(view, url, favicon);
                view.setTag("start");
                loading();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "onPageFinished");
                super.onPageFinished(view, url);
                if (view.getTag() != null && view.getTag().equals("error")) {
                    loadFailed();
                } else {
                    loadSuccess();
                    view.setVisibility(View.VISIBLE);
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.i(TAG, "onReceivedError");
                super.onReceivedError(view, request, error);
                onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.i(TAG, "onReceivedSslError");
                super.onReceivedSslError(view, handler, error);
                onReceivedError(view, error.getPrimaryError(), error.getCertificate().toString(), error.getUrl().toString());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i(TAG, "onReceivedError");
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.setTag("error");
                view.setVisibility(View.GONE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mLoadingPgb.setProgress(newProgress);
            }
        });

        mWebView.loadUrl("https://www.baidu.com/");
        Log.i(TAG, "initWebView end");
    }

    private void loading() {
        mLoadView.setVisibility(View.VISIBLE);
        mLoadingPgb.setVisibility(View.VISIBLE);
        mLoadingPgb.setProgress(0);
        mLoadfailedView.setVisibility(View.GONE);
    }

    private void loadSuccess() {
        mLoadView.setVisibility(View.GONE);
    }

    private void loadFailed() {
        mLoadView.setVisibility(View.VISIBLE);
        mLoadingPgb.setVisibility(View.GONE);
        mLoadfailedView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WEB_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                getActivity().finish();
            }
        }
    }
}
