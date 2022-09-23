package com.itheima.tanhua;


import com.itheima.autoconfig.template.HuanXinTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HuanxinTest2 {

    @Autowired
    private HuanXinTemplate huanXinTemplate;


    @Test
    public void test01(){

        huanXinTemplate.createUser("user005","123");

    }

}
