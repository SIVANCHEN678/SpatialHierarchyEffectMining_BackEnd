//package com.edu.cqupt.shemining.common;
//
//import cn.hutool.core.util.StrUtil;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTVerifier;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.edu.cqupt.shemining.exception.CustomException;
//import com.edu.cqupt.shemining.model.User;
//import com.edu.cqupt.shemining.service.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Component
//public class JwtIntercepter implements HandlerInterceptor {
//
//    private static final Logger log = LoggerFactory.getLogger(JwtIntercepter.class);
//
//    @Resource
//    private UserService userService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request,HttpServletResponse response ,Object handler){
//        //获取token
//        String token = request.getHeader("token");
//        if (StrUtil.isBlank(token)){
//            token = request.getParameter("token");
//        }
//
//        //开始认证
//        if (StrUtil.isBlank(token)){
//            throw new CustomException("无token，请重新登录");
//        }
//
//        //获联token中的userid
//        String userId;
//        User user;
//        try{
//            userId = JWT.decode(token).getAudience().get(0);
//            //根据token中的userid查询数据库
//            user = userService.getById(Integer.parseInt(userId));
//        }catch (Exception e){
//            String errMassage = "token验证失败，请重新登录";
//            log.error(errMassage + ", token=" + token, e);
//            throw new CustomException(errMassage);
//        }
//        if (user == null){
//            throw new CustomException("用户不存在，请重新登录");
//        }
//
//        try{
//            JWTVerifier jWTVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
//            jWTVerifier.verify(token); //验证token
//        }catch (JWTVerificationException e){
//            throw new CustomException("token验证失败，请重新登录");
//        }
//        log.info("token拦截成功，允许放行");
//        return true;
//    }
//}
