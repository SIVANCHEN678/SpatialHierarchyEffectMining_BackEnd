//package com.edu.cqupt.shemining.common;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.annotation.Resource;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Resource
//    private JwtIntercepter jwtIntercepter;
//
//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        //指定controller统一的接口前据,相当于:在url上拼了一个/api/xxX
//        configurer.addPathPrefix("/", c -> c.isAnnotationPresent(RestController.class ));
//    }
//    //加自定义拦截器JwtInterceptor，设国拦截规则
//    @Override
//    public void addInterceptors(InterceptorRegistry registry){
//        registry.addInterceptor(jwtIntercepter).addPathPatterns("/**")
//                .excludePathPatterns("/admin/user/login")
//                .excludePathPatterns("/admin/user/register");
//    }
//}
