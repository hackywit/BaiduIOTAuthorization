package kincohackywit.kinco.cn.signtest.authorization;

import java.util.Date;

/**
 * Created by hackywit on 2017/4/5.
 */

public class SignOptions {
    //bce-auth-v{version}/{accessKeyId}/{timestamp}/{expireTime}/{signedHeaders}/{signature}
    String version;
    String accessKeyId;
    Date timestamp;
    int expireTime;
    String signedHeaders;//如果您按照我们的推荐范围进行编码，那么认证字符串中的 {signedHeaders} 可以直接留空，无需填写。
    String signature;

    public SignOptions(String accessKeyId, String signedHeaders) {
        this.version = "1";
        this.accessKeyId = accessKeyId;
        this.signedHeaders = signedHeaders;
        this.timestamp = new Date();
        this.expireTime = 1800;
    }

    public SignOptions(String accessKeyId) {
        this.version = "1";
        this.accessKeyId = accessKeyId;
        this.timestamp = new Date();
        this.expireTime = 1800;
    }

    public SignOptions(String version, String accessKeyId, Date timestamp, int expireTime) {
        this.version = version;
        this.accessKeyId = accessKeyId;
        this.timestamp = timestamp;
        this.expireTime = expireTime;
    }

    public SignOptions(String version, String accessKeyId, Date timestamp, int expireTime, String signedHeaders) {
        this.version = version;
        this.accessKeyId = accessKeyId;
        this.timestamp = timestamp;
        this.expireTime = expireTime;
        this.signedHeaders = signedHeaders;
    }

    public String getSignedHeaders() {
        return signedHeaders;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public void setSignedHeaders(String signedHeaders) {
        this.signedHeaders = signedHeaders;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAuthStringPrefix() {
        //bce-auth-v{version}/{accessKeyId}/{timestamp}/{expireTime}/
        String authStringPrefix = "bce-auth-v" + version + "/" + accessKeyId + "/" + DateUtils.formatAlternateIso8601Date(timestamp) + "/" + expireTime;

        return authStringPrefix;
    }

    public String createSignatureString() {
        if (signedHeaders == null) {
            return getAuthStringPrefix() + "/" + "" + "/" + signature;
        }
        return getAuthStringPrefix() + "/" + signedHeaders + "/" + signature;
    }
}
