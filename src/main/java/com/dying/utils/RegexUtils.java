package com.dying.utils;

import cn.hutool.core.lang.Validator;

    /**
     * @author daylight
     */
    public class RegexUtils {
        /**
         * 是否是无效手机格式
         * @param phone 要校验的手机号
         * @return true:符合，false：不符合
         */
        public static boolean isPhoneInvalid(String phone){
            return Validator.isMobile(phone);
        }
        /**
         * 是否是无效邮箱格式
         * @param email 要校验的邮箱
         * @return true:符合，false：不符合
         */
        public static boolean isEmailInvalid(String email){
            return Validator.isEmail(email);
        }

    }