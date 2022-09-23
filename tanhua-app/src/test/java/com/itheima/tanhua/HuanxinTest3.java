package com.itheima.tanhua;

import com.itheima.autoconfig.template.HuanXinTemplate;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.pojo.db.User;
import com.itheima.tanhua.utils.Constants;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HuanxinTest3 {

    @DubboReference
    private UserServiceApi userServiceApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    //批量注册
    @Test
    public void register() {
        List<User> users = userServiceApi.findAll();
        for (User user : users) {
            Boolean create = huanXinTemplate.createUser("hx" + user.getId(), "123456");
            if (create){
                user.setHxUser("hx" + user.getId());
                user.setHxPassword(Constants.INIT_PASSWORD);
                userServiceApi.update(user);
            }
        }
    }
}
