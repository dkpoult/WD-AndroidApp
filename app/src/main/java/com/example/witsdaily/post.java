package com.example.witsdaily;

import java.util.ArrayList;
import java.util.Date;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class post {
    public Date postDate;
    private int postID;
    private int numComments = 0;
    private int parentId;
    private String title;
    private String body;
    private int upvotes = 0 , downvotes = 0;
    private double controversy;

    {
        controversy = (double)(upvotes + downvotes) / max(abs(upvotes - downvotes), 1);
    }

    public boolean isComment, isVoted = false, isLocked = false;
    private ArrayList<post> comments;

    public post(String title, String body,  Date postDate){
        this.title = title;
        this.body = body;
        this.isComment = false;
        this.postDate = postDate;
        comments = new ArrayList<>();
    }

    public post(String title, String body,  Date postDate, int parentId){
        this.title = title;
        this.body = body;
        this.isComment = true;
        this.parentId = parentId;
        this.postDate = postDate;
        comments = new ArrayList<>();
    }



    public int getParentId(){
        return parentId;
    }
    public void setParentId(){
        this.parentId = parentId;
    }
    public int getPostID(){
        return postID;
    }
    public int getNumComments(){
        return numComments;
    }
    public String getTitle(){
        return title;
    }
    public String getBody(){
        return body;

    }
    public int getUpvotes(){
        return upvotes;
    }
    public int getDownvotes(){
        return downvotes;
    }
    public ArrayList<post> getComments(){
        return comments;
    }
    public double getControversy(){
        return controversy;
    }
    public void addUp() {
        if (this.isVoted) {
            upvotes += 1;
        }else{
            downvotes -= 1;
        }
    }
    public void setVotes(int upvotes, int downvotes){
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.controversy = (double)(upvotes-downvotes)/max(abs(upvotes-downvotes),1);
    }
    public void setPostDate(Date postDate){
        this.postDate = postDate;
    }
    public void setPostID(int postID){
        this.postID = postID;
    }
    public void setNumComments(int numComments){
        this.numComments = numComments;
    }
    public void setParentId(int parentId){
        this.parentId = parentId;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setBody(String body){
        this.body = body;
    }

    public void addDown(){
        if (this.isVoted) {
            downvotes += 1;
        }else{
            upvotes -= 1;
        }
    }
    public void lock(){
        this.isLocked = true;
    }
    public void unlock(){
        this.isLocked = false;
    }
    public void addComment(String title, String body, Date postDate){
        comments.add(new post(title, body, postDate, this.postID));
        this.numComments +=1;
    }

}
