package com.globits.da.utils;

public class PageUtil {
    public static Integer validatePageIndex(Integer pageIndex) {
        if(pageIndex > 0) {
            return --pageIndex;
        }
        return 0;
    }

    public static Integer validatePageSize(Integer pageSize) {
        if(pageSize < 1) {
            return 20;
        }
        return pageSize;
    }
}
