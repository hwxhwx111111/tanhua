package com.itheima.tanhua.pojo.db;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class SoulQuestionLevel implements Serializable {
    private String id;
    private String name;
    private String cover;
    private String level;
    private Integer star;
    private List<SoulQuestion> questions;
    private Integer isLock;
    private String reportId;
}
