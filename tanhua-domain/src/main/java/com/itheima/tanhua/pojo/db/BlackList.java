package com.itheima.tanhua.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackList extends BasePojo implements Serializable {
    private Long id;
    private Long userId;
    private Long blackUserId;
}
