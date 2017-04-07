package kincohackywit.kinco.cn.signtest.authorization;

/**
 * Created by hackywit on 2017/4/5.
 */

public class BceCredentials {
    String accessKeyId;
    String secretKey;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public BceCredentials(String accessKeyId, String secretKey) {

        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
    }
}
