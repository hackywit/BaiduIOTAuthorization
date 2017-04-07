package kincohackywit.kinco.cn.signtest.authorization;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by hackywit on 2017/4/5.
 */

public class Encrypt {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Charset UTF8 = Charset.forName(DEFAULT_ENCODING);

    public static String sha256Hex(String signingKey, String stringToSign) {
        try {
			/*/*///*****/***/*/*///*haobawoyijingfengle
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey.getBytes(UTF8), "HmacSHA256"));
            return new String(Hex.encodeHex(mac.doFinal(stringToSign.getBytes(UTF8))));
        } catch (Exception e) {
            throw new RuntimeException("Fail to generate the signature", e);
        }
    }
}
