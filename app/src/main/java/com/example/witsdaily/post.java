package com.example.witsdaily;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class post{
    String postDate;
    private String postID;
    public String DovsID;
    public int voteType;
    private String parentId;
    private String title;
    private String body;
    private String sendee;
    private int upvotes = 0 , downvotes = 0;
    boolean isAnswer = false;
    private double controversy;

    {
        controversy = (double)(upvotes + downvotes) / max(abs(upvotes - downvotes), 1);
    }

    public boolean isComment, isVoted = false, isLocked = false;
    private ArrayList<post> comments = new ArrayList<>();

    post(String postID, String title, String body, String postDate, String postee){
        this.title = title + " : " + postee;
        this.postID = postID;
        this.body = body;
        this.isComment = false;
        this.postDate = postDate;
        this.sendee = postee;
    }

    post(String postID, boolean flag, String body, String postDate, String sendee, String parentId){
        this.title = sendee;
        this.body = body;
        this.postID = postID;
        this.isComment = true;
        this.parentId = parentId;
        this.sendee = sendee;
        this.postDate = postDate;
    }


    public String getSendee(){
        return sendee;
    }
    public void setSendee(String sendee){
        this.sendee = sendee;
    }
    String getParentId(){
        return parentId;
    }
    void setAnswer(){
        this.isAnswer = true;
    }
    public void setParentId(){
        this.parentId = parentId;
    }
    String getPostID(){
        return postID;
    }
    int getNumComments(){
        return this.comments.size();
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
    public void addUp(Context context) {
        DatabaseHelper myDB = new DatabaseHelper(context, "PhoneDatabase");
        if (this.isVoted) {
            upvotes += 1;
            downvotes -= 1;
            myDB.doUpdate("UPDATE VOTED SET TYPE = 1 WHERE postID =" + this.postID + ";");
            myDB.doQuery("INSERT OR IGNORE INTO VOTED VALUES (" + this.postID + ", 1);");
        }else{
            upvotes += 1;
            myDB.doQuery("INSERT OR IGNORE INTO VOTED VALUES (" + this.postID + ", 1);");
        }
        this.isVoted = true;
       myDB.doUpdate("UPDATE POST \n" +
                "SET\n" +
                "    upVotes = " + this.upvotes + ",\n" +
                "    downVotes = " + this.downvotes + "\n" +
                "WHERE\n" +
                "    postID =" + this.postID + "\n" + ";");

        Cursor c = myDB.doQuery("SELECT * FROM POST WHERE postID ="+ this.postID + ";");
        c.moveToFirst();
        System.out.println(c.getString(c.getColumnIndex("postID")));
        System.out.println(c.getString(c.getColumnIndex("upVotes")));
        this.voteType = 1;
        this.setControversy();
    }
    public void setControversy(){
        this.controversy = (double)(upvotes + downvotes) / max(abs(upvotes - downvotes), 1);
    }
    public void setVotes(int upvotes, int downvotes){
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.controversy = (double)(upvotes+downvotes)/max(abs(upvotes-downvotes),1);
    }
    public void setPostDate(String  postDate){
        this.postDate = postDate;
    }
    public void setPostID(String postID){
        this.postID = postID;
    }
    public void setNumComments(int numComments){
    }
    public void setParentId(String parentId){
        this.parentId = parentId;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setBody(String body){
        this.body = body;
    }

    public void addDown(Context context){
        DatabaseHelper myDB = new DatabaseHelper(context, "PhoneDatabase");
        if (this.isVoted) {
            upvotes -= 1;
            downvotes += 1;
            myDB.doUpdate("UPDATE VOTED SET TYPE = 0 WHERE postID =" + this.postID + ";");
            myDB.doQuery("INSERT OR IGNORE INTO VOTED VALUES (" + this.postID + ", 0);");
        }else{
            downvotes += 1;
            myDB.doQuery("INSERT OR IGNORE INTO VOTED VALUES (" + this.postID + ", 0);");
        }
        myDB.doUpdate("UPDATE POST \n" +
                "SET\n" +
                "    downVotes = " + this.downvotes + ",\n" +
                "    upVotes = " + this.upvotes + "\n" +
                "WHERE\n" +
                "    postID =" + this.postID + "\n" + ";");


        this.isVoted = true;
        this.voteType = 0;
        this.setControversy();
    }
    public void lock(Context context){
        DatabaseHelper myDB = new DatabaseHelper(context, "PhoneDatabase");
        this.isLocked = true;
        myDB.doUpdate("UPDATE POST \n" +
                "SET\n" +
                "    isLocked = 1\n" +
                "WHERE\n" +
                "    postID =" + this.postID + "\n" + ";");
        for(post i: comments){
            i.lock(context);
        }
    }
    void unlock(Context context) {
        this.isLocked = false;
        DatabaseHelper myDB = new DatabaseHelper(context, "PhoneDatabase");
            myDB.doUpdate("UPDATE POST \n" +
                    "SET\n" +
                    "    isLocked = 0\n" +
                    "WHERE\n" +
                    "    postID =" + this.postID + "\n" + ";");
        for (post i : comments) {
            i.unlock(context);
        }

    }
    void addComment(post Post){
        this.comments.add(Post);
    }
    public post getComment(int index){
        return this.comments.get(index);
    }

}
