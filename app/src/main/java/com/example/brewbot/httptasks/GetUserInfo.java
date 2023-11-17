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
import java.util.ArrayList;

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

                // TODO Set profile (Dit werkt dus niet om een of andere reden)
                /*
                Model bijgevoegd is om te testen, nieuw beter model is nog in ontwikkeling.
                 */

                double[][] clusterMeans = {
                        {0.25, 0.68, 0.43},
                        {2,0,16.5},
                        {1.5,0.33,5.58},
                        {6.6,0,-0.6}};
                ArrayList<int[]> stats = User.getInstance().getPastWeekStats();
                int pils = 0;
                int special = 0;
                int zero = 0;
                for (int[] day: stats){
                    pils += day[1];
                    special += day[2];
                    zero += day[3];
                }
                double[] vector = {special,zero,pils};
                User.getInstance().setProfile(findClosestCluster(vector, clusterMeans));
                System.out.println(User.getInstance().getProfile());

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

    public static int findClosestCluster(double[] vector, double[][] clusterMeans) {
        int closestCluster = 0;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < clusterMeans.length; i++) {
            double distance = 0.0;
            for (int j = 0; j < vector.length; j++) {
                distance += Math.pow(vector[j] - clusterMeans[i][j], 2);
            }
            distance = Math.sqrt(distance);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestCluster = i;
            }
        }

        return closestCluster;
    }

    protected void showError(String message)
    {
        Handler handler =  new Handler(context.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

}
