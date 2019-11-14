package com.example.aditya.result24;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class SignUpFragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, mobileNumber, qualification, username,
            password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    private static Activity activity;
    public SignUpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_fragment, container, false);
        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        activity = getActivity();
        fullName = (EditText) view.findViewById(R.id.fullName);
        emailId = (EditText) view.findViewById(R.id.userEmailId);
        mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
        qualification = (EditText) view.findViewById(R.id.qualification);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:

                checkValidation();
                break;

            case R.id.already_user:

                new LoginActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {

        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getUsername = username.getText().toString();
        String getQualification = qualification.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getUsername.equals("") || getUsername.length() == 0
                || getQualification.equals("") || getQualification.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)

            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");

            // Check if email id valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,"Your Email Id is Invalid.");

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            new CustomToast().Show_Toast(getActivity(), view,"Both password doesn't match.");

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            new CustomToast().Show_Toast(getActivity(), view,"Please select Terms and Conditions.");

            // Else do signup or do your stuff
        else {
            String url = getResources().getString(R.string.login);
            String[] params = {"registration",getEmailId,getPassword,getFullName,getMobileNumber,getQualification};
            new SignUpHttpPost(url, params).execute();
        }


    }

    public static class SignUpHttpPost extends AsyncTask<Void, Void, Void> {

        String url;
        String[] params;
        String data = "";

        SignUpHttpPost(String url, String[] params) {
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

                if (params[0].equals("login"))
                    urlParameters = "type="+params[0]+"&email="+params[1]+"&password="+params[2];

                else if (params[0].equals("registration"))
                    urlParameters = "type="+params[0]+"&email="+params[1]+"&password="+params[2]+"&name="+params[3]+"&mobile="+params[4]+"&qualification="+params[5];

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                data = response.toString();

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            try{
                JSONObject jObject = new JSONObject(data);

                if (jObject.getBoolean("status")) {
//                    Toast.makeText(getActivity(), jObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                    activity.startActivity(intent);

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
