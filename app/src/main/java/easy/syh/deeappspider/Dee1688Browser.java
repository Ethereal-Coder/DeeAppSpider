package easy.syh.deeappspider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Dee1688Browser extends AppCompatActivity {

    @BindView(R.id.address_edit)
    EditText addressEdit;
    @BindView(R.id.address_go)
    TextView addressGo;
    @BindView(R.id.address_clear)
    TextView addressClear;
    @BindView(R.id.navigation_top)
    LinearLayout navigationTop;
    @BindView(R.id.browser_progress)
    ProgressBar browserProgress;
    @BindView(R.id.fl_dee_web)
    FrameLayout flDeeWeb;
    @BindView(R.id.browser_back)
    ImageButton browserBack;
    @BindView(R.id.browser_forward)
    ImageButton browserForward;
    @BindView(R.id.jump_home)
    ImageButton jumpHome;
    @BindView(R.id.spider_msg)
    TextView spiderMsg;
    @BindView(R.id.navigation_bottom)
    LinearLayout navigationBottom;
    private URL plateHomeUrl;
    private static final String tempHomeUrl = "https://m.1688.com";
    private DeeWebView deeWebView;
    private ArrayList<String> imgUrls = new ArrayList<>();
    private String detail_1688_result;
    private final int disable = 120;
    private final int enable = 255;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dee_browser);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (deeWebView != null && deeWebView.canGoBack()) {
                deeWebView.goBack();
                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16)
                    changGoForwardButton(deeWebView);
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || deeWebView == null || intent.getData() == null) return;
        deeWebView.loadUrl(intent.getData().toString());
    }

    @Override
    protected void onDestroy() {
        if (deeWebView != null) deeWebView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (deeWebView != null) deeWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (deeWebView != null) deeWebView.onResume();
        super.onResume();
    }

    private void init() {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16) {
            browserBack.setAlpha(disable);
            browserForward.setAlpha(disable);
        }

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Intent intent = getIntent();
        if (intent != null) {
            try {
                plateHomeUrl = new URL(intent.getStringExtra("plate_url"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            } catch (Exception e) {
            }
        }

        deeWebView = new DeeWebView(this);
        flDeeWeb.addView(deeWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        deeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return false;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (s.contains("m.1688.com/offer")) {
                    detail_1688_result = s;
                } else {
                    detail_1688_result = null;
                }
                imgUrls.clear();
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16)
                    changGoForwardButton(webView);
            }

            @Override
            public void onLoadResource(WebView webView, String s) {
                super.onLoadResource(webView, s);
                System.err.println(s);

            }
        });

        deeWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
            }
        });

        //
        WebSettings webSetting = deeWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        if (plateHomeUrl == null) {
            deeWebView.loadUrl(tempHomeUrl);
        } else {
            deeWebView.loadUrl(plateHomeUrl.toString());
        }

        addressEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (null == deeWebView.getUrl()) return;
                    addressEdit.setText(deeWebView.getUrl());
                    addressGo.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    String title = deeWebView.getTitle();
                    if (title != null && title.length() > 10) {
                        addressEdit.setText(title.subSequence(0, 10) + "...");
                    } else {
                        addressEdit.setText(title);
                    }
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        addressEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String url = null;
                if (addressEdit.getText() != null) {
                    url = addressEdit.getText().toString();
                }
                if (url == null || addressEdit.getText().toString().equalsIgnoreCase("")) {
                    addressGo.setTextColor(Color.parseColor("#999999"));
                } else {
                    addressGo.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });


    }

    private void changGoForwardButton(WebView view) {
        if (view.canGoBack()) {
            browserBack.setAlpha(enable);
        } else {
            browserBack.setAlpha(disable);
        }
        if (view.canGoForward()) {
            browserForward.setAlpha(enable);
        } else {
            browserForward.setAlpha(disable);
        }
    }

    @OnClick({R.id.address_go, R.id.address_clear, R.id.browser_back, R.id.browser_forward, R.id.jump_home, R.id.spider_msg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.address_go:
                try {
                    URL tempEditUrl = new URL(addressEdit.getText().toString());
                    deeWebView.loadUrl(tempEditUrl.toString());
                    deeWebView.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Dee1688Browser.this, "link err", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.address_clear:
                addressEdit.setText("");
                break;
            case R.id.browser_back:
                if (deeWebView != null && deeWebView.canGoBack())
                    deeWebView.goBack();
                break;
            case R.id.browser_forward:
                if (deeWebView != null && deeWebView.canGoForward())
                    deeWebView.goForward();
                break;
            case R.id.jump_home:
                break;
            case R.id.spider_msg:
                if (detail_1688_result == null) {
                    Toast.makeText(Dee1688Browser.this, "not a 1688 ware detail page", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(Dee1688Browser.this, Process1688Activity.class)
                            .putExtra("process_url",detail_1688_result));
                }
                break;
        }
    }
}
