package com.boxsoft.gilsonmello;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ContentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    WebView wb;

    ProgressBar progress;

    SwipeRefreshLayout swipeRefresh;

    LinearLayout header;

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
        return CookieValue.replace("%20", " ")
                .replace("%40", "@");
    }

    public void clearCookies(){
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
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

        assert CookieValue != null;
        return CookieValue.replace("%20", " ")
                .replace("%40", "@");
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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Box Soft");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        progress = findViewById(R.id.progress);

        wb = (WebView) findViewById(R.id.webView);
        WebSettings ws = wb.getSettings();
        ws.setJavaScriptEnabled(true);
        wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile");
        wb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                new AlertDialog.Builder(ContentActivity.this)
                        .setTitle("Deseja continuar?")
                        .setMessage(message)
                        .setNegativeButton("Cancelar",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.cancel();
                                }
                            })
                        .setPositiveButton("Ok",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.confirm();
                                }
                            })
                        .setCancelable(true).create().show();
                return true;
            }
        });

        wb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);
                wb.setVisibility(View.GONE);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                swipeRefresh.setRefreshing(false);
                progress.setVisibility(View.GONE);
                wb.setVisibility(View.VISIBLE);
            }

            @TargetApi(Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String name = getCookie(url, "user_name");
                TextView txtName = (TextView) navigationView
                        .getHeaderView(0)
                        .findViewById(R.id.txtName);
                txtName.setText(name);
                TextView txtEmail = (TextView) navigationView
                        .getHeaderView(0)
                        .findViewById(R.id.txtEmail);
                String email = getCookie(url, "user_email");
                txtEmail.setText(email);

                toolbar.getMenu().getItem(2).setVisible(true);

                if(wb.getUrl().contains("participants/instalments")) {
                    toolbar.setTitle("Mensalidades");
                    toolbar.getMenu().getItem(2).setVisible(false);
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else if(wb.getUrl().equals("http://boxsoft.mirandafitness.com.br/mobile/participants")) {
                    toolbar.setTitle("Participantes");
                    navigationView.getMenu().getItem(2).setChecked(true);
                } else if(wb.getUrl().equals("http://boxsoft.mirandafitness.com.br/mobile/participants/create")) {
                    toolbar.setTitle("Cadastrar Participante");
                    navigationView.getMenu().getItem(2).setChecked(true);
                } else if(wb.getUrl().equals("http://boxsoft.mirandafitness.com.br/mobile/users/change")) {
                    toolbar.setTitle("Perfil");
                    toolbar.getMenu().getItem(2).setVisible(false);
                    navigationView.getMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(2).setChecked(false);
                } else if(wb.getUrl().equals("http://boxsoft.mirandafitness.com.br/mobile/boxes")) {
                    toolbar.setTitle("Caixas");
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else if(wb.getUrl().equals("http://boxsoft.mirandafitness.com.br/mobile/boxes/create")) {
                    toolbar.setTitle("Cadastrar Caixa");
                    toolbar.getMenu().getItem(2).setVisible(false);
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else if(wb.getUrl().contains("mobile/boxes") && wb.getUrl().contains("edit")) {
                    toolbar.setTitle("Editar Caixa");
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else if(wb.getUrl().contains("mobile/participants") && wb.getUrl().contains("edit")) {
                    toolbar.setTitle("Editar Participante");
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else if(wb.getUrl().contains("boxes/participants")) {
                    toolbar.setTitle("Dados do Caixa");
                    toolbar.getMenu().getItem(1).setVisible(true);
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else if(wb.getUrl().contains("mobile/participants") && wb.getUrl().contains("edit") ) {
                    toolbar.setTitle("Editar Participante");
                    navigationView.getMenu().getItem(1).setChecked(true);
                }  else if(wb.getUrl().contains("mobile/boxes/winner_of_month")) {
                    toolbar.setTitle("Vencedor do mês");
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else {
                    toolbar.setTitle("Home");
                    navigationView.getMenu().getItem(0).setChecked(true);
                    toolbar.getMenu().getItem(2).setVisible(false);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        item.setVisible(true);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            clearCookies();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.action_profile) {
            wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile/users/change");
        } else if(id == R.id.action_create) {
            if(wb.getUrl().contains("participants")) {
                wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile/participants/create");
            } else if(wb.getUrl().contains("boxes")) {
                wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile/boxes/create");
            }
            item.setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile");
        } else if (id == R.id.nav_box) {
            wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile/boxes");
        } else if (id == R.id.nav_participant) {
            wb.loadUrl("http://boxsoft.mirandafitness.com.br/mobile/participants");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
