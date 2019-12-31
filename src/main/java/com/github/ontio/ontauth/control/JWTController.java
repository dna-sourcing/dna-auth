package com.github.ontio.ontauth.control;

import ch.qos.logback.classic.Logger;
import com.github.ontio.ontauth.exception.ErrorInfo;
import com.github.ontio.ontauth.model.common.ResponseBean;
import com.github.ontio.ontauth.service.JWTService;
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

@Api(tags = "JWT鉴权")
@RequestMapping("/v1")
@RestController
public class JWTController {

    //
    private Logger logger = (Logger) LoggerFactory.getLogger(JWTController.class);

    //
    private final JWTService jwtService;
    private ValidateService validateService;

    @Autowired
    public JWTController(JWTService jwtService,
                         ValidateService validateService) {
        this.jwtService = jwtService;
        this.validateService = validateService;
    }

    @ApiOperation("根据ontid获取access_token")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    @PostMapping("/jwt")
    public ResponseEntity<ResponseBean> register(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {

        //
        ResponseBean rst = new ResponseBean();

        //
        Set<String> required = new HashSet<>();
        required.add("user_dnaid");

        //
        validateService.validateParamsKeys(obj, required);
        validateService.validateParamsValues(obj);

        //
        String user_ontid = (String) obj.get("user_dnaid");

        //
        Map<String, String> m = jwtService.register(user_ontid);

        //
        rst.setResult(m);
        rst.setCode(ErrorInfo.SUCCESS.code());
        rst.setMsg(ErrorInfo.SUCCESS.desc());
        return new ResponseEntity<>(rst, HttpStatus.OK);
    }


    @ApiOperation("验证access_token")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    @PostMapping("/jwt/verification")
    public ResponseEntity<ResponseBean> verify(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {

        //
        ResponseBean rst = new ResponseBean();

        //
        Set<String> required = new HashSet<>();
        required.add("access_token");

        //
        validateService.validateParamsKeys(obj, required);
        validateService.validateParamsValues(obj);

        //
        String access_token = (String) obj.get("access_token");

        //
        jwtService.verify(access_token);

        //
        rst.setResult(true);
        rst.setCode(ErrorInfo.SUCCESS.code());
        rst.setMsg(ErrorInfo.SUCCESS.desc());
        return new ResponseEntity<>(rst, HttpStatus.OK);
    }
}
