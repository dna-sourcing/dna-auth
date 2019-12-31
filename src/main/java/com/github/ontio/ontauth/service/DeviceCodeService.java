package com.github.ontio.ontauth.service;

import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.ontauth.exception.ErrorInfo;
import com.github.ontio.ontauth.exception.MyException;
import com.github.ontio.ontauth.model.dao.DeviceCodeDAO;
import com.github.ontio.ontauth.util.device.AESService;
import com.github.ontio.ontauth.mapper.DeviceCodeMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.github.ontio.common.Helper.hexToBytes;

@Service
@Configuration
public class DeviceCodeService {

    //
    private Logger logger = (Logger) LoggerFactory.getLogger(DeviceCodeService.class);

    //
    private DeviceCodeMapper deviceCodeMapper;
    private String[] urlList;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    public DeviceCodeService(DeviceCodeMapper deviceCodeMapper,
                             @Value("${ontology.url.list}") String[] urlList) {
        this.deviceCodeMapper = deviceCodeMapper;
        this.urlList = urlList;
    }

    //
    public Map<String, String> register(String ontid,
                                        String signature) throws Exception {

        //
        boolean f = verifySignature(ontid, ontid, signature);
        if (!f) {
            throw new MyException(ErrorInfo.DEVICE_VERIFY_ERROR.code(), ErrorInfo.DEVICE_VERIFY_ERROR.desc(), null);
        }

        //
        String deviceCodeStr = "device" + UUID.randomUUID().toString().replace("-", "");
        String encryptedDeviceCode = com.github.ontio.common.Helper.toHexString(AESService.AES_CBC_Encrypt(deviceCodeStr.getBytes()));

        // 保存到本地数据库
        Example example = new Example(DeviceCodeDAO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ontid=", ontid);

        //
        DeviceCodeDAO existed = deviceCodeMapper.selectOneByExample(example);

        //
        if (existed == null) {
            //
            DeviceCodeDAO dcd = new DeviceCodeDAO();
            dcd.setOntid(ontid);
            dcd.setDeviceCode(encryptedDeviceCode);
            dcd.setCreateTime(new Date());
            //
            deviceCodeMapper.insertSelective(dcd);
        } else {
            //
            DeviceCodeDAO dcd = new DeviceCodeDAO();
            dcd.setDeviceCode(encryptedDeviceCode);
            dcd.setUpdateTime(new Date());
            //
            deviceCodeMapper.updateByExampleSelective(dcd, example);
        }

        //
        Map<String, String> rs = new HashMap<>();
        rs.put("DeviceCode", deviceCodeStr);
        return rs;
    }

    //
    public void verify(String ontid,
                       String deviceCode) throws MyException {

        //
        Example example = new Example(DeviceCodeDAO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ontid = ", ontid);

        //
        DeviceCodeDAO dcd = deviceCodeMapper.selectOneByExample(example);

        //
        if (dcd == null) {
            throw new MyException(ErrorInfo.DEVICE_ONTID_NOT_EXISTED.code(), ErrorInfo.DEVICE_ONTID_NOT_EXISTED.desc(), null);
        }

        //
        String encryptedDeviceCode = com.github.ontio.common.Helper.toHexString(AESService.AES_CBC_Encrypt(deviceCode.getBytes()));
        if (!encryptedDeviceCode.equals(dcd.getDeviceCode())) {
            throw new MyException(ErrorInfo.DEVICE_ONTID_NOT_MATCH.code(), ErrorInfo.DEVICE_ONTID_NOT_MATCH.desc(), null);
        }
    }

    public boolean verifySignature(String ontid,
                                   String origDataStr,
                                   String signatureStr) throws Exception {

        // redis key: dc_ontid  value:pubkey
        String k = "dc_" + ontid;
        String existedPubkey = (String) redisTemplate.opsForValue().get(k);

        //
        String pubkeyStr = "";
        if (StringUtils.isEmpty(existedPubkey)) {
            //
            OntSdk ontSdk = getOntSdk();
            String issuerDdo = ontSdk.nativevm().ontId().sendGetDDO(ontid);
            if (StringUtils.isEmpty(issuerDdo)) {
                // todo 说明还需要去链上register
                throw new MyException(ErrorInfo.DEVICE_ONTID_DDO_NOT_FOUND.code(), ErrorInfo.DEVICE_ONTID_DDO_NOT_FOUND.desc(), null);
            }
            pubkeyStr = JSON.parseObject(issuerDdo).getJSONArray("Owners").getJSONObject(0).getString("Value");
            //
            redisTemplate.opsForValue().set(k, pubkeyStr);
        } else {
            pubkeyStr = existedPubkey;
        }
        logger.debug("pubkey:{}", pubkeyStr);

        //
        Account account = new Account(false, hexToBytes(pubkeyStr));
        boolean flag;
        try {
            flag = account.verifySignature(origDataStr.getBytes(StandardCharsets.UTF_8), Base64.getDecoder().decode(signatureStr.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            //
            logger.error(e.getMessage());
            //
            throw new MyException(ErrorInfo.DEVICE_VERIFY_ERROR.code(), ErrorInfo.DEVICE_VERIFY_ERROR.desc(), null);
        }
        logger.debug("verify signature result:{}", flag);

        return flag;

    }

    //
    public OntSdk getOntSdk() {

        //
        OntSdk wm = OntSdk.getInstance();
        //
        String ontologyUrl = urlList[0];  // todo
        //
        String restUrl = ontologyUrl + ":" + "20334";
        String rpcUrl = ontologyUrl + ":" + "20336";
        String wsUrl = ontologyUrl + ":" + "20335";

        //
        wm.setRestful(restUrl);  // todo
        // wm.openWalletFile("identity.json");

        //
        return wm;
    }
}
