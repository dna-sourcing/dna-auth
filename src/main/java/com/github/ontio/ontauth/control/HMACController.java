package com.github.ontio.ontauth.control;

import ch.qos.logback.classic.Logger;
import com.github.ontio.ontauth.exception.ErrorInfo;
import com.github.ontio.ontauth.model.common.ResponseBean;
import com.github.ontio.ontauth.service.HMACService;
import com.github.ontio.ontauth.service.util.ValidateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "HAMC鉴权")
@RequestMapping("/v1")
@RestController
public class HMACController {

    //
    private Logger logger = (Logger) LoggerFactory.getLogger(HMACController.class);

    //
    private final HMACService hmacService;
    private ValidateService validateService;

    @Autowired
    public HMACController(HMACService hmacService,
                          ValidateService validateService) {
        this.hmacService = hmacService;
        this.validateService = validateService;
    }

    // 健康检查
    @GetMapping("/check")
    public ResponseEntity<ResponseBean> test() {

        //
        ResponseBean rst = new ResponseBean();

        //
        rst.setCode(0);
        rst.setMsg("SUCCESS");
        rst.setResult(true);
        return new ResponseEntity<>(rst, HttpStatus.OK);
    }

    @ApiOperation("根据 ontid 注册 appid 和 appkey")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    @PostMapping("/hmac")
    public ResponseEntity<ResponseBean> register(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {

        //
        ResponseBean rst = new ResponseBean();

        //
        Set<String> required = new HashSet<>();
        required.add("ontid");

        //
        validateService.validateParamsKeys(obj, required);
        validateService.validateParamsValues(obj);

        //
        String ontid = (String) obj.get("ontid");

        //
        Map<String, String> m = hmacService.register(ontid);
        //
        rst.setResult(m);
        rst.setCode(ErrorInfo.SUCCESS.code());
        rst.setMsg(ErrorInfo.SUCCESS.desc());
        return new ResponseEntity<>(rst, HttpStatus.OK);
    }


    @ApiOperation("验证")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    @PostMapping("/hmac/verification")
    public ResponseEntity<ResponseBean> verify(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {

        //
        ResponseBean rst = new ResponseBean();

        //
        Set<String> required = new HashSet<>();
        required.add("hmac_data");
        required.add("method");
        required.add("request_uri");
        required.add("body_md5_base64_str");

        //
        validateService.validateParamsKeys(obj, required);
        validateService.validateParamsValues(obj);

        //
        String hmacData = (String) obj.get("hmac_data");
        String method = (String) obj.get("method");
        String requestUri = (String) obj.get("request_uri");
        String bodyMD5Base64Str = (String) obj.get("body_md5_base64_str");

        //
        hmacService.verify(hmacData, method, requestUri, bodyMD5Base64Str);

        //
        rst.setResult(true);
        rst.setCode(ErrorInfo.SUCCESS.code());
        rst.setMsg(ErrorInfo.SUCCESS.desc());
        return new ResponseEntity<>(rst, HttpStatus.OK);
    }
}
