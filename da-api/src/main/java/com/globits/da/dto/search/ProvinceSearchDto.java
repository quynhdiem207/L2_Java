package com.globits.da.dto.search;

public class ProvinceSearchDto {
    private String keyword;
    private String orderBy;
    private Integer pageIndex;
    private Integer pageSize;

    public ProvinceSearchDto() {}

    public ProvinceSearchDto(String keyword, String orderBy, Integer pageIndex, Integer pageSize) {
        this.keyword = keyword;
        this.orderBy = orderBy;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
