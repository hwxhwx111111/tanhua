package com.itheima.tanhua.vo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    private Long counts = 0L;//总记录数
    private Integer pagesize = 10;//页大小
    private Long pages = 0L;//总页数
    private Integer page = 1;//当前页码
    private List<T> items = Collections.emptyList(); //列表

    public PageResult(Integer page, Integer pagesize, Long counts, List<T> list) {
        this.page = page;
        this.pagesize = pagesize;
        this.counts = counts;
        this.pages = counts % pagesize == 0 ? counts / pagesize : counts / pagesize + 1;
        this.items = list;
    }
    public PageResult(int page, int pageSize, List<T> items){
        this.page = page;
        this.pagesize = pageSize;
        this.items = items;
    }
}
