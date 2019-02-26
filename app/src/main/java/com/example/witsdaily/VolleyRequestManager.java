package com.example.witsdaily;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class VolleyRequestManager {
    private static VolleyRequestManager volleyRequestManager;
    private static Context context;
    private RequestQueue requestQueue;

    private VolleyRequestManager(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext(), new HurlStack(null));
        }
        return requestQueue;
    }

    public static synchronized VolleyRequestManager getManagerInstance(Context context){
        if(volleyRequestManager == null){
            volleyRequestManager = new VolleyRequestManager(context);
        }
        return volleyRequestManager;
    }

    public<T> void addRequestToQueue(Request<T> request){
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }
}