package com.zlin.enums;

/**
 * 支付方式 枚举
 */
public enum PayMethod {

    /**
     * 支付方式
     */
	WEIXIN(1, "微信"),
	ALIPAY(2, "支付宝");

	public final Integer type;
	public final String value;

	PayMethod(Integer type, String value){
		this.type = type;
		this.value = value;
	}

    /**
     * 不合法的支付方式
     * @return 是否不合法
     */
	public static boolean illegalPayMethod(Integer payType) {
	    if (payType == null) {
	        return true;
        }
        for (PayMethod value : PayMethod.values()) {
            if (value.type.equals(payType)) {
                return false;
            }
        }
	    return true;
    }

}
