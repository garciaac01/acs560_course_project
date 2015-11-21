package com.ipfw.myezshopper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
    PreferencesManager prefManager;
    DBHelper helper = new DBHelper(this);

    EditText email,password,res_email,code,newpass;
    Button login,register;
    String emailtxt,passwordtxt;
    boolean allowLogin;
    StringBuilder memberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new PreferencesManager(this);


        if (prefManager.getLoggedIn()){

            Intent i = ProfileActivity.newIntent(LoginActivity.this);
            startActivity(i);
        }

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.loginbtn);
        register = (Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regactivity = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(regactivity);
                finish();
            }
        });//end register setOnClickListener


        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                allowLogin = false;
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                if (emailtxt.equals("")){
                    Toast.makeText(getApplication(), "Email cannot be blank", Toast.LENGTH_SHORT).show();
                }else if (passwordtxt.equals("")){
                    Toast.makeText(getApplication(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
                }else{
                    String URL = "http://52.91.100.201:8080/user";
                    new JSONTask().execute(URL);
                }

            }//end event handler
        });//end login setOnClickListener



    }//end onCreate

    public class JSONTask extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader br = null;

            try {
                URL object = new URL(params[0]);
                connection = (HttpURLConnection) object.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("POST");

                JSONObject loginInformation = new JSONObject();

                loginInformation.put("email", emailtxt);
                loginInformation.put("password", passwordtxt);
                loginInformation.put("type", "login");

                OutputStream os = connection.getOutputStream();
                os.write(loginInformation.toString().getBytes("UTF-8"));
                os.flush();

                memberID = new StringBuilder();
                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    allowLogin = true;
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        memberID.append(line + "\n");
                    }

                    br.close();

                    return "Successfully logged in!";
                } else if (HttpResult ==HttpURLConnection.HTTP_FORBIDDEN){
                   return "Incorrect password";
                } else if (HttpResult == HttpURLConnection.HTTP_NOT_FOUND){
                    return "Incorrect email";
                }

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
                return "No network connection";
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (allowLogin){

                prefManager.setLoggedin(true);
                prefManager.setId(memberID.toString());
                prefManager.setEmail(emailtxt);

                Intent i = ProfileActivity.newIntent(LoginActivity.this);
                startActivity(i);
            }else{
                Toast.makeText(getApplication(), result, Toast.LENGTH_SHORT).show();
            }
        }

    }//end JSONTask class

}//end LoginActivity class