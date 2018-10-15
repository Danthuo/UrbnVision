package bbitb.com.urbnvision.models;

import java.io.Serializable;

public class Comment implements Serializable {
    private Student student;
    private String commentId;
    private long timeCreated;
    private String comment;
    private String uid;

    public Comment() {
    }

    public Comment(Student student, String commentId, long timeCreated, String comment) {
        this.student = student;
        this.commentId = commentId;
        this.timeCreated = timeCreated;
        this.comment = comment;
    }

    public Comment(String uid, String commentId, long timeCreated, String comment) {
        this.uid = uid;
        this.commentId = commentId;
        this.timeCreated = timeCreated;
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
