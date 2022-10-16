package com.itheima.tanhua.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 袁鹏
 * @date 2022-09-26-11:18
 */
@Data
@Document("focus_user")
public class FocusUser {
    @Id
    private String id;
    private Long created;
    private Long userId;
    private Long followUserId;
}
