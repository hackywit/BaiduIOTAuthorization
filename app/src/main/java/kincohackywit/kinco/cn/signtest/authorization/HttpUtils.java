package kincohackywit.kinco.cn.signtest.authorization;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hackywit on 2017/4/5.
 */

public class HttpUtils {
    //定义一个编码格式变量
    private static final String DEFAULT_ENCODING = "UTF-8";

    //构建一个URI不保留字符位图
    private static BitSet URI_UNRESERVED_CHARACTERS = new BitSet();
    //构建一个百分比字符编码集字符串
    private static String[] PERCENT_ENCODED_STRINGS = new String[256];

    private static final Joiner queryStringJoiner = Joiner.on('&');
    //定义用于将集合String连接起来的字符'\n'
    private static final Joiner headerJoiner = Joiner.on('\n');

    private static final Set<String> defaultHeadersToSign = Sets.newHashSet();

    static {
        for (int i = 'a'; i <= 'z'; i++) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }
        URI_UNRESERVED_CHARACTERS.set('-');
        URI_UNRESERVED_CHARACTERS.set('.');
        URI_UNRESERVED_CHARACTERS.set('_');
        URI_UNRESERVED_CHARACTERS.set('~');

        for (int i = 0; i < PERCENT_ENCODED_STRINGS.length; ++i) {
            PERCENT_ENCODED_STRINGS[i] = String.format("%%%02X", i);//生成%加2位的16进制数，格式化后的数在任何情况下都是确定的
        }

