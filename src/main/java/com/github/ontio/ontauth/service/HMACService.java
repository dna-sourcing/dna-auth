package com.github.ontio.ontauth.service;

import ch.qos.logback.classic.Logger;
import com.github.ontio.common.Helper;
import com.github.ontio.ontauth.exception.ErrorInfo;
import com.github.ontio.ontauth.exception.MyException;
import com.github.ontio.ontauth.mapper.HMACMapper;
import com.github.ontio.ontauth.model.dao.HMACDAO;
import com.github.ontio.ontauth.util.device.AESService;
import com.github.ontio.ontauth.util.tmp.Base64ConvertUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@Configuration
public class HMACService {

    //
    private Logger logger = (Logger) LoggerFactory.getLogger(HMACService.class);

    //
    private HMACMapper hmacMapper;
    private long hmacExpTime;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    public HMACService(HMACMapper hmacMapper,
                       @Value("${hmac.request.expire}") long hmacExpTime) {
        this.hmacMapper = hmacMapper;
        this.hmacExpTime = hmacExpTime;
    }

    //
    public Map<String, String> register(String ontid) throws NoSuchAlgorithmException {

        // 一个 ontid 可以注册多个 appid

        //
        String salt = getRandomString(6);
        String appid;

        // 保证生成的appid的唯一性
        while (true) {
            //
            appid = getRandomString(8);
            //
            Example example = new Example(HMACDAO.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andCondition("app_id=", appid);
            int rst = hmacMapper.selectCountByExample(example);
            //
            if (rst == 0) {
                break;
            }
        }

        // 按照规范上的是 secret = base64(md5(base64(salt + md5(key))));
        // 只做一次 base64 貌似也可以？
        String appkey = Base64.getEncoder().
                encodeToString(byteMerrage(salt.getBytes(StandardCharsets.UTF_8), MD5Encode(appid)));

        //
        HashMap<String, String> res = new HashMap<>();
        res.put("ontid", ontid);
        res.put("appid", appid);
        res.put("appkey", appkey);

        //
        HMACDAO hmacdao = new HMACDAO();
        hmacdao.setOntid(ontid);
        hmacdao.setAppid(appid);
        hmacdao.setAppkey(com.github.ontio.common.Helper.toHexString(AESService.AES_CBC_Encrypt(appkey.getBytes())));
        hmacdao.setCreateTime(new Date());
        hmacMapper.insertSelective(hmacdao);

        //
        return res;
    }


    public void verify(String hmacData,
                       String method,
                       String requestUri,
                       String bodyMD5Base64Str) throws Exception {

        //
        String[] hmacDataArray = hmacData.split(":");if (hmacDataArray.length != 5) {
            throw new MyException(ErrorInfo.HMAC_LENGTH_ERROR.code(), ErrorInfo.HMAC_LENGTH_ERROR.desc(), null);
        }

        //
        String tag = hmacDataArray[0];
        String appId = hmacDataArray[1];
        String signature = hmacDataArray[2];
        String nonce = hmacDataArray[3];
        String requestTimeStamp = hmacDataArray[4];

        // redis nonce_appid lasttime
        // 防刷
        String k = appId + nonce;
        Date last = (Date) redisTemplate.opsForValue().get(k);
        if (StringUtils.isEmpty(last)) {
            redisTemplate.opsForValue().set(k, new Date());
        } else {
            //
            Date now = new Date();

            //
            long interval = now.getTime() - last.getTime();  // milliseconds

            //
            redisTemplate.opsForValue().set(k, now);

            //
            if (interval <= 5000) {
                //
                throw new MyException(ErrorInfo.REQUEST_ERROR.code(), ErrorInfo.REQUEST_ERROR.desc(), null);
            }
        }


        // 验证开头
        if (!tag.equals("ont")) {
            throw new MyException(ErrorInfo.HMAC_TAG_ERROR.code(), ErrorInfo.HMAC_TAG_ERROR.desc(), null);
        }

        // 验证有效期
        long expireDate = Long.parseLong(requestTimeStamp) * 1000 + hmacExpTime;
        if (new Date(expireDate).before(new Date())) {
            throw new MyException(ErrorInfo.HMAC_EXPIRED.code(), ErrorInfo.HMAC_EXPIRED.desc(), null);
        }

        // 查库
        Example example = new Example(HMACDAO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("app_id=", appId);
        HMACDAO existed = hmacMapper.selectOneByExample(example);
        if (existed == null) {
            throw new MyException(ErrorInfo.HMAC_APPID_NOT_EXISTED.code(), ErrorInfo.HMAC_APPID_NOT_EXISTED.desc(), null);
        }

        //
        String tmp = existed.getAppkey();
        byte[] decrypted = AESService.AES_CBC_Decrypt(Helper.hexToBytes(tmp));
        String appkey = new String(decrypted);

        // 验证签名
        String rawData = appId + method + requestUri + requestTimeStamp + nonce + bodyMD5Base64Str;

        String localSignature = Base64ConvertUtil.encode(sha256_HMAC(rawData, appkey));

        if (!localSignature.equals(signature)) {
            throw new MyException(ErrorInfo.HMAC_VERIFY_ERROR.code(), ErrorInfo.HMAC_VERIFY_ERROR.desc(), null);
        } else {
            System.out.println("verify success!");
        }
    }


    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static byte[] byteMerrage(byte[] head,
                                     byte[] tail) {
        byte[] temp = new byte[head.length + tail.length];
        System.arraycopy(head, 0, temp, 0, head.length);
        System.arraycopy(tail, 0, temp, head.length, tail.length);
        return temp;
    }

    public static byte[] MD5Encode(String origin) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] rs = md.digest(origin.getBytes(StandardCharsets.UTF_8));
        return rs;

    }

    /**
     * sha256_HMAC加密
     * @param message 消息
     * @param secret  秘钥
     * @return 加密后字符串
     */
    public static byte[] sha256_HMAC(String message,
                                     String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }
}
