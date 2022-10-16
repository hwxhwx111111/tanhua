package com.itheima.tanhua.pojo.db;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class SoulQuestion implements Serializable {
    private String id;
    private String question;
    private List<SoulOption> options;
}
