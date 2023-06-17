package com.example.youtube_ad_free_demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private WebView web;

     String jsString = "(function() {\n" +
             "    var videoItems = document.querySelectorAll('ytm-rich-item-renderer[data-video-id]');\n" +
             "console.log('aasasasas')"+
             "    for (var i = 0; i < videoItems.length; i++) {\n" +
             "        videoItems[i].addEventListener('click', function() {\n" +
             "            var videoId = this.getAttribute('data-video-id');\n" +
             "            window.AndroidInterface.onVideo(videoId);\n" +
             "        });\n" +
             "    }\n" +
             "})();";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        web = findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);


        web.addJavascriptInterface(new YouTubeWebInterface(), "YouTubeInterface");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl());
                return false;
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();


                // Check if the URL represents a video item click
                if (url.contains("https://m.youtube.com/youtubei/v1/browse?key")) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            addListener(view);
                        }
                    });

                }

                if (isAdUrl(url)) {
                    // Return an empty WebResourceResponse to block the ad request
                    return new WebResourceResponse(null, null, null);
                }
                // Allow other requests to proceed normally
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //web.loadUrl("javascript:" + getJavascriptCode());
                addListener(view);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                Log.d(TAG, "onPageCommitVisible: ");
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted: ");
                super.onPageStarted(view, url, favicon);
            }
        });

        web.loadUrl("http://m.youtube.com");
    }

    private void addListener(WebView view) {
        String script = "javascript:"+getJavascriptCode();
        view.evaluateJavascript(script, null);
    }

    private class YouTubeWebInterface {
        @JavascriptInterface
        public void openVideo(String videoId) {
            // Handle the YouTube video click

            Log.d(TAG, "openVideo: "+videoId);
            Toast.makeText(MainActivity.this, "Clicked video: " + videoId, Toast.LENGTH_SHORT).show();

        }
    }

    private String getJavascriptCode() {
        try {
            InputStream inputStream = getAssets().open("youtube-interceptor.js");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }


    private boolean isAdUrl(String url) {
        // Check if the URL matches common ad-related patterns
        String regexPattern = "^https?://[^/]+\\.googlevideo\\.com/[^?]+";
        String queryString = url.substring(url.lastIndexOf('?') + 1);
        String[] queryParams = queryString.split("&");


        // Check if the URL matches specific ad-related patterns using regex
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return true;
        }

        // Check if the URL contains specific keywords indicating ads
        String[] adKeywords = {"doubleclick", "googleads", "ads", "adserve", "ad_type", "ad_url", "adserver", "advertis", "adredir"};
        for (String keyword : adKeywords) {
            if (url.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

}