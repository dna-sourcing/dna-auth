package com.github.ontio.ontauth.service.util;

import com.github.ontio.ontauth.exception.ErrorInfo;
import com.github.ontio.ontauth.exception.MyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ValidateService {

    //
    private void validateOntid(String ontid) throws MyException {
        //
        if (StringUtils.isEmpty(ontid) || ontid.length() != 42 || !ontid.toLowerCase().contains("did:ont:A".toLowerCase()))
            throw new MyException(ErrorInfo.PARAM_VALUE_ERROR.code(), ErrorInfo.PARAM_VALUE_ERROR.desc(), null);
    }

    public void validateParamsKeys(LinkedHashMap<String, Object> obj,
                                   Set<String> required) throws MyException {
        Iterator<String> requiredIterator = required.iterator();
        while (requiredIterator.hasNext()) {
            String next = requiredIterator.next();
            if (!obj.containsKey(next))
                throw new MyException(ErrorInfo.PARAM_ERROR.code(), ErrorInfo.PARAM_ERROR.desc(), null);
        }
    }

    public void validateParamsValues(LinkedHashMap<String, Object> obj) throws MyException {

        if (obj.containsKey("dapp_ontid")) {
            String dapp_ontid = (String) obj.get("dapp_ontid");
            validateOntid(dapp_ontid);
        }

        if (obj.containsKey("ontid")) {
            String ontid = (String) obj.get("ontid");
            validateOntid(ontid);
        }

        if (obj.containsKey("signature")) {
            String signature = (String) obj.get("signature");
            // TODO
            if (StringUtils.isEmpty(signature))
                throw new MyException(ErrorInfo.PARAM_VALUE_ERROR.code(), ErrorInfo.PARAM_VALUE_ERROR.desc(), null);
        }

        // if (obj.containsKey("body_md5_base64_str")) {
        //     String bodyMD5Base64Str = (String) obj.get("body_md5_base64_str");
        //     // TODO
        //     if (StringUtils.isEmpty(bodyMD5Base64Str))
        //         throw new MyException(ErrorInfo.PARAM_VALUE_ERROR.code(), ErrorInfo.PARAM_VALUE_ERROR.desc(), null);
        // }

    }
}
