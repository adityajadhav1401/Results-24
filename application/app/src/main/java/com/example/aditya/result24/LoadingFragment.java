package com.example.aditya.result24;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class LoadingFragment extends Fragment implements OnClickListener {
    private static View view;
    private static Activity activity;
    public static ProgressBar progress;

    public LoadingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.loading_fragment, container, false);
        activity = getActivity();
        progress = view.findViewById(R.id.pbLoading);
        progress.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    public static class LoginHttpPost extends AsyncTask<Void, Void, Void> {

        String url;
        String[] params;
        String data = "";

        LoginHttpPost(String url, String[] params) {
            this.url = url;
            this.params = params;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                String urlParameters = "";

                urlParameters = "type="+params[0]+"&email="+params[1]+"&password="+params[2];
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();
                data = response.toString();

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            try {
                JSONObject jObject = new JSONObject(data);

                if (jObject.getBoolean("status")) {
                    new CustomToast().Show_Toast(activity, view, jObject.getString("message"));
                    Intent intent = new Intent(activity, HomeActivity.class);
                    Bundle b = new Bundle();
                    JSONArray jsonArray = jObject.getJSONArray("data");
                    JSONObject fields = jsonArray.getJSONObject(0).getJSONObject("fields");
                    b.putString("email", jsonArray.getJSONObject(0).getString("pk"));
                    b.putString("name", fields.getString("name"));
                    b.putString("mobile", fields.getString("mobile"));
                    b.putString("qualification",fields.getString("qualification"));
                    intent.putExtras(b);
                    progress.setVisibility(View.INVISIBLE);
                    activity.startActivity(intent);
                    activity.finish();

                } else {
                    new CustomToast().Show_Toast(activity, view, jObject.getString("message"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new CustomToast().Show_Toast(activity, view, "Error Occured");
            }
        }
    }
}