package com.itheima.tanhua.Aspect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.itheima.tanhua.Interface.LogConfig;
import com.itheima.tanhua.utils.Constants;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class LogAspect {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Before(value="execution(* com.itheima.tanhua.service.*.*(..)) && @annotation(config)")
    public void checkUserState(JoinPoint pjp , LogConfig config) throws Throwable {
        //解析SpringEL获取动态参数
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String objId = parse(config.objId(), signature.getParameterNames(), pjp.getArgs());
        //构造Map集合
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", redisTemplate.opsForValue().get(Constants.USER_ID));
        msg.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        msg.put("objId", objId);
        msg.put("type", config.type());
        String message = JSON.toJSONString(msg);
        //发送消息
        try {
            amqpTemplate.convertSendAndReceive("tanhua.log.exchange",
                    "log."+config.key(),message);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String parse(String expression, String[] paraNames,Object [] paras) {
        if(StrUtil.isEmpty(expression)) return "";
        StandardEvaluationContext context = new StandardEvaluationContext();
        for(int i=0;i<paraNames.length;i++) {
            context.setVariable(paraNames[i], paras[i]);
        }
        Expression exp = new SpelExpressionParser().parseExpression(expression);
        Object value = exp.getValue(context);
        return value == null ? "" : value.toString();
    }
}