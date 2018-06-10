package easy.syh.deeappspider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Process1688Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_1688);
        try {
            URL processUrl = new URL(getIntent().getStringExtra("process_url"));
            processData(processUrl.toString());
        } catch (Exception e) {
            finish();
        }
    }

    private void processData(String url) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, Result1688>() {
                    @Override
                    public Result1688 apply(String s) throws Exception {
                        String jsonStr = "nothing";
                        Document doc = Jsoup.connect(s).timeout(5000).get();
                        Elements scripts = doc.select("script");
                        for (Element script : scripts) {
                            if (script.toString().contains("wingxViewData[0]")) {
                                jsonStr = script.toString().replaceFirst("^.*=\\{", "{").replaceFirst("</script>$", "");
                                break;
                            }
                        }
                        if (jsonStr.equals("nothing")) {
                            throw new Exception("get json failed");
                        } else {
                            return new Gson().fromJson(jsonStr, Result1688.class);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Result1688, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Result1688 result1688) throws Exception {
                        final String detailUrl = "http:" + result1688.getDetailUrl();
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                emitter.onNext(detailUrl);
                                emitter.onComplete();
                            }
                        });
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String s) throws Exception {
                        Document doc = Jsoup.connect(s).timeout(5000).get();
                        List<String> detailImgs = new ArrayList<>();
                        Elements select = doc.select("img[src]");
                        for (int i = 0; i < select.size(); i++) {
                            String detailImg = select.get(i).attr("src").replace("\\\"", "");
                            if (!detailImg.endsWith("360x360.jpg")) {
                                detailImgs.add(detailImg);

                            }
                        }
                        return detailImgs;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<String> strings) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
