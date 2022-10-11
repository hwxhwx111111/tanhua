package com.itheima.dubbo.filter;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.itheima.dubbo.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

//网关过滤器
@Component
@Order(-1)
public class AuthFilter implements GlobalFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 对登录注册进行放行
        //1.获取request对象
        ServerHttpRequest request = exchange.getRequest();
        String uri = request.getURI().getPath();
        if (StrUtil.equals(uri, "/user/login") || StrUtil.equals(uri, "/user/loginVerification")
                ||StrUtil.equals(uri, "/system/users/verification")
                ||StrUtil.equals(uri, "/system/users/login")) {
            //放行
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println(uri);
            return chain.filter(exchange);
        }

        //2.获取header中的token
        String token = request.getHeaders().getFirst("Authorization");
        //2.解析token，校验token
        Claims claimsBody = AppJwtUtil.getClaimsBody(token);
        int i = AppJwtUtil.verifyToken(claimsBody);

        //使用redis存id值
        String redisKey = "AUTH_USER_ID";
        //3.判断放行条件
        if (i == 0 || i == -1) {
            //设置redis中的id的失效时间 要比token失效时间短
            long tokenExpTime = claimsBody.getExpiration().getTime() ;
            //id失效时间
            long idExpTime = tokenExpTime - 3600;
            //4.获取当前登录者的id，存放在redis中，供其他接口路由调用
            Object userId = claimsBody.get("id");
            redisTemplate.opsForValue().set(redisKey, Convert.toStr(userId), idExpTime, TimeUnit.MILLISECONDS);
            //放行
            return chain.filter(exchange);
        }

        //5.返回未登陆的401状态码
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        //进行拦截
        return exchange.getResponse().setComplete();
    }
}
