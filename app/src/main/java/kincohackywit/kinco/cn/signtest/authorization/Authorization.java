package kincohackywit.kinco.cn.signtest.authorization;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by hackywit on 2017/4/6.
 */

public class Authorization {
    String httpMethod;
    String canonicalURI;
    String canonicalQueryString;
    List<String> canonicalHeaders;

    SignOptions options;

    BceCredentials bceCredentials;

    public Authorization(String httpMethod, String canonicalURI, String canonicalQueryString, List<String> canonicalHeaders, SignOptions options, BceCredentials bceCredentials) {
        this.httpMethod = httpMethod;
        this.canonicalURI = canonicalURI;
        this.canonicalQueryString = canonicalQueryString;
        this.canonicalHeaders = canonicalHeaders;
        this.options = options;
        this.bceCredentials = bceCredentials;
    }

    public String getSignatureString(){
        //构造原始请求，CanonicalRequest = HTTP Method + "\n" + CanonicalURI + "\n" + CanonicalQueryString + "\n" + CanonicalHeaders
        CanonicalRequest request = new CanonicalRequest(httpMethod,canonicalURI,canonicalQueryString,canonicalHeaders,options);
        //对原始请求规范化UriEncode()
        String canonicalRequest = request.requestEncode();
        //获取认证字符串前缀
        String authStringPrefix = options.getAuthStringPrefix();
        //用HAMC算法对认证字符串前缀用Access Key进行签名生成signingKey
        String singingKey = Encrypt.sha256Hex(bceCredentials.getSecretKey(),authStringPrefix);
        //用HAMC算法对规范化CanonicalRequest用singingKey进行签名生成签名摘要signature
        String signature = Encrypt.sha256Hex(singingKey,canonicalRequest);
        //将signature放入option选项中
        options.setSignature(signature);
        //获得最终的签名字符串，bce-auth-v{version}/{accessKeyId}/{timestamp}/{expireTime}/{signedHeaders}/{signature}，注意最后的字符串是不带{}号的
        String signatureString = options.createSignatureString();
        return signatureString;
    }
}
