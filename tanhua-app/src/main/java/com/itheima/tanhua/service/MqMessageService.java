package com.itheima.tanhua.service;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 袁鹏
 * @date 2022-09-30-0:38
 */
@Service
public class MqMessageService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private AmqpTemplate amqpTemplate;

    //发送日志消息
    public void sendLogMessage(Long userId,String type,String key,String busId) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("userId",userId.toString());
            map.put("type",type);
            map.put("logTime", LocalDate.now().format(formatter));
            map.put("busId",busId);
            String message = JSON.toJSONString(map);
            amqpTemplate.convertAndSend("tanhua.log.exchange", "log."+key,message);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    //发送动态审核消息
    public void sendAudiService(String movementId) {
        try {
            amqpTemplate.convertAndSend("tanhua.audit.exchange", "audit.movement",movementId);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}