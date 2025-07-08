package com.dying.constant;


import java.util.List;

public interface UserConstant {

     String USER_LOGIN_STATE = "userLoginState";

     String USER_CHECK_CODE = "user:checkCode";

     String USER_SESSION_STATE="user:sessionState";

     int USER_PAGE_SIZE = 8;

     String USER_LIKE_STATE = "user:likeState";

     int USER_REDIS_EXPIRE = 60;

     String USER_DEFAULT_TAGS = "[女]";

     String USER_SEARCH="user:search";
    /**
     * 权限管理
     */
    int DEFAULT_ROLE = 0;
     int ADMIN_ROLE = 1;
}
