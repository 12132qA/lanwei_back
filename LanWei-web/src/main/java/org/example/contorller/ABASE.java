package org.example.contorller;

/*
* 获取ip 地址
* */

import org.example.dto.SessionWebUserDto;
import org.example.pojo.contants.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ABASE {

    /**
     * 获取用户IP
     * */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
    /**
     * 获取用户信息
     * */
    protected static SessionWebUserDto getUserInfoFormSession(HttpSession httpSession){
        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) httpSession.getAttribute(Constants.SESSION_KEY);
        return sessionWebUserDto;
    }
}
