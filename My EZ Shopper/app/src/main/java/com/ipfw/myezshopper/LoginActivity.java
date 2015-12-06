package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity {
    PreferencesManager prefManager;
    NetworkHelper netHelper;

    EditText email,password;
    Button login,register;
    String emailtxt,passwordtxt;
    boolean allowLogin;
    String memberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new PreferencesManager(this);
        netHelper = new NetworkHelper(this);


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

                if (netHelper.isConnected()){
                    Intent regactivity = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(regactivity);
                    finish();
                }else{
                    Toast.makeText(getApplication(), "No network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });//end register setOnClickListener


        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                allowLogin = false;
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                if (emailtxt.equals("")) {
                    Toast.makeText(getApplication(), "Email cannot be blank", Toast.LENGTH_SHORT).show();
                }else if (passwordtxt.equals("")) {
                    Toast.makeText(getApplication(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
                }else if (netHelper.isConnected()) {
                    String URL = "http://52.91.100.201:8080/api/user/login";
                    new JSONTask().execute(URL);
                }else if (prefManager.getEmail()!=""){
                    String pass = prefManager.getPassword();
                    String email = prefManager.getEmail();
                    if (emailtxt.equals(email) && passwordtxt.equals(pass)){
                        prefManager.setLoggedin(true);
                        Intent i = ProfileActivity.newIntent(LoginActivity.this);
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(getApplication(), "Login information incorrect", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplication(), "No network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class JSONTask extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader br = null;
            StringBuffer buffer = new StringBuffer();

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
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

                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    allowLogin = true;

                    br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJSON = buffer.toString();
                    JSONArray arr = new JSONArray(finalJSON);
                    JSONObject obj = arr.getJSONObject(0);
                    prefManager.setName(obj.getString("name"));
                    prefManager.setPassword(obj.getString("password"));
                    prefManager.setEmail(obj.getString("email"));
                    prefManager.setId(obj.getString("_id"));
                    memberID = obj.getString("_id");
                    //todo store list items locally

                    return "Successfully logged in!";
                }else if (HttpResult == HttpURLConnection.HTTP_BAD_REQUEST){
                    return "Login information incorrect";
                }

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
                return "The EZ Shopper Server is down";
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

                Intent i = ProfileActivity.newIntent(LoginActivity.this);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getApplication(), result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}