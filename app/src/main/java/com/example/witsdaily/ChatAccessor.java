package com.example.witsdaily;

import android.content.Context;
import android.icu.text.SymbolTable;

import android.util.Log;
import android.view.View;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;


public abstract class ChatAccessor {
    WebSocket ws = null;
    private StompClient mStompClient;
    String personNumber,userToken,courseCode;
    public static final String ANDROID_EMULATOR_LOCALHOST = "10.0.2.2";
    public static final String SERVER_PORT = "8080";
    public ChatAccessor(String pPersonNumber,String pUserToken,String pCourseCode){
        personNumber = pPersonNumber;
        userToken = pUserToken;
        courseCode = pCourseCode;
    }
    // do some connection shiz with socckets
    public JSONArray getPreviousMessages() {

        JSONArray messages = new JSONArray();
        messages.put("message one");
        messages.put("message two");
        messages.put("boom random middle message");
        messages.put("message three");

        return messages;
        //personNumber and userToken
    }


 public boolean establishConnection() {
     List<StompHeader> headers = new ArrayList<>();
     headers.add(new StompHeader("personNumber", personNumber));
     headers.add(new StompHeader("userToken", userToken));

     mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + ANDROID_EMULATOR_LOCALHOST
             + ":" + SERVER_PORT + "https://wd.dimensionalapps.com/websocket");

     mStompClient.connect(headers);
     if (!mStompClient.isConnected())
         return false;



     mStompClient.topic("/topic/"+courseCode).subscribe(new Consumer<StompMessage>() {
         @Override
         public void accept(StompMessage topicMessage) throws Exception {
             onMessage(topicMessage);
             System.out.println(topicMessage.getPayload());
         }
     });


     mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
     return true;
 }
 public void sendMessage(String message){
     mStompClient.send("/chat/"+courseCode+"/send_message", message);
 }

 public void disconnect(){
        mStompClient.disconnect();
 }
 abstract void onMessage(StompMessage topicMessage);




}

