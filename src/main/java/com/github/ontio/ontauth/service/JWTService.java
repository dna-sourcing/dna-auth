package com.github.ontio.ontauth.service;

import ch.qos.logback.classic.Logger;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.ontauth.exception.ErrorInfo;
import com.github.ontio.ontauth.exception.MyException;
import com.github.ontio.ontauth.util.jwt.MyJwt;
import com.github.ontio.ontauth.util.tmp.Base64ConvertUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Configuration
public class JWTService {

    //
    private Logger logger = (Logger) LoggerFactory.getLogger(JWTService.class);

    //
    private final String jwtOntid;
    private final String jwtPubKey;
    private final String jwtPriKey;
    private final long expTime;

    @Autowired
    public JWTService(@Value("${jwt.ontid}") String jwtOntid,
                      @Value("${jwt.PublicKey}") String jwtPubKey,
                      @Value("${jwt.PrivateKey}") String jwtPriKey,
                      @Value("${jwt.access.token.expire}") long expTime) throws UnsupportedEncodingException {
        this.jwtOntid = Base64ConvertUtil.decode(jwtOntid);
        this.jwtPubKey = Base64ConvertUtil.decode(jwtPubKey);
        this.jwtPriKey = Base64ConvertUtil.decode(jwtPriKey);
        this.expTime = expTime;
    }

    //
    public Map<String, String> register(String user_dnaid) throws Exception {

        //
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("type", "access_token");
        contentData.put("dnaid", user_dnaid);

        String accessToken = MyJwt.create().withIssuer(jwtOntid).withExpiresAt(new Date(new Date().getTime() + expTime)).withAudience(user_dnaid).withIssuedAt(new Date()).
                withJWTId(UUID.randomUUID().toString().replace("-", "")).withClaim("content", contentData).sign(jwtPriKey);

        //
        Map<String, String> m = new HashMap<>();
        m.put("access_token", accessToken);
        m.put("user_dnaid", user_dnaid);

        //
        return m;
    }

    //
    public void verify(String access_token) throws Exception {
        //
        verifyWithPublicKey(access_token, jwtPubKey);
    }

    //
    public void verifyWithPublicKey(String access_token,
                                    String publicKey) throws Exception {

        DecodedJWT jwt = JWT.decode(access_token);
        String content = String.format("%s.%s", jwt.getHeader(), jwt.getPayload());
        String signature = Base64ConvertUtil.decode(jwt.getSignature());
        Account account = new Account(false, Helper.hexToBytes(publicKey));
        boolean flag = account.verifySignature(content.getBytes(StandardCharsets.UTF_8), Helper.hexToBytes(signature));

        if (!flag) {
            throw new MyException(ErrorInfo.JWT_VERIFY_ERROR.code(), ErrorInfo.JWT_VERIFY_ERROR.desc(), null);
        }

        if (jwt.getExpiresAt().before(new Date())) {
            throw new MyException(ErrorInfo.JWT_EXPIRED.code(), ErrorInfo.JWT_EXPIRED.desc(), null);
        }
    }

}
