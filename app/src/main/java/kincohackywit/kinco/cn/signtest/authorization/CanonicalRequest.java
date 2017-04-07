package kincohackywit.kinco.cn.signtest.authorization;

import android.util.Log;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by hackywit on 2017/4/5.
 */

public class CanonicalRequest {
    private static final String TAG = "CanonicalRequest";

    String httpMethod;
    String canonicalURI;
    String canonicalQueryString;
    List<String> canonicalHeaders;

    SignOptions options;

    public void setOptions(SignOptions options) {
        this.options = options;
    }

    public CanonicalRequest(String httpMethod, String canonicalURI, String canonicalQueryString, List<String> canonicalHeaders,SignOptions options) {
        this.httpMethod = httpMethod;
        this.canonicalURI = canonicalURI;
        this.canonicalQueryString = canonicalQueryString;
        this.canonicalHeaders = canonicalHeaders;
        canonicalHeaders.add("host:iot.gz.baidubce.com");//因为host是必须加的，所有我们封装到里面
        this.options = options;
    }

    //根据变量求得需要返回的规范化原始请求
    public String requestEncode() {
        //httpMethod规范化
        String httpMethodCode = httpMethodEncode();
        //canonicalURI规范化
        String canonicalURICode = canonicalURIEncode();
        //canonicalQueryString规范化
        String canonicalQueryStringCode = canonicalQueryStringEncode();
        //canonicalHeaders规范化
        String canonicalHeadersCode = canonicalHeadersEncode();
        //组合成最终的CanonicalRequest
        String canonicalRequest = httpMethodCode + "\n" + canonicalURICode + "\n" + canonicalQueryStringCode + "\n" + canonicalHeadersCode;
        return canonicalRequest;
    }

    //HTTP Method：指HTTP协议中定义的GET、PUT、POST等请求，必须使用全大写的形式。百度云API所涉及的HTTP Method有五种：GET,POST,PUT,DELETE,HEAD
    private String httpMethodEncode() {
        String method = httpMethod.toUpperCase();
        if (method.equals("GET") || method.equals("POST") || method.equals("PUT") || method.equals("DELETE") || method.equals("HEAD")) {
            return method;
        } else {
            Log.d(TAG, "Method error");
            return null;//可以抛异常
        }
    }

    //CanonicalURI：是对URL中的绝对路径进行编码后的结果。要求绝对路径必须以“/”开头，不以“/”开头的需要补充上，空路径为“/”，即CanonicalURI = UriEncodeExceptSlash(Path)。
    private String canonicalURIEncode() {
        if (canonicalURI == null) { //空路径为"/"
            return "/";
        } else if (canonicalURI.startsWith("/")) { //绝对路径必须以"/"开头
            return HttpUtils.UriEncodeExceptSlash(canonicalURI);
        } else { //不以"/"开头的需要补上"/"
            return "/" + HttpUtils.UriEncodeExceptSlash(canonicalURI);
        }
    }

    //CanonicalQueryString：对于URL中的Query String（Query String即URL中“？”后面的“key1 = valve1 & key2 = valve2 ”字符串）进行编码后的结果。
    private String canonicalQueryStringEncode() {
        //构建键值对的Map集合
        Map<String, String> map = HttpUtils.StringToMap(canonicalQueryString);
        return HttpUtils.GetCanonicalQueryString(map);
    }

    //CanonicalHeaders：对HTTP请求中的Header部分进行选择性编码的结果。
    private String canonicalHeadersEncode() {
        //先构建出需要编码的头
        Map<String, String> headers = HttpUtils.StringToMap(canonicalHeaders);
        Set<String> headersToSign = HttpUtils.SetHeadersToSign(options);
        SortedMap<String, String> sortedMap = HttpUtils.GetHeadersToSign(headers, headersToSign);
        //对需要编码的头进行编码
        String canonicalHeaders = HttpUtils.getCanonicalHeaders(sortedMap);
        return canonicalHeaders;
    }
}
