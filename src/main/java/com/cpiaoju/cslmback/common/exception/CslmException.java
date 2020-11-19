package com.cpiaoju.cslmback.common.exception;

/**
 * FEBS系统内部异常
 *
 * @author ziyou
 */
public class CslmException extends RuntimeException  {

    private static final long serialVersionUID = -994962710559017255L;

    public CslmException(String message) {
        super(message);
    }
}
