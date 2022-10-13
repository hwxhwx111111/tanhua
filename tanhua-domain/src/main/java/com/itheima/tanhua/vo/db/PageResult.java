package com.itheima.tanhua.vo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageResult<T> implements Serializable {
    private Integer counts;
    private Integer pagesize;
    private Integer pages;
    private Integer page;
    private List<T> items;

    public PageResult(Integer page, Integer pagesize, List <T> items){
        this.page=page;
        this.pagesize=pagesize;
        this.items=items;
    }



}
