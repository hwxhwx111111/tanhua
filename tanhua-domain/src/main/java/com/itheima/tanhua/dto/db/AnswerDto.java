package com.itheima.tanhua.dto.db;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 袁鹏
 * @date 2022-10-11-18:21
 */
@Data
public class AnswerDto implements Serializable {
    private List<Answer> answers;

    @Data
    public static class Answer implements Serializable{
        private String questionId;
        private String optionId;
    }
}
