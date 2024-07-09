//package com.edu.cqupt.shemining.common;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.edu.cqupt.shemining.model.Log;
//import com.edu.cqupt.shemining.model.User;
//import com.edu.cqupt.shemining.service.LogService;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//@Component
//@Aspect
//public class LogAspect {
//
//    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
//
//    @Resource
//    private LogService logService;
//
////    @Around("@annotation(autoLog)")
////    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, AutoLog autoLog) throws Throwable{
////
////        String name = autoLog.value();
////
////        String time = DateUtil.now();
////
////        String userName = "";
////        User user = JwtTokenUtils.getcurrentuser();
////        if (ObjectUtil.isNotNull(user)){
////            userName = user.getName();
////        }
////
////        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
////        String ip = request.getRemoteAddr();
////
////        //执行具体的接口
////        Result result = (Result) proceedingJoinPoint.proceed();
////
////        //再去往日志表里面写一条日志记录
////        Log log1 = new Log(null, name, time, userName, ip);
////        logService.save(log1);
////
////        return result;
////
////    }
//}
