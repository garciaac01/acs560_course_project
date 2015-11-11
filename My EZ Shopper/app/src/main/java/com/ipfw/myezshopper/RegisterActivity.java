package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class RegisterActivity extends Activity {
    EditText email,password;
    Button login,register;
    String emailtxt,passwordtxt;
    boolean isLogin = false;
    boolean allowLogin;
    StringBuilder memberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        register = (Button)findViewById(R.id.registerbtn);
        login = (Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowLogin = false;
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                if (emailtxt.equals("")) {
                    Toast.makeText(getApplication(), "User name cannot be blank", Toast.LENGTH_LONG).show();
                } else if (passwordtxt.equals("")) {
                    Toast.makeText(getApplication(), "Password cannot be blank", Toast.LENGTH_LONG).show();
                } else {
                    String URL = "http://52.91.100.201:8080/user";
                    isLogin = true;
                    new JSONTask().execute(URL);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                isLogin = false;
                allowLogin = false;
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                if (emailtxt.equals("")) {
                    Toast.makeText(getApplication(), "User name cannot be blank", Toast.LENGTH_LONG).show();
                } else if (passwordtxt.equals("")) {
                    Toast.makeText(getApplication(), "Password cannot be blank", Toast.LENGTH_LONG).show();
                } else {
                    String URL = "http://52.91.100.201:8080/user";
                    new JSONTask().execute(URL);
                }

            }//end onClick

        });//end setOnClickListener

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


                JSONObject registerInformation = new JSONObject();

                registerInformation.put("name", emailtxt);
                registerInformation.put("password", passwordtxt);

                if (isLogin){
                    registerInformation.put("type", "login");
                }
                else{
                    registerInformation.put("type", "register");
                }

                OutputStream os = connection.getOutputStream();
                os.write(registerInformation.toString().getBytes("UTF-8"));
                os.flush();

                memberID = new StringBuilder();
                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    if (isLogin){
                        allowLogin = true;
                    }

                    br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        memberID.append(line + "\n");
                    }

                    br.close();

                    System.out.println("" + memberID.toString());
                    if (!isLogin){
                        return "Successfully added";
                    }
                } else if (HttpResult ==HttpURLConnection.HTTP_FORBIDDEN){
                    String msg = "";
                    if (isLogin){
                       msg = "Incorrect password";
                    }
                    else
                    {
                        msg = "User name already exists";
                    }

                    return msg;
                } else if (HttpResult ==HttpURLConnection.HTTP_NOT_FOUND) {
                    return "Incorrect user name";
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

            if (isLogin && allowLogin) {
                //Intent profactivity = new Intent(RegisterActivity.this, ProfileActivity2.class);
                //startActivity(profactivity);

                //Need to get the memberID to pass as the third parameter
                Intent i = ProfileActivity2.newIntent(RegisterActivity.this, emailtxt, null);
                startActivity(i);
            }
            else{
                Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            }
        }

    }//end JSONTask class

}//end RegisterActivity class