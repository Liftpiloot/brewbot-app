package com.example.brewbot.httptasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.example.brewbot.User;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterUser extends AsyncTask<Void, Void, String> {
    private String email;
    private String username;
    private String password;
    private int age;
    private String gender;
    private Context context;

    public RegisterUser(String email, String username, String password, int age, String gender, Context context){
        this.email = email;
        this.username = username;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.context = context;
        System.out.println("REGISTER USER CLASS MADE");
    }

    @Override
    protected String doInBackground(Void... voids) {
        System.out.println("REGISTER USER: " + username);
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
            body.put("password_confirm", password);
            body.put("email", email);
            body.put("age", age);
            body.put("gender", gender);
        } catch (JSONException e) {
            showError(e.getMessage());
            return null;
        }

        Response response = client.prepare("POST", "https://brewbot.nl/api/user/register")
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .setBody(body.toString())
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
            System.out.println("Logging in");
            User.getInstance().setToken(token);
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
