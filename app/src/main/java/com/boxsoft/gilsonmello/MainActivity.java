package com.boxsoft.gilsonmello;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WebView wb;

    ProgressBar progress;

    SwipeRefreshLayout swipeRefresh;

    private void refreshContent() {
        wb.reload();
    }

    /**
     * Função que verifica se há conexão com a internet
     * @return boolean
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public String getCookieLaravelSession(String siteName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);

        if(cookies != null ) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains("remember_web")) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }

    public String getCookie(String siteName, String CookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if(cookies != null ) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {
                    if(wb.canGoBack()) {
                        wb.goBack();
                    } else {
                        finish();
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    boolean okPrompt = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        progress = findViewById(R.id.progress);

        wb = (WebView) findViewById(R.id.webView);
        wb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Deseja continuar?")
                    .setMessage(message)
                    .setNegativeButton("Cancelar",
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do your stuff here
                                result.cancel();
                                okPrompt = false;
                            }
                        })
                    .setPositiveButton("Ok",
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do your stuff here
                                result.confirm();
                                okPrompt = true;
                            }
                        })
                    .setCancelable(true)
                    .create()
                    .show();
                return okPrompt;
            }
        });
        WebSettings ws = wb.getSettings();
        ws.setJavaScriptEnabled(true);
        wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile");
        wb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                swipeRefresh.setRefreshing(false);
                wb.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefresh.setRefreshing(false);
                progress.setVisibility(View.GONE);
                wb.setVisibility(View.VISIBLE);
                String session = getCookieLaravelSession(url);
                String user_name = getCookie(url, "user_name");
                if (session != null || user_name != null) {
                    finish();
                    startActivity(new Intent(MainActivity.this, ContentActivity.class));
                }
            }
        });

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setRefreshing(false);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        if(!isOnline()) {
            Toast.makeText(this, "Nenhuma conexão encontrada", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
