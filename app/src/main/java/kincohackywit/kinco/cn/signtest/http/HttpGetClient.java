package kincohackywit.kinco.cn.signtest.http;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hackywit on 2017/4/6.
 */

public class HttpGetClient {
    String url;
    String query;
    List<String> canonicalHeaders;

    public HttpGetClient(String url, String query, List<String> canonicalHeaders) {
        this.url = url;
        this.query = query;
        this.canonicalHeaders = canonicalHeaders;
    }

    public void post() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request.Builder builder = new Request.Builder();
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
                    Request request = builder
                            .tag(2)
                            .url(url)
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
