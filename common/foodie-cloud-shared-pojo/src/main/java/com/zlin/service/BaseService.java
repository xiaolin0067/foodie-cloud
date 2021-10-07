package com.zlin.service;

import com.github.pagehelper.PageInfo;
import com.zlin.pojo.PagedGridResult;

import java.util.List;

/**
 * @author zlin
 * @date 20211006
 */
public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

}
