package com.itheima.tanhua;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@Document(value="tb_person")
public class Person implements Serializable {

    @Id
    private ObjectId id;

    @Field("myname")
    private String name;

    private int age;

    private String address;
}