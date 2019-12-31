package com.github.ontio.ontauth.exception;


public enum ErrorInfo {

    SUCCESS(0, "SUCCESS"),

    //
    PARAM_ERROR(61001, "FAIL, param missing."),
    PARAM_VALUE_ERROR(61002, "FAIL, param error."),

    // device code
    DEVICE_VERIFY_ERROR(71001, "signature verify failed."),
    DEVICE_ONTID_NOT_EXISTED(71002, "ontid not existed."),
    DEVICE_ONTID_NOT_MATCH(71003, "device code and ontid not match."),
    DEVICE_ONTID_DDO_NOT_FOUND(71004, "ontid ddo not found."),

    // hmac
    REQUEST_ERROR(81001, "too frequent request."),
    HMAC_TAG_ERROR(81002, "tag is not ont."),
    HMAC_EXPIRED(81003, "expired."),
    HMAC_LENGTH_ERROR(81004, "hmacDataArray length is not 5."),
    HMAC_APPID_NOT_EXISTED(81005, "appid not existed."),
    HMAC_VERIFY_ERROR(81006, "signature verify fail."),

    // JWT
    JWT_EXPIRED(91001, "expired."),
    JWT_VERIFY_ERROR(91002, "verify failed."),

    //
    EXCEPTION(63002, "FAIL, exception.");

    private Integer code;
    private String msg;

    ErrorInfo(Integer code,
              String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer code() {
        return code;
    }

    public String desc() {
        return msg;
    }


}
