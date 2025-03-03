package com.zlin.pojo;

import com.github.pagehelper.PageInfo;

import java.util.List;

public class PagedGridResult {
	
	private int page;			// 当前页数
	private int total;			// 总页数	
	private long records;		// 总记录数
	private List<?> rows;		// 每行显示的内容

	public PagedGridResult() {}

    public PagedGridResult(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        setPage(page);
        setRows(list);
        setTotal(pageList.getPages());
        setRecords(pageList.getTotal());
    }

	public PagedGridResult(int page, int total, long records, List<?> rows) {
		this.page = page;
		this.total = total;
		this.records = records;
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public long getRecords() {
		return records;
	}
	public void setRecords(long records) {
		this.records = records;
	}
	public List<?> getRows() {
		return rows;
	}
	public void setRows(List<?> rows) {
		this.rows = rows;
	}
}
