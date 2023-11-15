package com.example.brewbot.httptasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.example.brewbot.User;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class GetPastSevenDays extends AsyncTask<Void, Void, ArrayList<int[]>> {
    Context context;

    public GetPastSevenDays(Context context){
        this.context = context;
    }


    @Override
    protected ArrayList<int[]> doInBackground(Void... voids) {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        Response response = client.prepare("GET", "https://brewbot.nl/api/user/beer/consumptions")
                .setHeader("Accept", "application/json")
                .setHeader("Authorization", "Bearer " + User.getInstance().getToken())
                .execute()
                .toCompletableFuture()
                .join();

        if (response != null && response.getStatusCode() == 200){
            JSONObject jsonObject;
            try {
                ArrayList<int[]> returnArray = new ArrayList<int[]>();
                ArrayList<Integer> beers = new ArrayList<>();
                ArrayList<Integer> days = new ArrayList<>();
                jsonObject = new JSONObject(response.getResponseBody());
                JSONObject beerObject = jsonObject.getJSONObject("consumptions");
                Iterator<String> keys = beerObject.keys();
                while(keys.hasNext())
                {
                    String date = keys.next();
                    JSONObject value = beerObject.optJSONObject(date);
                    assert value != null;

                    String[] daynum = date.split("-");
                    int pils = value.getInt("normal");
                    int zero = value.getInt("zero");
                    int special = value.getInt("special");
                    int total = pils + zero + special;
                    int[] daybeers = {Integer.parseInt(daynum[0]), total};
                    returnArray.add(daybeers);
                    System.out.println(Integer.parseInt(daynum[0]) + total);
                }
                return returnArray;
            } catch (JSONException e) {
                showError(e.getMessage());
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(ArrayList array) {
        if (array != null) {
            System.out.println("adding to user");
            User.getInstance().setPastWeekStats(array);
        } else {
            showError("Failed to add drinking data to user");
        }
    }
    protected void showError(String message)
    {
        Handler handler =  new Handler(context.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}