        //在static语句中初始化默认请求头集合，因为是定义在static块中，所以该赋值语句只会被执行一次
        defaultHeadersToSign.add(Headers.HOST.toLowerCase());
        defaultHeadersToSign.add(Headers.CONTENT_LENGTH.toLowerCase());
        defaultHeadersToSign.add(Headers.CONTENT_TYPE.toLowerCase());
        defaultHeadersToSign.add(Headers.CONTENT_MD5.toLowerCase());
    }

    /**
     * RFC 3986规定，"URI非保留字符"包括以下字符：字母（A-Z，a-z）、数字（0-9）、连字号（-）、点号（.）、下划线（_)、波浪线（~），算法实现如下：
     * 1. 将字符串转换成UTF-8编码的字节流
     * 2. 保留所有“URI非保留字符”原样不变
     * 3. 对其余字节做一次RFC 3986中规定的百分号编码（Percent-encoding），即一个“%”后面跟着两个表示该字节值的十六进制字母，字母一律采用大写形式。
     */
    public static String UriEncode(String value) {
        try {
            StringBuilder builder = new StringBuilder();
            for (byte b : value.getBytes(DEFAULT_ENCODING)) {
                if (URI_UNRESERVED_CHARACTERS.get(b & 0xFF)) { //判断是否在保留字符集中，如果在就不进行编码
                    builder.append((char) b);
                } else {  //当判断到不在字符集中，需要对不保留字符进行编码
                    builder.append(PERCENT_ENCODED_STRINGS[b & 0xFF]);
                }
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 与UriEncode() 类似，区别是斜杠（/）不做编码。一个简单的实现方式是先调用UriEncode()，然后把结果中所有的`%2F`都替换为`/`
     */
    public static String UriEncodeExceptSlash(String path) {
        return UriEncode(path).replace("%2F", "/"); //不对"/"进行编码，其他的不保留字符需要编码
    }

    /**
     * 编码查询的部分
     */
    public static String GetCanonicalQueryString(Map<String, String> parameters) {
        //如果参数为空，返回空字符串
        if (parameters == null) {
            return "";
        }
        //定义一个数组来接收用来排序的编码后的查询语句
        List<String> parameterStrings = Lists.newArrayList();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            //对于key是authorization，直接忽略。
            if ("Authorization".equalsIgnoreCase(entry.getKey())) {
                continue;
            }
            String key = entry.getKey();
            checkNotNull(key, "parameter key should not be null");//谷歌第三方库提供的的一个函数，如果为null，打印第二个参数的信息
            String value = entry.getValue();
            if (value == null) {
                //对于只有key的项，转换为UriEncode(key) + "="的形式。
                parameterStrings.add(UriEncode(key) + '=');
            } else {
                //对于key=value的项，转换为 UriEncode(key) + "=" + UriEncode(value) 的形式。这里value可以是空字符串。
                parameterStrings.add(UriEncode(key) + '=' + UriEncode(value));
            }
        }
        //将上面转换后的所有字符串按照字典顺序排序。
        Collections.sort(parameterStrings);
        //将排序后的字符串按顺序用 & 符号链接起来。
        return queryStringJoiner.join(parameterStrings);
    }

    /**
     * 将字符串转化为map
     */
    public static Map<String, String> StringToMap(String mapString) {
        //传入参数:mapString 形如 text1=测试&text10=test
        if (mapString == null || mapString.equals("")){
            return null;
        }
        Map<String, String> map = Maps.newHashMap();
        String[] queryLines = mapString.split("&");
        for (int i = 0; i < queryLines.length; i++) {
            String[] items = queryLines[i].split("=");
            map.put(items[0], items[1]);
        }
        return map;
    }

    /**
     * 将list列表转化为map
     */
    public static Map<String, String> StringToMap(List<String> mapList) {
        Map<String, String> map = Maps.newHashMap();
        if (mapList == null) {
            return map;
        }
        for (String header : mapList) {
            String[] array = header.split(":");
            String key = "";
            String value = "";
            for (int i = 0; i < array.length; i++) {
                if (i == 0) {
                    key = array[0];
                } else if (i == 1) {
                    value = array[1];
                } else {
                    value = value + ":" + array[i];
                }
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * 从签名字符串选项中找到需要签名的头，和默认头按照规则计算出需要编码的头，放置到set集合中
     * 有编码头就用编码头，没有编码头，用默认头
     */
    public static Set<String> SetHeadersToSign(SignOptions options) {
        Set<String> set = Sets.newHashSet();
        if (options.getSignedHeaders() == null) {
            set.add(Headers.HOST);
            set.add(Headers.CONTENT_LENGTH);
            set.add(Headers.CONTENT_TYPE);
            set.add(Headers.CONTENT_MD5);
            set.add(Headers.BCE_PREFIX);
            return set;
        } else {
            String[] array = options.getSignedHeaders().split(";");
            for (int i = 0; i < array.length; i++) {
                set.add(array[i]);
            }
            set.add(Headers.HOST);//host是必须编码的
            return set;
        }
    }

    /**
     * 根据规则选择出所有需要编码的请求头放置到SortedMap中
     */
    public static SortedMap<String, String> GetHeadersToSign(Map<String, String> headers, Set<String> headersToSign) {
        //百度云API的唯一要求是Host域必须被编码。大多数情况下，我们推荐您对以下Header进行编码：Host,Content-Length,Content-Type,Content-MD5,所有以 x-bce- 开头的Header
        //如果这些Header没有全部出现在您的HTTP请求里面，那么没有出现的部分无需进行编码。
        //定义一个新的SortedMap接收需要编码的headers
        SortedMap<String, String> ret = Maps.newTreeMap();
        //需要签名的请求头不为空，将所有的头去空格，转小写，用于之后的比较
        if (headersToSign != null) {
            //定义一个集合来接收去空格，转小写之后的请求头
            Set<String> tempSet = Sets.newHashSet();
            for (String header : headersToSign) {
                tempSet.add(header.trim().toLowerCase());
            }
            headersToSign = tempSet;//将处理过的新的签名操作重新赋值给headersToSign
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            //请求头的值不为空且并不为空字符串
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                //要求编码的头不为空，并且请求的key在要编码的头域中，且编码的头不是anthorization，就将编码首部项放到集合中
                if (headersToSign != null && (headersToSign.contains(key.toLowerCase().trim()) || (key.toLowerCase().trim()).startsWith(Headers.BCE_PREFIX)) && !Headers.AUTHORIZATION.equalsIgnoreCase(key)) {
                    ret.put(key, entry.getValue());
                }
            }
        }
        return ret;
    }

    /**
     * 对需要编码的请求头，编码排序后放置到List集合中，最后用"\n"连接起来组成最终的canonicalHeaders字符串返回出去
     */
    public static String getCanonicalHeaders(SortedMap<String, String> headers) {
        //判断请求头hash集合是否为空，为空返回空字符串
        if (headers.isEmpty()) {
            return "";
        }
        //定义一个list集合来接收即将去空格，转小写后的请求项
        List<String> headerStrings = Lists.newArrayList();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            //判断是否有key，没有就continue
            String key = entry.getKey();
            if (key == null) {
                continue;
            }
            //判断是否有值，没有返回空
            String value = entry.getValue();
            if (value == null) {
                value = "";
            }
            //将得到的key，value，去空格，转小写，标准化之后添加到list集合中
            headerStrings.add(HttpUtils.UriEncode(key.trim().toLowerCase()) + ':' + HttpUtils.UriEncode(value.trim()));//这边value的值是不需要转小写的
        }
        //对list集合中的元素进行排序
        Collections.sort(headerStrings);
        //排序后，将集合中的元素用'\n'拼接成字符串返回最终的可用的CanonicalHeaders
        return headerJoiner.join(headerStrings);
    }
}
