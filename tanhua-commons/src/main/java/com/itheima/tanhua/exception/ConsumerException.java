package com.itheima.tanhua.exception;

import lombok.Data;

@Data
public class ConsumerException extends RuntimeException{

    public ConsumerException(String msg){
        super(msg);
    }


}
