package deeplydiligent.vidnote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;


public class MainActivity extends AppCompatActivity {
    final Context context = this;
    private TextView mTextMessage;
    private String html = "<iframe width=\"450\" height=\"260\" style=\"border: 1px solid #cccccc;\" src=\"http://google.com/\" ></iframe>";
    private WebView mWebView;
    private boolean opened;

    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button savedFiles = (Button) findViewById(R.id.action_savedFiles);

        opened = false;

        class MyJavaScriptInterface {
            @JavascriptInterface
            public void onUrlChange(String url) {
                Log.d("hydrated", "onUrlChange" + url);
                if (url.contains("watch") && !opened){

                    Intent intent = new Intent(context, video.class);
                    intent.putExtra("com.example.vidnote.MESSAGE", url);
                    Log.d("lmao",url);
                    startActivity(intent);
                    opened = true;
                }
            }
        }

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
                    }
                });
            }
        });
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(),"android");

        mWebView.loadUrl("https://www.youtube.com/music");

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_savedFiles) {
            Intent intent = new Intent(this, ActivitySavedVideos.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.screens, menu);


        return true;
    }

    @Override
    protected void onResume() {
        opened = false;
        super.onResume();
    }
}
