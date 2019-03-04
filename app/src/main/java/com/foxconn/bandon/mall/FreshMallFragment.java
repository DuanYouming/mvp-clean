package com.foxconn.bandon.mall;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.EncodeUtils;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.utils.NetworkUtil;
import com.foxconn.bandon.zixing.activity.CaptureFragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;


public class FreshMallFragment extends BaseFragment {
    public static final String TAG = FreshMallFragment.class.getName();
    private static final String APP_CACHE_DIRNAME = "/webcache";
    private static String[] foods = new String[]{"萝卜", "苹果", "可乐", "猪蹄", "西瓜", "秋刀鱼", "番茄沙司", "牛肉丸", "酸奶"};
    private String mUrl;
    private WebView mWebView;

    public FreshMallFragment() {

    }


    public static FreshMallFragment newInstance() {
        return new FreshMallFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getBundle();
        if (null != bundle) {
            String key = bundle.getString(CaptureFragment.KEY_CAPTURE_RESULT);
            if (!TextUtils.isEmpty(key)) {
                mUrl = Constant.JD_HTML5_SHOPPING_URL_WITH_KEYWORD;
                int index = (int) (Math.random() * foods.length);
                mUrl = mUrl.replaceAll("SEARCH_KEYWORD", foods[index]);
                return;
            }
        }
        mUrl = Constant.JD_HTML5_SHOPPING_URL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            String mac = NetworkUtil.getMacAddress(getContext());
            mUrl = mUrl.replaceAll("HASH_MAC", EncodeUtils.sha256(mac));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        hookWebView();
        return inflater.inflate(R.layout.fragment_fresh_mall, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = view.findViewById(R.id.web_view);
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDatabaseEnabled(true);
        String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;

        webSettings.setDatabasePath(cacheDirPath);
        webSettings.setAppCachePath(cacheDirPath);
        webSettings.setAppCacheEnabled(true);


        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setTextZoom(120);
        mWebView.setWebViewClient(new MyWebViewClient());
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.stopLoading();
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.clearHistory();
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            LogUtils.d(TAG, "url:" + request.getUrl());
            return shouldInterceptRequest(view, request.getUrl().toString());
        }

    }

    public void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            @SuppressLint("PrivateApi")
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                LogUtils.d(TAG, "sProviderInstance isn't null");
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            @SuppressLint("PrivateApi")
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
                field.set("sProviderInstance", sProviderInstance);
            }
        } catch (Throwable e) {
            LogUtils.e(TAG, "hook error:" + e.toString());
        }
    }
}
