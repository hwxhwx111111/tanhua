package com.itheima.tanhua.pojo.mongo;



import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document("recommend_user")
public class RecommendUser implements Serializable {
    @Id
    private ObjectId id;
    private Long userId;
    private Long toUserId;
    private Long score;
    private String date;
}
