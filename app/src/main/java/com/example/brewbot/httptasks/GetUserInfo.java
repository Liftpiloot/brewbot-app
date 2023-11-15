package com.example.brewbot.httptasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;
import android.widget.TextView;

import com.example.brewbot.MainActivity;
import com.example.brewbot.User;
import com.example.brewbot.homescreenActivity;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.pmml4s.model.Model;

import java.io.IOException;

public class GetUserInfo extends AsyncTask {
    private final Context context;

    public GetUserInfo(Context context){
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        Response response;
        String token = User.getInstance().getToken();
        response = client.prepare("GET", "https://brewbot.nl/api/user/me")
                .setHeader("Accept", "application/json")
                .setHeader("Authorization", "Bearer "+ token)
                .execute()
                .toCompletableFuture()
                .join();

        try {
            client.close();
        } catch (IOException e) {
            showError(e.getMessage());
            return null;
        }

        if(response == null){
            showError("Got invalid response from server");
            return null;
        }

        try{
            JSONObject jsonObject = new JSONObject(response.getResponseBody());
            if (response.getStatusCode() == 200){
                JSONObject userObject = jsonObject.getJSONObject("user");
                User.getInstance().setUsername(userObject.getString("username"));
                User.getInstance().setEmail(userObject.getString("email"));
                User.getInstance().setAge(userObject.getInt("age"));
                User.getInstance().setGender(userObject.getString("gender"));

                // TODO Set profile

                // Go to homescreen
                Intent intent = new Intent(this.context, homescreenActivity.class);
                context.startActivity(intent);
                //TODO update for extra information
                return jsonObject;
            }
            String errorMsg = jsonObject.getString("message");
            showError(errorMsg);
            return null;
        } catch (JSONException e) {
            showError(e.getMessage());
            return null;
        }
    }

    protected void showError(String message)
    {
        Handler handler =  new Handler(context.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

}
