package com.github.ontio.ontauth.control;

import ch.qos.logback.classic.Logger;
import com.github.ontio.ontauth.exception.ErrorInfo;
import com.github.ontio.ontauth.exception.MyException;
import com.github.ontio.ontauth.model.common.ResponseBean;
import com.github.ontio.ontauth.service.DeviceCodeService;
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

@Api(tags = "DeviceCode鉴权")
@RequestMapping("/v1")
@RestController
public class DeviceCodeController {

    //
    private Logger logger = (Logger) LoggerFactory.getLogger(DeviceCodeController.class);

    //
    private final DeviceCodeService deviceCodeService;
    private ValidateService validateService;

    @Autowired
    public DeviceCodeController(DeviceCodeService deviceCodeService,
                                ValidateService validateService) {
        this.deviceCodeService = deviceCodeService;
        this.validateService = validateService;
    }

    @ApiOperation("根据ontid获取DeviceCode")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    @PostMapping("/device")
    public ResponseEntity<ResponseBean> register(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {

        //
        ResponseBean rst = new ResponseBean();

        //
        Set<String> required = new HashSet<>();
        required.add("ontid");
        required.add("signature");

        //
        validateService.validateParamsKeys(obj, required);
        validateService.validateParamsValues(obj);

        //
        String ontid = (String) obj.get("ontid");
        String signature = (String) obj.get("signature");

        //
        Map<String, String> m = deviceCodeService.register(ontid, signature);
        //
        rst.setResult(m);
        rst.setCode(ErrorInfo.SUCCESS.code());
        rst.setMsg(ErrorInfo.SUCCESS.desc());
        return new ResponseEntity<>(rst, HttpStatus.OK);

    }


    @ApiOperation("验证DeviceCode")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    @PostMapping("/device/verification")
    public ResponseEntity<ResponseBean> verify(@RequestBody LinkedHashMap<String, Object> obj) throws MyException {

        //
        ResponseBean rst = new ResponseBean();

        //
        Set<String> required = new HashSet<>();
        required.add("ontid");
        required.add("device_code");

        //
        validateService.validateParamsKeys(obj, required);
        validateService.validateParamsValues(obj);

        //
        String ontid = (String) obj.get("ontid");
        String deviceCode = (String) obj.get("device_code");

        //
        deviceCodeService.verify(ontid, deviceCode);

        //
        rst.setResult(true);
        rst.setCode(ErrorInfo.SUCCESS.code());
        rst.setMsg(ErrorInfo.SUCCESS.desc());
        return new ResponseEntity<>(rst, HttpStatus.OK);
    }
}
