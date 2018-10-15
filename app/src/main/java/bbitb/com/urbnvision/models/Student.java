package bbitb.com.urbnvision.models;

import java.io.Serializable;

/**
 * Created by Daniel Thuo on 16/04/2018.
 */

public class Student implements Serializable {

    private String uid;
    private String username;
    private String email;
    private String spin;
    private String photoUrl;
    private String  phone;


    public Student(){

    }

    public Student(String username, String email, String spin, String uid){
        this.username = username;
        this.email = email;
        this.spin = spin;
        this.uid = uid;
    }

    public Student(String uid, String username, String email, String spin , String photoUrl, String phone) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.spin = spin;
        this.photoUrl = photoUrl;
        this.phone = phone;
    }

    public Student(String username, String email, String spin , String photoUrl, String phone) {
        this.username = username;
        this.email = email;
        this.spin = spin;
        this.photoUrl = photoUrl;
        this.phone = phone;
    }

    public Student(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpin() {
        return spin;
    }

    public void setSpin(String spin) {
        this.spin = spin;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
