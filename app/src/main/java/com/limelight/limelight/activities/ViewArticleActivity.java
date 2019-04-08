package com.limelight.limelight.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.limelight.limelight.R;
import com.limelight.limelight.dialogs.ClassificationDialog;

import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ViewArticleActivity extends AppCompatActivity {
    private String url;
    private WebView webview;
    private Toolbar toolbar;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        url = intent.getExtras().getString("url", "");
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        webview = findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (mySwipeRefreshLayout.isRefreshing()) {
                    mySwipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        webview.getSettings().setJavaScriptEnabled(false);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);


        if (!url.equals("")) {
            //open the article in webview
            progressDialog = new ProgressDialog(ViewArticleActivity.this, R.style.AlertDialogStyle);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            webview.loadUrl("https://mercury.postlight.com/amp?url=" + url);
        } else {
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
        }

        mySwipeRefreshLayout.setOnRefreshListener(() -> webview.reload());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_analyze:

                if (isValid(url)) {
                    ClassificationDialog dialog = new ClassificationDialog(ViewArticleActivity.this, url);
                    dialog.show();
                } else {
                    Toast.makeText(ViewArticleActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
                //put code to open dialog bo;x to display analysis
                return true;
            case R.id.menu_share:
                //Toast.makeText(this, "menu_share", Toast.LENGTH_SHORT).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                try {
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ViewArticleActivity.this, "There are no browsers installed.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "There are no browsers installed.", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.menu_open_browser:
                //Toast.makeText(this, "menu_open_browser", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "There are no browsers installed.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "There are no browsers installed.", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isValid(String url) {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }


}
