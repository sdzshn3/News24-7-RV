package com.sdzshn3.android.news247.Retrofit;

public class Fields {
    private String body;

    private String thumbnail;

    public String getBody ()
    {
        return body;
    }

    public void setBody (String body)
    {
        this.body = body;
    }

    public String getThumbnail ()
    {
        return thumbnail;
    }

    public void setThumbnail (String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [body = "+body+", thumbnail = "+thumbnail+"]";
    }
}
