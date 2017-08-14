package com.antoineraulin.devbrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity {
    private Context c;
    String currentUrl;
    ArrayList<String> console = new ArrayList<String>();
    Boolean sourceState = false;
    Boolean consoleState = false;
    Boolean networkState = false;
    private boolean isConnected = true;
    private RecyclerView recyclerView;
    TextView toolBarText;
    private List<MyObject> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolBarText = (TextView) findViewById(R.id.textViewToolBar);
        c = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText hotbarUrl = (EditText) findViewById(R.id.editText2);


        hotbarUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(!isFocused)
                {
                    Button consoleButton = (Button) findViewById(R.id.button3);
                    Button sourceButton = (Button) findViewById(R.id.button2);
                    Button networkButton = (Button) findViewById(R.id.button4);
                    sourceButton.setVisibility(VISIBLE);
                    consoleButton.setVisibility(VISIBLE);
                    networkButton.setVisibility(VISIBLE);
                }else{
                    Button consoleButton = (Button) findViewById(R.id.button3);
                    Button sourceButton = (Button) findViewById(R.id.button2);
                    Button networkButton = (Button) findViewById(R.id.button4);
                    sourceButton.setVisibility(GONE);
                    consoleButton.setVisibility(GONE);
                    networkButton.setVisibility(GONE);
                }
            }
        });




        hotbarUrl.setText("https://www.google.com", TextView.BufferType.EDITABLE);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            if (ni.getState() != NetworkInfo.State.CONNECTED) {
                isConnected = false;
            }
        }
        final WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setNetworkAvailable(isConnected);
        myWebView.setWebViewClient(new MyWebViewClient());
        final WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://google.com");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(GONE);

                } else {
                    progressBar.setVisibility(VISIBLE);

                }
            }
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                console.add(consoleMessage.message());
                TextView consoleText = (TextView) findViewById(R.id.textViewConsole);
                consoleText.setText("");
                String[] simpleArray = new String[ console.size() ];
                console.toArray( simpleArray );
                int arraySize = simpleArray.length;
                for(int i = 0; i < arraySize; i++) {
                    consoleText.append(simpleArray[i]);
                    consoleText.append("\n");
                }

                return true;
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void shouldInterceptRequest (WebView view,
                                                WebResourceRequest request){
            Log.i("webview request", String.valueOf(request));
            }
        });

        hotbarUrl.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    myWebView.loadUrl(hotbarUrl.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        final EditText consoleInput = (EditText) findViewById(R.id.editTextConsole);
        consoleInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    myWebView.evaluateJavascript("(function(){"+consoleInput.getText().toString()+"})();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                }
                            });

                    return true;
                }
                return false;
            }
        });

        final Button sourceButton = (Button) findViewById(R.id.button2);
        sourceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(sourceState == false){
                    myWebView.loadUrl(
                            "javascript:this.document.location.href = 'source://' + encodeURI(document.documentElement.outerHTML);");
                }else{
                    closeSource();
                }

            }
        });
        final Button consoleButton = (Button) findViewById(R.id.button3);
        consoleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(consoleState == false){
                    consoleOpen();
                }else{
                    consoleClose();
                }

            }
        });
        final Button networkButton = (Button) findViewById(R.id.button4);
        networkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(networkState == false){
                    networkOpen();
                }else{
                    networkClose();
                }

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);

    }

    private void recycle(){

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new MyAdapter(cities));
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (url.startsWith("source://")) {
                try {
                    String html = URLDecoder.decode(url, "UTF-8").substring(9);
                    sourceReceived(html);
                } catch (UnsupportedEncodingException e) {
                    Log.e("example", "failed to decode source", e);
                }
                return true;
            }
            currentUrl=url;
            EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
            hotbarUrl.setText(currentUrl, TextView.BufferType.EDITABLE);
            URL aURL = null;
            try {
                aURL = new URL(currentUrl);
                String domain = aURL.getHost();
                String str = currentUrl;
                String substr = domain;
                String before = str.substring(0, str.indexOf(substr));
                before = "<font color='#919191'>"+before+"</font>";
                String after = str.substring(str.indexOf(substr) + substr.length());
                after = "<font color='#919191'>"+after+"</font>";
                //String next = "<font color='#EE0000'>"+domain+"</font>";
                hotbarUrl.setText(Html.fromHtml(before + domain + after), TextView.BufferType.EDITABLE);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (isConnected) {
                return false;
            } else {
                view.loadData(offlineMessageHtml, "text/html", "utf-8");
                return true;
            }
        }

        public HashMap<String, Long> resources = new HashMap<String, Long>();
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request){
            resources.put(request.getUrl().toString(), System.currentTimeMillis());
            WebResourceResponse response =  super.shouldInterceptRequest(view, request);
            return response;
        }
        public void onLoadResource(WebView view, String url) {
            if(resources.containsKey(url)){
                Long timeStartResource = resources.get(url);
                Long timeElapseResource = System.currentTimeMillis() - timeStartResource;
                Log.i("webview info","url: "+url+" | time elapsed : "+timeElapseResource);
                cities.add(new MyObject(url, timeElapseResource));
            }
            super.onLoadResource(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            cities.clear();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            console.clear();
            recycle();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            currentUrl=url;
            EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
            if(currentUrl.startsWith("data:text/html,<style")){
                String before = "<font color='#FF2D2D'>code source</font>";
                hotbarUrl.setText(Html.fromHtml(before), TextView.BufferType.EDITABLE);
            }else{

            hotbarUrl.setText(currentUrl, TextView.BufferType.EDITABLE);
            URL aURL = null;
            try {
                aURL = new URL(currentUrl);
                String domain = aURL.getHost();
                String str = currentUrl;
                String substr = domain;
                String before = str.substring(0, str.indexOf(substr));
                before = "<font color='#919191'>"+before+"</font>";
                String after = str.substring(str.indexOf(substr) + substr.length());
                after = "<font color='#919191'>"+after+"</font>";
                //String next = "<font color='#EE0000'>"+domain+"</font>";
                hotbarUrl.setText(Html.fromHtml(before + domain + after), TextView.BufferType.EDITABLE);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }}
        }

        @Override
        public void onReceivedError (WebView view, int errorCode,
                                     String description, String failingUrl) {
            Log.i("errorCode", description);

            String contentHtml = description;
            String errorCodeHtml = "ERROR";
            String html = "\n" +
                    "<!doctype html>\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no\">\n"+
                    "<style>\n" +
                    "  body { text-align: center; padding: 150px; }\n" +
                    "  h1 { font-size: 50px; }\n" +
                    "  body { font: 20px Helvetica, sans-serif; color: #333; }\n" +
                    "  article { display: block; text-align: left; width: 650px; margin: 0 auto; }\n" +
                    "  a { color: #dc8100; text-decoration: none; }\n" +
                    "  a:hover { color: #333; text-decoration: none; }\n" +
                    "</style>\n" +
                    "\n" +
                    "<article>\n" +
                    "    <h1>Ooops</h1>\n" +
                    "    <div>\n<p>\n"+
                    contentHtml+
                    "</p>\n"+
                    "</div>\n"+
                    "<h5>\n"+
                    errorCodeHtml+
                    "</h5>\n"+
                    "</article>";
            view.stopLoading();
            view.loadData(html, "text/html", "utf-8");
        }
    }

    private void sourceReceived(String html) {
        sourceState = true;
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebView myWebViewSource = (WebView) findViewById(R.id.webviewsource);
        myWebView.setVisibility(GONE);
        final WebSettings webSettings = myWebViewSource.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Log.i("information", myWebView.getUrl());
        myWebViewSource.setVisibility(VISIBLE);
        Button sourceButton = (Button) findViewById(R.id.button2);
        Button consoleButton = (Button) findViewById(R.id.button3);
        consoleButton.setVisibility(GONE);
        sourceButton.setBackgroundResource(R.drawable.ic_action_close);

        EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
        Button networkButton = (Button) findViewById(R.id.button4);
        hotbarUrl.setVisibility(GONE);
        toolBarText.setVisibility(VISIBLE);
        networkButton.setVisibility(GONE);
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll("  ", "\n");
        Log.i("html", html);
        myWebViewSource.getSettings().setJavaScriptEnabled(true);
        String finalHTML = "<!DOCTYPE html>\n" +
                "<html lang=\"fr\">\n" +
                "<head>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/EnlighterJS.min.css\" />\n" +
                "    <script type=\"text/javascript\" src=\"file:///android_asset/MooTools.min.js\"></script>\n" +
                "    <script type=\"text/javascript\" src=\"file:///android_asset/EnlighterJS.min.js\"></script>\n" +
                "    \n" +
                "    \n" +
                "\n" +
                "\n" +
                "    <meta name=\"EnlighterJS\" content=\"Advanced javascript based syntax highlighting\" data-language=\"javascript\" data-indent=\"2\" data-selector-block=\"pre\" data-selector-inline=\"code\" /></head>\n" +
                "<body>\n" +
                "\n" +
                "<pre data-enlighter-language=\"html\">\n" +
                html +
                "</pre>\n" +
                "</body>\n" +
                "</html>\n";
        myWebViewSource.loadDataWithBaseURL( "file:///android_asset/", finalHTML, "text/html",
                "utf-8", null );
        URL aURL = null;
        String before = "<font color='#FFFFFF'>source</font>";
        try {
            aURL = new URL(myWebView.getUrl());
            String domain = aURL.getHost();
            before = "<font color='#919191'>source : </font><font color='#FFFFFF'>"+domain+"</font>";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        toolBarText.setText(Html.fromHtml(before));

    }

    private void closeSource(){
        sourceState = false;
        EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebView myWebViewSource = (WebView) findViewById(R.id.webviewsource);
        Button sourceButton = (Button) findViewById(R.id.button2);
        Button consoleButton = (Button) findViewById(R.id.button3);
        sourceButton.setBackgroundResource(R.drawable.ic_action_source);
        myWebViewSource.setVisibility(GONE);
        consoleButton.setVisibility(VISIBLE);
        myWebView.setVisibility(VISIBLE);
        Button networkButton = (Button) findViewById(R.id.button4);
        hotbarUrl.setVisibility(VISIBLE);
        toolBarText.setVisibility(GONE);
        networkButton.setVisibility(VISIBLE);
        myWebViewSource.loadUrl("about:blank");
        URL aURL = null;
        try {
            aURL = new URL(myWebView.getUrl());
            String domain = aURL.getHost();
            String str = currentUrl;
            String substr = domain;
            String before = str.substring(0, str.indexOf(substr));
            before = "<font color='#919191'>"+before+"</font>";
            String after = str.substring(str.indexOf(substr) + substr.length());
            after = "<font color='#919191'>"+after+"</font>";
            //String next = "<font color='#EE0000'>"+domain+"</font>";
            hotbarUrl.setText(Html.fromHtml(before + domain + after), TextView.BufferType.EDITABLE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        WebView myWebView = (WebView) findViewById(R.id.webview);
        if(sourceState == false){
            if(myWebView.canGoBack())
            {
                myWebView.goBack();
            }
        }else{
            closeSource();
        }
    }

    private void consoleOpen(){
        consoleState = true;
        EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
        hotbarUrl.setVisibility(GONE);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        RelativeLayout consoleLayout = (RelativeLayout) findViewById(R.id.consoleContent);
        myWebView.setVisibility(GONE);
        toolBarText.setVisibility(VISIBLE);
        consoleLayout.setVisibility(VISIBLE);
        Button consoleButton = (Button) findViewById(R.id.button3);
        Button sourceButton = (Button) findViewById(R.id.button2);
        Button networkButton = (Button) findViewById(R.id.button4);
        sourceButton.setVisibility(GONE);
        networkButton.setVisibility(GONE);
        consoleButton.setBackgroundResource(R.drawable.ic_action_close);
        URL aURL = null;
        String before = "<font color='#FFFFFF'>console</font>";
        try {
            aURL = new URL(myWebView.getUrl());
            String domain = aURL.getHost();
            before = "<font color='#919191'>console : </font><font color='#FFFFFF'>"+domain+"</font>";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        toolBarText.setText(Html.fromHtml(before));
    }

    private void consoleClose(){
        consoleState = false;
        EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
        hotbarUrl.setVisibility(VISIBLE);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        RelativeLayout consoleLayout = (RelativeLayout) findViewById(R.id.consoleContent);
        myWebView.setVisibility(VISIBLE);
        consoleLayout.setVisibility(GONE);
        Button networkButton = (Button) findViewById(R.id.button4);
        Button consoleButton = (Button) findViewById(R.id.button3);
        Button sourceButton = (Button) findViewById(R.id.button2);
        sourceButton.setVisibility(VISIBLE);
        toolBarText.setVisibility(GONE);
        networkButton.setVisibility(VISIBLE);
        consoleButton.setBackgroundResource(R.drawable.ic_action_console);
        URL aURL = null;
        try {
            aURL = new URL(myWebView.getUrl());
            String domain = aURL.getHost();
            String str = currentUrl;
            String substr = domain;
            String before = str.substring(0, str.indexOf(substr));
            before = "<font color='#919191'>"+before+"</font>";
            String after = str.substring(str.indexOf(substr) + substr.length());
            after = "<font color='#919191'>"+after+"</font>";
            //String next = "<font color='#EE0000'>"+domain+"</font>";
            hotbarUrl.setText(Html.fromHtml(before + domain + after), TextView.BufferType.EDITABLE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void networkOpen(){
        networkState = true;
        EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setVisibility(GONE);
        recyclerView.setVisibility(VISIBLE);
        toolBarText.setVisibility(VISIBLE);
        Button consoleButton = (Button) findViewById(R.id.button3);
        Button sourceButton = (Button) findViewById(R.id.button2);
        Button networkButton = (Button) findViewById(R.id.button4);
        hotbarUrl.setVisibility(GONE);
        sourceButton.setVisibility(GONE);
        consoleButton.setVisibility(GONE);
        networkButton.setBackgroundResource(R.drawable.ic_action_close);
        URL aURL = null;
        String before = "<font color='#FFFFFF'>"+getString(R.string.network)+"</font>";
        try {
            aURL = new URL(myWebView.getUrl());
            String domain = aURL.getHost();
            before = "<font color='#919191'>"+getString(R.string.network)+" : </font><font color='#FFFFFF'>"+domain+"</font>";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        toolBarText.setText(Html.fromHtml(before));


    }
    private void networkClose(){
        networkState = false;
        EditText hotbarUrl = (EditText) findViewById(R.id.editText2);
        hotbarUrl.setVisibility(VISIBLE);
        toolBarText.setVisibility(GONE);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setVisibility(VISIBLE);
        recyclerView.setVisibility(GONE);
        Button consoleButton = (Button) findViewById(R.id.button3);
        Button sourceButton = (Button) findViewById(R.id.button2);
        Button networkButton = (Button) findViewById(R.id.button4);
        sourceButton.setVisibility(VISIBLE);
        consoleButton.setVisibility(VISIBLE);
        networkButton.setBackgroundResource(R.drawable.ic_action_name);
        URL aURL = null;
        try {
            aURL = new URL(myWebView.getUrl());
            String domain = aURL.getHost();
            String str = currentUrl;
            String substr = domain;
            String before = str.substring(0, str.indexOf(substr));
            before = "<font color='#919191'>"+before+"</font>";
            String after = str.substring(str.indexOf(substr) + substr.length());
            after = "<font color='#919191'>"+after+"</font>";
            //String next = "<font color='#EE0000'>"+domain+"</font>";
            hotbarUrl.setText(Html.fromHtml(before + domain + after), TextView.BufferType.EDITABLE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }



}
