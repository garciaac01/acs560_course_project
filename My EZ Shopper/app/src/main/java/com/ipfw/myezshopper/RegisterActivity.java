package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
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
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.io.*;
import java.util.List;


public class RegisterActivity extends Activity {
    EditText email,password;
    Button login,register;
    String emailtxt,passwordtxt;
    List<NameValuePair> params;

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
                Intent regactivity = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(regactivity);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                try {
                    //TODO modify to work as async task
                    //Added to remove onMainThreadException
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    //Post Data
                    HttpClient httpClient = new DefaultHttpClient();
                    // replace with your url
                    HttpPost httpPost = new HttpPost("http://52.91.100.201:8080/user");

                    JSONObject obj = new JSONObject();
                    obj.put("name", emailtxt);
                    obj.put("password", passwordtxt);

                    try {
                        AbstractHttpEntity entity = null;
                        entity = new ByteArrayEntity(obj.toString().getBytes("UTF8"));
                        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        httpPost.setEntity(entity);

                    } catch (UnsupportedEncodingException e) {
                        // log exception
                        e.printStackTrace();
                    }

                    //making POST request.
                    try {
                        HttpResponse response = httpClient.execute(httpPost);
                        // write response to log
                        Log.d("Http Post Response:", response.toString());
                        int statusCode = response.getStatusLine().getStatusCode();
                        String status = "";
                        if (statusCode == 200){
                            status = "Successfully registered!";
                        }else{
                            status = "Error registering";
                        }
                        //TODO: Remove status code from message
                        Toast.makeText(getApplication(),status + " " + statusCode,Toast.LENGTH_LONG).show();
                    } catch (ClientProtocolException e) {
                        // Log exception
                        e.printStackTrace();
                    } catch (IOException e) {
                        // Log exception
                        e.printStackTrace();
                    }

                } catch (Exception ex){

                }

            }//end onClick

        });//end setOnClickListener

    }//end onCreate

}//end RegisterActivity class