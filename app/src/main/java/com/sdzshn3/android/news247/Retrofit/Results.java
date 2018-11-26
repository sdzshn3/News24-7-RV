package com.sdzshn3.android.news247.Retrofit;

public class Results {
    private Tags[] tags;

    private String webUrl;

    private String isHosted;

    private String pillarId;

    private String sectionName;

    private String webTitle;

    private String type;

    private String webPublicationDate;

    private String id;

    private String sectionId;

    private String apiUrl;

    private String pillarName;

    private Fields fields;

    public Tags[] getTags ()
    {
        return tags;
    }

    public void setTags (Tags[] tags)
    {
        this.tags = tags;
    }

    public String getWebUrl ()
    {
        return webUrl;
    }

    public void setWebUrl (String webUrl)
    {
        this.webUrl = webUrl;
    }

    public String getIsHosted ()
    {
        return isHosted;
    }

    public void setIsHosted (String isHosted)
    {
        this.isHosted = isHosted;
    }

    public String getPillarId ()
    {
        return pillarId;
    }

    public void setPillarId (String pillarId)
    {
        this.pillarId = pillarId;
    }

    public String getSectionName ()
    {
        return sectionName;
    }

    public void setSectionName (String sectionName)
    {
        this.sectionName = sectionName;
    }

    public String getWebTitle ()
    {
        return webTitle;
    }

    public void setWebTitle (String webTitle)
    {
        this.webTitle = webTitle;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getWebPublicationDate ()
    {
        return webPublicationDate;
    }

    public void setWebPublicationDate (String webPublicationDate)
    {
        this.webPublicationDate = webPublicationDate;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getSectionId ()
    {
        return sectionId;
    }

    public void setSectionId (String sectionId)
    {
        this.sectionId = sectionId;
    }

    public String getApiUrl ()
    {
        return apiUrl;
    }

    public void setApiUrl (String apiUrl)
    {
        this.apiUrl = apiUrl;
    }

    public String getPillarName ()
    {
        return pillarName;
    }

    public void setPillarName (String pillarName)
    {
        this.pillarName = pillarName;
    }

    public Fields getFields ()
    {
        return fields;
    }

    public void setFields (Fields fields)
    {
        this.fields = fields;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [tags = "+tags+", webUrl = "+webUrl+", isHosted = "+isHosted+", pillarId = "+pillarId+", sectionName = "+sectionName+", webTitle = "+webTitle+", type = "+type+", webPublicationDate = "+webPublicationDate+", id = "+id+", sectionId = "+sectionId+", apiUrl = "+apiUrl+", pillarName = "+pillarName+", fields = "+fields+"]";
    }
}
