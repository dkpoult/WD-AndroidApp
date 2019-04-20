package com.example.witsdaily;

import android.content.Context;
import android.icu.text.SymbolTable;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;


public abstract class ChatAccessor {
    WebSocket ws = null;
    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    String personNumber, userToken, courseCode;

    String TAG = "Websocket connection";
    private Disposable mRestPingDisposable;
    public ChatAccessor(String pPersonNumber, String pUserToken, String pCourseCode) {
        personNumber = pPersonNumber;
        userToken = pUserToken;
        courseCode = pCourseCode;
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://wd.dimensionalapps.com/chatsocket/websocket");

        resetSubscriptions();
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
        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            System.out.println("Stomp connection opened");
                            break;
                        case ERROR:

                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            System.out.println("Stomp connection error");
                            break;
                        case CLOSED:
                            System.out.println("Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            System.out.println("Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);
        headers.add(new StompHeader("personNumber", personNumber));
        headers.add(new StompHeader("userToken", userToken));
        //receiving messages
        Disposable dispTopic = mStompClient.topic("/topic/"+courseCode,headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    onMessage(topicMessage);
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });


        compositeDisposable.add(dispTopic);

        mStompClient.connect(headers);

        return true;
    }


    private void sendEchoViaStomp(String message) {

        JSONObject messageObject = new JSONObject();
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("personNumber", personNumber));
        headers.add(new StompHeader("userToken", userToken));
        headers.add(new StompHeader(StompHeader.DESTINATION,"/chat/"+courseCode+"/sendMessage"));


        try {
            messageObject.put("content",message);
            messageObject.put("messageType","CHAT");
            StompMessage stompMessage = new StompMessage(StompCommand.SEND,headers,messageObject.toString());
            compositeDisposable.add(mStompClient.send(stompMessage)
                    .compose(applySchedulers())
                    .subscribe(() -> {
                        Log.d(TAG, "STOMP echo send successfully");
                    }, throwable -> {
                        Log.e(TAG, "Error send STOMP echo", throwable);
                    }));
            //mStompClient.send(stompMessage).subscribe();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public void sendMessage(String message){
        sendEchoViaStomp(message);
    }
    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    protected void onDestroy() {
        mStompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }
    abstract void onMessage(StompMessage topicMessage);
}