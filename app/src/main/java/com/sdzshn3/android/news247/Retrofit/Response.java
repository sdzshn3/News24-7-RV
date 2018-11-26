package com.sdzshn3.android.news247.Retrofit;

import java.util.List;

public class Response {
    private String total;

    private String startIndex;

    private List<Results> results;

    private String orderBy;

    private String status;

    private String pages;

    private String pageSize;

    private String currentPage;

    private String userTier;

    public String getTotal ()
    {
        return total;
    }

    public void setTotal (String total)
    {
        this.total = total;
    }

    public String getStartIndex ()
    {
        return startIndex;
    }

    public void setStartIndex (String startIndex)
    {
        this.startIndex = startIndex;
    }

    public List<Results> getResults ()
    {
        return results;
    }

    public void setResults (List<Results> results)
    {
        this.results = results;
    }

    public String getOrderBy ()
    {
        return orderBy;
    }

    public void setOrderBy (String orderBy)
    {
        this.orderBy = orderBy;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getPages ()
    {
        return pages;
    }

    public void setPages (String pages)
    {
        this.pages = pages;
    }

    public String getPageSize ()
    {
        return pageSize;
    }

    public void setPageSize (String pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getCurrentPage ()
    {
        return currentPage;
    }

    public void setCurrentPage (String currentPage)
    {
        this.currentPage = currentPage;
    }

    public String getUserTier ()
    {
        return userTier;
    }

    public void setUserTier (String userTier)
    {
        this.userTier = userTier;
    }
}
