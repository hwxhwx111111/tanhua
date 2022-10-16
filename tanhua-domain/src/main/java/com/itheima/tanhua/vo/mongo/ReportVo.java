package com.itheima.tanhua.vo.mongo;

import com.itheima.tanhua.pojo.db.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class ReportVo implements Serializable {
    private String conclusion;
    private String cover;
    private List<Dimension> dimensions;
    private List<UserInfo> similarYou;

    @Data
    @AllArgsConstructor
    public static class Dimension{
        private String key;
        private String value;
    }
}
