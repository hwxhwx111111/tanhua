package com.itheima.tanhua.pojo.mongo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("recommend_user")
public class RecommendUser implements Serializable {
    @Id
    private ObjectId id;
    private Long userId;
    private Long toUserId;
    private Double score;
    private String date;
}
