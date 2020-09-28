package com.tongchuang.general.core.utils;

import com.tongchuang.general.modular.main.entity.User;
import com.tongchuang.general.modular.main.model.vo.UserVO;
import org.springframework.core.NamedThreadLocal;

/**
 * @author
 * @date 2019/11/7
 */
public class UserContextHolder {

    private static final ThreadLocal<UserVO> userContextHolder = new NamedThreadLocal("User");

    public static UserVO get() {
        return userContextHolder.get();
    }
    
    public static String getUnionId() {
        return userContextHolder.get().getUnionId();
    }

    public static String getUserId() {
        return userContextHolder.get().getUserId();
    }
    
    public static void set(UserVO userVo) {
        userContextHolder.set(userVo);
    }

    public static void remove() {
        userContextHolder.remove();
    }

}
