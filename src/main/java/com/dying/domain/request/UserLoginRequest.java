package com.dying.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userAccount;
    private String userPassword;

    /**
     *  经度 纬度
     */
    private Double latitude;

    private Double longitude;
}
