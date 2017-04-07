package kincohackywit.kinco.cn.signtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

import kincohackywit.kinco.cn.signtest.authorization.Authorization;
import kincohackywit.kinco.cn.signtest.authorization.BceCredentials;
import kincohackywit.kinco.cn.signtest.authorization.SignOptions;
import kincohackywit.kinco.cn.signtest.http.HttpGetClient;
import kincohackywit.kinco.cn.signtest.http.HttpPostClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button authenticate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticate = (Button) findViewById(R.id.authenticate);
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String method = "POST";
                String url = "/v1/endpoint";
                String query = "";
                List<String> canonicalHeaders = Lists.newArrayList();
                canonicalHeaders.add("content-type: text/plain;charset=UTF-8");
                SignOptions options = new SignOptions("399133cc71b14fa49f453035024ba2b4","host;content-type");
                BceCredentials bceCredentials = new BceCredentials("399133cc71b14fa49f453035024ba2b4", "2f1eedc846194ddeaf4516be9fd675f3");
                Authorization authorization = new Authorization(method, url, query, canonicalHeaders, options, bceCredentials);
                String signatureString = authorization.getSignatureString();
                canonicalHeaders.add("Authorization:" + signatureString);
                url = "http://iot.gz.baidubce.com" + url;

                String json = "{\"endpointName\":\"cxbpoint2\"}";
                HttpPostClient httpPostClient = new HttpPostClient(url, query, canonicalHeaders, json);//传入http报文请求行，首部项，主体
                try {
                    httpPostClient.post();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                HttpGetClient httpGetClient = new HttpGetClient(url, query, canonicalHeaders);//传入http报文请求行，首部项，主体
//                try {
//                    httpGetClient.post();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}
