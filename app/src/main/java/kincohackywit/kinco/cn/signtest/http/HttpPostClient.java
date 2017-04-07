package kincohackywit.kinco.cn.signtest.http;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hackywit on 2017/4/5.
 */

public class HttpPostClient {
    private static final String TAG = "HttpPostClient";

    String url;
    String query;
    List<String> canonicalHeaders;
    String json;

    public static final MediaType JSON = MediaType.parse("text/plain;charset=UTF-8");


    public HttpPostClient(String url, String query, List<String> canonicalHeaders, String json) {
        this.url = url;
        this.query = query;
        this.canonicalHeaders = canonicalHeaders;
        this.json = json;
    }

    public void post() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request.Builder builder = new Request.Builder();
                    //注意：很多发送HTTP请求的第三方库，会添加或者删除你指定的header（例如：某些库会删除content-length:0这个header），如果签名错误，请检查你您真实发出的http请求的header，看看是否与签名时的header一样。
                    for (String list : canonicalHeaders) {
                        String key = "";
                        String value = "";
                        String[] array = list.split(":");
                        for (int i = 0; i < array.length; i++) {
                            if (i == 0) {
                                key = array[0];
                            } else if (i == 1) {
                                value = array[1];
                            } else {
                                value = value + ":" + array[i];
                            }
                        }
                        builder.addHeader(key, value);  //将请求头以键值对形式添加，可添加多个请求头
                    }
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(JSON, json);
                    Request request = builder
                            .tag(1)
                            .url(url)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("text",responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
