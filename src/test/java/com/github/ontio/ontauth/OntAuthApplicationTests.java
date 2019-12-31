package com.github.ontio.ontauth;

import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.ontauth.service.DeviceCodeService;
import com.github.ontio.ontauth.service.HMACService;
import com.github.ontio.ontauth.util.device.AESService;
import com.github.ontio.ontauth.util.tmp.Base64ConvertUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "file:/Volumes/Data/_work/201802_Ontology/ONTAuth/config/application-dev.properties")
public class OntAuthApplicationTests {

    @Autowired
    HMACService hmacService;

    @Autowired
    DeviceCodeService deviceCodeService;

    @Test
    public void example01() throws Exception {

        //
        // String AppId = "mdgDyjj4";
        // String AppSecret = "g6VD0d8oBTfuUa2z1RSBoQ==";
        // String AppId = "4ktavTIs";
        // String AppSecret = "NVBXT2lnPZyRdNU6GHdgAWHQoUMYrQ==";
        String AppId = "T0IZJ3Vv";
        String AppSecret = "bWVzOUJx4W5w0R8H9LP/hc+RWdSpQA==";

        //
        String method = "PUT";
        String requestUri = "/v1/auth-requesters/did:ont:AHzg7vYANf6XHnBL64hT3vorzj4v7sZpt3";
        String bodyMD5Base64Str = "wZXj/gbNoScf3NgKoZPrOA==";

        //
        UUID uuid = UUID.randomUUID();
        // String nonce = Base64ConvertUtil.encode(uuid.toString().getBytes(StandardCharsets.UTF_8));
        String nonce = "12121212";
        Long tsLong = System.currentTimeMillis() / 1000;
        // String requestTimeStamp = tsLong.toString();
        String requestTimeStamp = "1561522122";
        String rawData = AppId + method + requestUri + requestTimeStamp + nonce + bodyMD5Base64Str;
        String signature = Base64ConvertUtil.encode(sha256_HMAC(rawData, AppSecret));
        String hmacData = String.format("ont:%s:%s:%s:%s", AppId, signature, nonce, requestTimeStamp);

        //
        hmacService.verify(hmacData, method, requestUri, bodyMD5Base64Str);
    }

    @Test
    public void example02() throws Exception {

        String prikey = "3fb33f6ece9b9cba55936965c0eef8c15819bc74d7588c1feb3217b8948e1854";
        String ontid = "did:ont:AYcZZYxgAVw7uoizVJFGmEYEdbFomeXhEG";
        // pubkey  02c1c8d9ee6f5c3bbc901aa00406fe819b4cc5bb85e605c3970a97ac400416b764

        //
        Account account = new Account(Helper.hexToBytes(prikey), SignatureScheme.SHA256WITHECDSA);
        System.out.println(account.getAddressU160().toBase58());
        byte[] b = account.generateSignature(ontid.getBytes(StandardCharsets.UTF_8), SignatureScheme.SHA256WITHECDSA, null);

        String sig = new String(Base64.getEncoder().encode(b), StandardCharsets.UTF_8);

        //
        deviceCodeService.verifySignature(ontid, ontid, sig);
    }


    public static byte[] sha256_HMAC(String message,
                                     String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] MD5Encode(String origin) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] rs = md.digest(origin.getBytes("utf-8"));
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String encode(byte[] src) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(src);
        return new String(encodeBytes);
    }

    @Test
    public void test02() {

        //
        String AppId = "6Xj8aSGC";
        String AppSecret = "R3VUd0JWu2wEcAgYruBBEDC8HkUMwQ==";

        //http request method
        String method = "POST";
        //http request uri
        String requestUri = "/v1/kyc-data";

        //
        String hmacData = "ont:6Xj8aSGC:/j9j0oQOJNgodJBmt2LVcVcGH7UOr5BdK6n3MHnF2JE=:YTQxNjMyMDMtYTBhYi00YmYxLTlhOWItNWEwOGJlMzJmZGMy:1563257304";
        String bodyMD5Base64Str = "e3b+nSiakg+3/CjKe51y1w==";

        try {
            verify(hmacData, method, requestUri, bodyMD5Base64Str, AppSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test03() {

        String AppSecret = "NWJEdFdSKrFR/Y5P8Li+gEoWFb98ow==";

        //http request method
        String method = "POST";
        //http request uri
        String requestUri = "/v1/trustanchors/kyc-result";

        //
        // String hmacData = "ont:ozJPVCyl:IIN2VViWZHHfBrF+rws3DjJUWW526/GkVzfZZmNBmBk=:ZDVhNzZjNTEtZWFkZi00MDA4LTlkMjctNjAxNjRjYjg0MDBh:1563412173";
        String hmacData = "ont:ozJPVCyl:8N6Fmal2wN1kG6MLObyyK6kstA6UU77QJaxl/PmMZ7A=:MmQyY2E3MjYtNzFjMC00MTJmLWI3NjktODY1MjM4NmI5YTMy:1563347728";
        String bodyMD5Base64Str = "waZBdGle/KaFTeDWJ+qI1Q==";

        try {
            verify(hmacData, method, requestUri, bodyMD5Base64Str, AppSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test04() {

        String appId = "ozJPVCyl";
        String appKey = "NWJEdFdSKrFR/Y5P8Li+gEoWFb98ow==";

        String appKeyEnc = com.github.ontio.common.Helper.toHexString(AESService.AES_CBC_Encrypt(appKey.getBytes()));

        System.out.println(appKeyEnc);
    }

    public void verify(String hmacData,
                       String method,
                       String requestUri,
                       String bodyMD5Base64Str,
                       String AppSecret) throws Exception {

        //
        String[] hmacDataArray = hmacData.split(":");
        if (hmacDataArray.length != 5) {
            throw new Exception("length size incorrect.");
        }

        //
        String tag = hmacDataArray[0];
        String appId = hmacDataArray[1];
        String signature = hmacDataArray[2];
        String nonce = hmacDataArray[3];
        String requestTimeStamp = hmacDataArray[4];

        // 验证开头
        if (!tag.equals("ont")) {
            throw new Exception("tag incorrect.");

        }

        // 验证有效期
        long expireDate = Long.parseLong(requestTimeStamp) * 1000 + 86400000;  // 86400000ms 是 24h
        if (new Date(expireDate).before(new Date())) {
            throw new Exception("expired.");

        }

        // 验证签名
        String rawData = appId + method + requestUri + requestTimeStamp + nonce + bodyMD5Base64Str;

        String localSignature = Base64ConvertUtil.encode(sha256_HMAC(rawData, AppSecret));

        if (!localSignature.equals(signature)) {
            throw new Exception("verify failed.");
        } else {
            System.out.println("verify success!");
        }
    }

}
