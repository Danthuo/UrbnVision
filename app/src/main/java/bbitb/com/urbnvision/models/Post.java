package bbitb.com.urbnvision.models;

import java.io.Serializable;

public class Post implements Serializable{
    private String company;
    private String postText;
    private  String postImageUrl;
    private String postId;
    private long numLikes;
    private long numComments;
    private long timeCreated;

    public Post(){

    }

    public Post(String company, String postText, String postImageUrl, String postId, long numLikes, long numComments, long timeCreated) {
        this.company = company;
        this.postText = postText;
        this.postImageUrl = postImageUrl;
        this.postId = postId;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.timeCreated = timeCreated;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    /*public String getUser() {
        return company;
    }

    public void setUser(String company) {
        this.company = company;
    }*/

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
