package com.itheima.tanhua.vo.mongo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {

    private Integer counts;    //总记录数
    private Integer pagesize;  //页大小
    private Integer pages;     //总页数
    private Integer page;     //当前页面
    private List<Object> items;   //列表

    public PageResult(Integer page,Integer pagesize,List<Object> items){
        this.page=page;
        this.pages=pagesize;
        this.items=items;
    }

}
