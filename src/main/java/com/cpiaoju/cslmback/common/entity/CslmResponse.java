package com.cpiaoju.cslmback.common.entity;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * @author ziyou
 */
public class CslmResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = -8713837118340960775L;

    public CslmResponse code(HttpStatus status) {
        this.put("code", status.value());
        return this;
    }

    public CslmResponse message(String message) {
        this.put("message", message);
        return this;
    }

    public CslmResponse data(Object data) {
        this.put("data", data);
        return this;
    }

    public CslmResponse success() {
        this.code(HttpStatus.OK);
        return this;
    }

    public CslmResponse fail() {
        this.code(HttpStatus.INTERNAL_SERVER_ERROR);
        return this;
    }

    @Override
    public CslmResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
