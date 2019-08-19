package com.example.witsdaily;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;


public abstract class SocketAccessor {

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    String personNumber, userToken, courseCode,socketType; // course code is now with type,, ie COMS1234:Tutor
    String sendStream = "sendMessage"; // default
    String TAG = "Websocket connection";
    private Disposable mRestPingDisposable;
    public SocketAccessor(String pPersonNumber, String pUserToken, String pCourseCode,String pType) {
        personNumber = pPersonNumber;
        userToken = pUserToken;
        courseCode = pCourseCode;
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://wd.dimensionalapps.com/chatsocket/websocket");
        socketType = pType;
        resetSubscriptions();
    }



    public boolean establishConnection() {

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("personNumber", personNumber));
        headers.add(new StompHeader("userToken", userToken));
        //mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
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

        //receiving messages
        String courseCodeWithc = courseCode.replace(":","\\c");
        Disposable dispTopic = mStompClient.topic("/topic/"+courseCodeWithc,headers)
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

    public void setMessageType(String messageType){
        socketType = messageType;
    }
    public void setStreamType(boolean pStreamType){
        if (pStreamType){
            sendStream = "sendMessage";
        }
        else{
            sendStream = "deleteMessage";
        }
    }
    private void sendEchoViaStomp(String message) {

        JSONObject messageObject = new JSONObject();
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("personNumber", personNumber));
        headers.add(new StompHeader("userToken", userToken));
        headers.add(new StompHeader(StompHeader.DESTINATION,"/chat/"+courseCode+"/"+sendStream));


        try {
            messageObject.put("content",message);
            messageObject.put("messageType",socketType);
            messageObject.put("userToken",userToken);
            messageObject.put("personNumber",personNumber);
            StompMessage stompMessage = new StompMessage(StompCommand.SEND,headers,messageObject.toString());
            compositeDisposable.add(mStompClient.send(stompMessage)
                    .compose(applySchedulers())
                    .subscribe(() -> Log.d(TAG, "STOMP echo send successfully"), throwable -> {
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

    public void onDestroy() {
        mStompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }
    abstract void onMessage(StompMessage topicMessage);
}