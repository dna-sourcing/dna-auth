package com.github.ontio.ontauth.util;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class GlobalVariable {

    //
    private static Logger logger = (Logger) LoggerFactory.getLogger(GlobalVariable.class);

    //
    public static String API_VERSION = "1.0.0";

    //
    public static Integer scrypt_N = 16384;
    public static Integer scrypt_r = 8;
    public static Integer scrypt_p = 1;

}
