package com.example.brewbot.httptasks;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.example.brewbot.User;
import com.example.brewbot.homescreenActivity;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginTask extends AsyncTask<Void, Void, String> {
    private final String emailText;
    private final String passwordText;
    private final Context context;

    public LoginTask(String emailText, String passwordText, Context context) {
        this.emailText = emailText;
        this.passwordText = passwordText;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("email", emailText);
            requestObject.put("password", passwordText);
        } catch (JSONException e) {
            showError(e.getMessage());
            return null;
        }

        AsyncHttpClient client = new DefaultAsyncHttpClient();
        Response response;
        response = client.prepare("POST", "https://brewbot.nl/api/user/login")
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .setBody(requestObject.toString())
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

        try {
            JSONObject jsonObject = new JSONObject(response.getResponseBody());
            if (response.getStatusCode() == 200) {
                return jsonObject.getString("token");
            }
            String errorMsg = jsonObject.getString("message");
            showError(errorMsg);
            return null;
        } catch (JSONException e) {
            showError(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String token) {
        if (token != null) {
            User.getInstance().setToken(token);
            GetPastSevenDays getStats = new GetPastSevenDays(context);
            getStats.execute();
            GetUserInfo getUserInfo = new GetUserInfo(this.context);
            getUserInfo.execute();
        }
    }

    protected void showError(String message)
    {
        Handler handler =  new Handler(context.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

}

