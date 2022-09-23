package com.itheima.tanhua.vo.mongo;

import com.itheima.tanhua.pojo.db.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactVo implements Serializable {

    private Long id;
    private String userId;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;

    public static ContactVo init(UserInfo userInfo){
        ContactVo contactVo = new ContactVo();
        if(userInfo!=null){
            BeanUtils.copyProperties(userInfo,contactVo);
            contactVo.setUserId("hx"+userInfo.getId().toString());
        }
        return contactVo;
    }

}
