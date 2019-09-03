package com.spring.cloud.mybatis.util;


import core.page.Page;
import core.page.PageData;

public class PageUtil {

    /**
     * mongo 的分页
     * @param: [data]
     */
    public static PageData pageToPageData(org.springframework.data.domain.Page data) {
        Page page = new Page();
        page.setTotal(data.getTotalElements());
        page.setPageNum(data.getNumber() + 1);
        page.setPageSize(data.getSize());
        page.setHasNextPage(data.hasNext());
        return new PageData(page, data.getContent());
    }

    /**
     * mybatis 分页
     * @param: [data]
     */
    public static PageData pageToPageData(com.baomidou.mybatisplus.core.metadata.IPage data) {
        Page page = new Page();
        page.setTotal(data.getTotal());
        page.setPageNum(Integer.parseInt(data.getCurrent()+""));
        page.setPageSize(Integer.parseInt(data.getSize()+""));
        long current = data.getCurrent();
        long pages = data.getPages();
        page.setHasNextPage(current<pages);
        return new PageData(page, data.getRecords());
    }

}
