package com.jntoo.db.utils;

import java.util.*;


public class Collect<T>{
    /**
     * 总页数
     */
    private long totalPages = 0;
    /**
     * 总行数
     */
    private long totalRows = 0;
    /**
     * 当前页
     */
    private long currentPage = 1;
    /**
     * 每页长度
     */
    private long pageSize = 12;

    private long firstRow = 0;


    /**
     * url 规则， 可以自定义 如：userlist/{page}
     */
    private String urlRule;
    /**
     * 数据对象
     */
    private List<T> lists = new ArrayList();

    public Collect() {
    }

    public Collect(long currentPage, long pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        calcFirstRow();
    }

    public Collect(long currentPage, long totalRows , long pageSize) {
        this.totalRows = totalRows;
        this.currentPage = currentPage < 1 ? 1 : currentPage;
        this.pageSize = pageSize;
        calcFirstRow();
        calcTotalPages();
    }

    /**
     * 判断是否有上一页
     * @return 返回得URL地址
     */
    public boolean IsPrev()
    {
        return currentPage > 1;
    }

    /**
     * 获取上一页的Url
     * @return 返回得URL地址
     */
    public String getPrevPage()
    {
        if(IsPrev()){
            return getUrlPath(currentPage-1);
        }
        return "";
    }

    /**
     * 是否有下一页
     * @return 是否下一页
     */
    public boolean IsNext()
    {
        return currentPage < totalPages;
    }

    /**
     * 获取下一页的URL 地址
     * @return 返回得URL地址
     */
    public String getNextPage()
    {
        if(IsNext()){
            return getUrlPath(currentPage+1);
        }
        return "";
    }

    /**
     * 获取第一页URL
     * @return 返回得URL地址
     */
    public String getFirstPage()
    {
        return getUrlPath(1);
    }

    /**
     * 获取最后一页URL
     * @return 返回得URL地址
     */
    public String getLastPage()
    {
        return getUrlPath(totalPages);
    }

    /**
     * 获取页码列表：实体类为 PageNumsPojo
     * @return 获取得页码
     */
    public List<PageNumsPojo> getPageNums()
    {
        // 取回页码
        long rollPage = 2;
        long show_nums = rollPage * 2 +1;
        long i=0;
        long start=0,end=0;
        List<PageNumsPojo> result = new ArrayList();
        if(totalPages < show_nums){
            start = 1;
            end = totalPages;
        }else if(currentPage < (1+rollPage)){
            start = 1;
            end = show_nums;
        }else if(currentPage >= (totalPages - rollPage)){
            start = totalPages - show_nums;
            end = totalPages;
        }else {
            start =  currentPage - rollPage;
            end   = currentPage + rollPage;
        }
        for (i=start;i<=end;i++)
        {
            PageNumsPojo pojo = new PageNumsPojo();
            pojo.page = i;
            pojo.url = getUrlPath(i);
            result.add(pojo);
        }
        return result;
    }


    public class PageNumsPojo{
        private long page;
        private String url;
        public long getPage() {
            return page;
        }
        public void setPage(long page) {
            this.page = page;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }



    /**
     * 获取替换成功的页码
     * @param page 页码
     * @return 返回得URL地址
     */
    protected String getUrlPath(long page)
    {
        return urlRule.replace("{page}" , String.valueOf(page));
    }

    public long getTotalPages() {
        return totalPages;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
        calcTotalPages();
        // 设置总行数,计算出总页数
    }

    private void calcTotalPages()
    {
        if( totalRows > 0 && pageSize > 0 )
        {
            double ceil = Double.valueOf(totalRows).doubleValue() / Double.valueOf(pageSize).doubleValue();
            totalPages = Double.valueOf(Math.ceil(ceil)).longValue();
        }
    }

    private void calcFirstRow()
    {
        firstRow = pageSize * (currentPage - 1);
    }

    public long getFirstRow() {
        return firstRow;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
        calcFirstRow();
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public String getUrlRule() {
        return urlRule;
    }

    public void setUrlRule(String urlRule) {
        this.urlRule = urlRule;
    }

    public List<T> getLists() {
        return lists;
    }

    public void add(T obj)
    {
        lists.add(obj);
    }

    public void addAll(Collection<T> list)
    {
        lists.addAll(list);
    }

    public void setLists(List lists) {
        this.lists = lists;
    }
}
