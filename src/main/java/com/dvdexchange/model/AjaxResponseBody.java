package com.dvdexchange.model;

import com.fasterxml.jackson.annotation.JsonView;


import java.util.List;


public class AjaxResponseBody<T> {
    @JsonView(Views.Disk.class)
    String msg;

    @JsonView(Views.Disk.class)
    String code;

    @JsonView(Views.Disk.class)
    List<T> result;

    @JsonView(Views.Disk.class)
    int additionalValue;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getAdditionalValue() {
        return additionalValue;
    }

    public void setAdditionalValue(int additionalValue) {
        this.additionalValue = additionalValue;
    }

}
