package com.github.ontio.ontauth.model.common;

import lombok.Data;

@Data
public class ResponseBean {

    private Integer code;

    private String msg;

    private Object result;

    public ResponseBean(Integer code, String msg, Object result){
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public ResponseBean(){}


}
