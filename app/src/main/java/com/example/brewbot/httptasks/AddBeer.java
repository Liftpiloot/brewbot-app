package com.example.brewbot.httptasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.example.brewbot.User;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class AddBeer extends AsyncTask<Void, Void, String> {
    private final String type;
    private final Context context;
    public AddBeer(String type, Context context){
        this.type = type;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        JSONObject body = new JSONObject();
        try {
            body.put("type", type);
            body.put("location", "null");
        } catch (JSONException e) {
            showError(e.getMessage());
        }
        System.out.println(body);
        client.prepare("POST", "https://brewbot.nl/api/user/beer")
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .setHeader("Authorization", "Bearer " + User.getInstance().getToken())
                .setBody(body.toString())
                .execute()
                .toCompletableFuture()
                .join();
        return null;
    }
    protected void showError(String message)
    {
        Handler handler =  new Handler(context.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}
