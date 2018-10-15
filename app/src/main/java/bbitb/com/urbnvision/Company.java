package bbitb.com.urbnvision;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import bbitb.com.urbnvision.models.Review;

public class Company implements Serializable {
    private String uid;
    private String username;
    private String name;
    private String email;
    private String spin;
    private String phone;
    private String desc;
    private String image;
    private String website;
    private String notifications;

    private int totalVoters;
    private double totalRating;
    private int star1;
    private int star2;
    private int star3;
    private int star4;
    private int star5;

    private Review review;

    public Company(){

    }

    public Company(String uid, String username, String name, String email, String spin, String phone,
                   String desc, String website, String notifications, String image){
        this.uid = uid;
        this.username = username;
        this.name = name;
        this.email = email;
        this.spin = spin;
        this.phone = phone;
        this.desc = desc;
        this.image = image;
        this.website = website;
        this.notifications = notifications;



        /*this.totalVoters = totalVoters;
        this.totalRating = totalRating;
        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.star5 = star5;*/

        //this.review = review;
    }

    public Company(String username, String email, String spin, String uid){
        this.username = username;
        this.email = email;
        this.spin = spin;
        this.uid = uid;
    }

    public Company(String uid, String username, String email, String spin , String image, String phone) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.spin = spin;
        this.image = image;
        this.phone = phone;
    }

    public Company(int totalVoters, double totalRating, int star1, int star2, int star3, int star4, int star5) {
        this.totalVoters = totalVoters;
        this.totalRating = totalRating;
        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.star5 = star5;

    }


    public Company( String username, String uid, double totalRating, int totalVoters, int star1, int star2, int star3, int star4, int star5) {
        this.username = username;
        this.uid = uid;
        this.totalVoters = totalVoters;
        this.totalRating = totalRating;
        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.star5 = star5;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("email", email);
        result.put("name", name);
        result.put("desc", desc);
        result.put("website", website);
        result.put("phone", phone);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSpin() {
        return spin;
    }

    public void setSpin(String spin) {
        this.spin = spin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTotalVoters() {
        return totalVoters;
    }

    public void setTotalVoters(int totalVoters) {
        this.totalVoters = totalVoters;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public int getStar1() {
        return star1;
    }

    public void setStar1(int star1) {
        this.star1 = star1;
    }

    public int getStar2() {
        return star2;
    }

    public void setStar2(int star2) {
        this.star2 = star2;
    }

    public int getStar3() {
        return star3;
    }

    public void setStar3(int star3) {
        this.star3 = star3;
    }

    public int getStar4() {
        return star4;
    }

    public void setStar4(int star4) {
        this.star4 = star4;
    }

    public int getStar5() {
        return star5;
    }

    public void setStar5(int star5) {
        this.star5 = star5;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String title) {
        this.username = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String url) {
        this.website = url;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }
}
