package com.itheima.tanhua.vo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageResultM<T> implements Serializable {
    private Integer counts;
    private Integer pagesize;
    private Integer pages;
    private Integer page;
    private List<T> items;

    public PageResultM(Integer page, Integer pagesize, List<T> items) {
        this.counts = items.size();
        this.pagesize = pagesize;
        if (items.size() % pagesize == 0) {
            this.pages = items.size() / pagesize;
        } else {
            this.pages = (items.size() / pagesize) + 1;
        }
        this.page = page;
        this.items = items;
    }
}
