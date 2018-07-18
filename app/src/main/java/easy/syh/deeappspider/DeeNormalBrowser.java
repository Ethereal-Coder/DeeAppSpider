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
import android.util.Log;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeeNormalBrowser extends AppCompatActivity {

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
    private static final String TAG_IMG = "Dee_img";
    private static final String TAG_HOST = "Dee_host";
    private static final String TAG_TITLE = "Dee_title";
    private DeeWebView deeWebView;
    private ArrayList<String> imgUrls = new ArrayList<>();
    private final int disable = 120;
    private final int enable = 255;
    private String dee_filter;
    private String title_x = "";
    private String title_j = "";
    private String detail_1688_result;
    private Disposable disposable;


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
                }else {
                    detail_1688_result = null;
                }
                String host = getHost(s);
                Log.d(TAG_HOST, "url: " + host);
                if (host.contains("taobao") || host.contains("tmall")) {
                    dee_filter = "tb";
                } else if (host.contains("jd")) {
                    dee_filter = "jd";
                } else if (host.contains("1688")) {
                    dee_filter = "1688";
                } else {
                    dee_filter = "normal";
                }
                imgUrls.clear();
                title_j = "";

                if (host.contains("taobao") && host.contains("detail")) {
                  getWebTitleByJsoup(s, "tb");
                } else if (host.contains("tmall") && host.contains("detail")) {
                  getWebTitleByJsoup(s, "tm");
                }
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16)
                    changGoForwardButton(webView);
            }

            @Override
            public void onLoadResource(WebView webView, String url) {
                super.onLoadResource(webView, url);
//                Log.i(TAG_IMG, "加载Res：" + url);
                if (isImageSuffix(url)) {
                    Log.i(TAG_IMG, "加载img：" + url);
                    if (dee_filter.equals("tb")) {
                        if ((url.contains("/tps"))
                                || (url.contains("imgextra"))
                                || (url.contains("tfscom"))
                                || (url.contains("uploaded"))) {
                            imgUrls.add(url);
                        } else {

                        }
                    } else if (dee_filter.equals("jd")) {
                        if (url.contains("da") || url.contains("mobilecms") || url.contains("jdphoto")) {
                            imgUrls.add(url);
                        }
                    } else if (dee_filter.equals("1688")) {
                        if (url.contains("ibank") || url.contains("mobilecms") || url.contains("jdphoto")) {
                            imgUrls.add(url);
                        }
                    } else {
                        imgUrls.add(url);
                    }
                }
            }
        });

        deeWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String s) {
                Log.e(TAG_TITLE, "title：" + s);
                title_x = s.trim();
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
                    Toast.makeText(DeeNormalBrowser.this, "link err", Toast.LENGTH_SHORT).show();
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
                    if (imgUrls.size() == 0) {
                        Toast.makeText(DeeNormalBrowser.this, "请换个网页尝试", Toast.LENGTH_LONG).show();
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < imgUrls.size(); i++) {
                          if (checkImgSize(imgUrls.get(i))){
                            list.add(imgUrls.get(i));
                          }
                        }
                        //Intent intent = new Intent(DeeNormalBrowser.this, ShareActivity.class);
                        //intent.putStringArrayListExtra("spider_img_list", imgUrls);
                        //intent.putExtra("spider_title", title_j.isEmpty() ? title_x : title_j);
                        //startActivity(intent);

                    }
                } else {
                    startActivity(new Intent(DeeNormalBrowser.this, Process1688Activity.class)
                            .putExtra("process_url",detail_1688_result));
                }
                break;
        }
    }

    private void getWebTitleByJsoup(String url, final String platform) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        io.reactivex.Observable.just(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override public void onNext(String url) {
                        try {
                            Document doc = Jsoup.connect(url).timeout(5000).get();
//                            Log.e("Dee_content", doc.toString());
                            if (platform.equals("tb")) {
                                Elements metas = doc.select("meta[name]");
                                for (Element meta : metas) {
                                    //Log.e("Dee_content",meta.toString());
                                    if (meta.attr("name").equals("keywords")) {
                                        String content = meta.attr("content");
                                        //Log.e("Dee_content",content);
                                        title_j = content;
                                    }
                                }
                            } else if (platform.equals("tm")) {
                                Elements metas = doc.select("meta[property]");
                                for (Element meta : metas) {
                                    //Log.e("Dee_content",meta.toString());
                                    if (meta.attr("property").equals("og:title")) {
                                        String content = meta.attr("content");
                                        //Log.e("Dee_content",content);
                                        title_j = content;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override public void onError(Throwable e) {
                    }

                    @Override public void onComplete() {

                    }
                });
    }




    /**
     * webp --> tps, imgextra,tfscom,uploaded
     * https://gw.alicdn.com/imgextra/i4/T1vsbrXgdgXXcK5RZ8_101517.jpg_970x970q50s150.jpg_.webp
     * https://gw.alicdn.com/tps/i4/TB1JnU9QXXXXXaEXXXXqa6X9pXX_720x720q75.jpg_.webp
     *
     * jd
     * png -- mobilecms --
     */
    private boolean isImageSuffix(String url) {
        return url.endsWith(".png")
                || url.endsWith(".PNG")
                || url.endsWith(".jpg")
                || url.endsWith(".JPG")
                || url.endsWith(".jpeg")
                || url.endsWith(".JPEG")
                || url.endsWith(".webp")
                || url.endsWith(".WEBP");
    }

    public String getHost(String url) {
        if (url == null || url.trim().equals("")) {
            return "";
        }
        String host = "";
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }

    private boolean checkImgSize(String xx) {
        return checkImgSize(xx, 300);
    }

    /**
     * 合适返回true
     */
    private boolean checkImgSize(String xx, int imgWidth) {
        int index_j = xx.indexOf(".jpg_");
        int index_p = xx.indexOf(".png_");
        if (index_p == -1) {
            if (index_j == -1) {

            } else {
                String xs = xx.substring(index_j + 5);
                int index_x = xs.indexOf("x");
                try {
                    Integer integer = Integer.getInteger(xs.substring(0, index_x));
                    if (integer < imgWidth) {
                        return false;
                    }
                } catch (Exception e) {

                }
            }
        } else {
            if (index_j == -1) {
                String xs = xx.substring(index_p + 5);
                int index_x = xs.indexOf("x");
                try {
                    Integer integer = Integer.getInteger(xs.substring(0, index_x));
                    if (integer < imgWidth) {
                        return false;
                    }
                } catch (Exception e) {

                }
            } else {
                int temp_index = index_j > index_p ? index_p : index_j;
                String xs = xx.substring(temp_index + 5);
                int index_x = xs.indexOf("x");
                try {
                    Integer integer = Integer.getInteger(xs.substring(0, index_x));
                    if (integer < imgWidth) {
                        return false;
                    }
                } catch (Exception e) {

                }
            }
        }
        return true;
    }

    private ArrayList<String> getOpImgUrls(ArrayList<String> imgUrls) {
        ArrayList<String> list = new ArrayList<>();
        for (String img : imgUrls) {
            int index_p = img.indexOf(".png");
            int index_j = img.indexOf(".jpg");
            if (index_p == -1) {
                if (index_j == -1) {
                    list.add(img);
                } else {
                    list.add(img.substring(0, index_j + 4));
                }
            } else {
                if (index_j == -1) {
                    list.add(img.substring(0, index_p + 4));
                } else {
                    int temp_index = index_j > index_p ? index_p : index_j;
                    list.add(img.substring(0, temp_index + 4));
                }
            }
        }
        return list;
    }


}
