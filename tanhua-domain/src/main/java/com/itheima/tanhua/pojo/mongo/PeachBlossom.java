package com.itheima.tanhua.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author 袁鹏
 * @date 2022-10-11-14:52
 */
@Document("peach_blossom")
@Data
public class PeachBlossom implements Serializable {
    @Id
    private String id;
    private Long userId;
    private String url;
}
