package com.zlin.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zlin
 * @date 20210124
 */
public enum ImageFileSuffix {

    /**
     * 文件类型
     */
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png")
    ;

    public final String val;

    ImageFileSuffix(String val) {
        this.val = val;
    }

    public static boolean illegalSuffix(String param) {
        if (StringUtils.isBlank(param)) {
            return true;
        }
        for (ImageFileSuffix value : ImageFileSuffix.values()) {
            if (param.equals(value.val)) {
                return false;
            }
        }
        return true;
    }
}
