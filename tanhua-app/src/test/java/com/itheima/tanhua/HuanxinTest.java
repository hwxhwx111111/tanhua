package com.itheima.tanhua;


import cn.hutool.core.collection.CollUtil;
import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMTextMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

public class HuanxinTest {

    private EMService service;

    @Before
    public void init() {
        EMProperties properties = EMProperties.builder()
                .setAppkey("1157220926140087#tanhua")
                .setClientId("YXA6gXk6JG8eSPeALWI7yqTZ4g")
                .setClientSecret("YXA6mUkEg3SLCqdsML7jJ-OeulMQF_g")
                .build();
        service = new EMService(properties);
    }

    //注册用户
    @Test
    public void test01() {
        service.user().create("user003", "hwx123").block();
    }

    //添加好友关系
    @Test
    public void test02() {

        // service.contact().add("user001","user002").block();

    }

    //发送消息
    @Test
    public void test03(){

        HashSet<String> set = CollUtil.newHashSet("user002");
        service.message().send("user001"
                ,"users"
                ,set
                ,new EMTextMessage().text("java")
                ,null).block();

    }
}
