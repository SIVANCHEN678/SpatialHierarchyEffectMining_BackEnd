//package com.edu.cqupt.shemining.common;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.StrUtil;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.edu.cqupt.shemining.model.User;
//import com.edu.cqupt.shemining.service.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Date;
//
//@Component
//public class JwtTokenUtils {
//
//    private static UserService staticUserService;
//    private static final Logger log = LoggerFactory.getLogger((JwtTokenUtils.class));
//
//    @Resource
//    private UserService userService;
//
//    @PostConstruct
//    public void setUserService(){
//        staticUserService = userService;
//    }
//
//    public static String genToken(String userId,String password){
//        return JWT.create().withAudience(userId)  // 将 userId保存到token里面,作为载荷
//                .withExpiresAt(DateUtil.offsetHour(new Date(),2))//2小时后token过期.sign(Algonithm.HMAC256(password));1/以 password作为 token的密钥
//                .sign(Algorithm.HMAC256(password)); //以password作为token的密钥
//    }
//
//    public static User getcurrentuser(){
//        String token = null;
//        try {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            token = request.getHeader( "token");
//            if (StrUtil.isBlank(token)){
//                token = request.getParameter(token);
//            }
//            if (StrUtil.isBlank(token)){
//                log.error("获取当前登录的token失败, token:{}",token);
//                return null;
//            }
//            //解析token，获取用户的id
//            String adminId = JWT.decode(token).getAudience().get(0);
//            return staticUserService.getById(Integer.valueOf(adminId));
//        } catch (Exception e) {
//            log.error("获取当前登录的管理员信息失败，token=0", token, e);
//            return null;
//        }
//    }
//
//}
