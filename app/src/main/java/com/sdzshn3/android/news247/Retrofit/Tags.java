package com.sdzshn3.android.news247.Retrofit;

public class Tags {
    private String id;

    private String lastName;

    private String webUrl;

    private String[] references;

    private String bio;

    private String twitterHandle;

    private String bylineLargeImageUrl;

    private String bylineImageUrl;

    private String apiUrl;

    private String webTitle;

    private String firstName;

    private String type;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getLastName ()
    {
        return lastName;
    }

    public void setLastName (String lastName)
    {
        this.lastName = lastName;
    }

    public String getWebUrl ()
    {
        return webUrl;
    }

    public void setWebUrl (String webUrl)
    {
        this.webUrl = webUrl;
    }

    public String[] getReferences ()
    {
        return references;
    }

    public void setReferences (String[] references)
    {
        this.references = references;
    }

    public String getBio ()
    {
        return bio;
    }

    public void setBio (String bio)
    {
        this.bio = bio;
    }

    public String getTwitterHandle ()
    {
        return twitterHandle;
    }

    public void setTwitterHandle (String twitterHandle)
    {
        this.twitterHandle = twitterHandle;
    }

    public String getBylineLargeImageUrl ()
    {
        return bylineLargeImageUrl;
    }

    public void setBylineLargeImageUrl (String bylineLargeImageUrl)
    {
        this.bylineLargeImageUrl = bylineLargeImageUrl;
    }

    public String getBylineImageUrl ()
    {
        return bylineImageUrl;
    }

    public void setBylineImageUrl (String bylineImageUrl)
    {
        this.bylineImageUrl = bylineImageUrl;
    }

    public String getApiUrl ()
    {
        return apiUrl;
    }

    public void setApiUrl (String apiUrl)
    {
        this.apiUrl = apiUrl;
    }

    public String getWebTitle ()
    {
        return webTitle;
    }

    public void setWebTitle (String webTitle)
    {
        this.webTitle = webTitle;
    }

    public String getFirstName ()
    {
        return firstName;
    }

    public void setFirstName (String firstName)
    {
        this.firstName = firstName;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", lastName = "+lastName+", webUrl = "+webUrl+", references = "+references+", bio = "+bio+", twitterHandle = "+twitterHandle+", bylineLargeImageUrl = "+bylineLargeImageUrl+", bylineImageUrl = "+bylineImageUrl+", apiUrl = "+apiUrl+", webTitle = "+webTitle+", firstName = "+firstName+", type = "+type+"]";
    }
}
