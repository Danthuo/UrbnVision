package bbitb.com.urbnvision.models;

import java.util.Date;

public class Review {
    private Student student;
    private String review;
    private String reviewId;
    private Date timeStamp;
    private double totalStarGiven;


    public Review(Student student, String reviewId, String review, Date timeStamp, double totalStarGiven) {
        this.student = student;
        this.reviewId = reviewId;
        this.review = review;
        this.timeStamp = timeStamp;
        this.totalStarGiven = totalStarGiven;
    }

    public Review() {
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String uid) {
        this.reviewId = uid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getTotalStarGiven() {
        return totalStarGiven;
    }

    public void setTotalStarGiven(double totalStarGiven) {
        this.totalStarGiven = totalStarGiven;
    }
}
