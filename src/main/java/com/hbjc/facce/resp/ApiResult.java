package com.hbjc.facce.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApiResult<T> implements Serializable {
    private int code;

    private String msg;

    private T data;
}
