package com.ipfw.myezshopper;

import android.app.Activity;
import android.app.Dialog;
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
    EditText email,password,res_email,code,newpass;
    Button login,cont,cont_code,cancel,cancel1,register,forpass;
    String emailtxt,passwordtxt,email_res_txt,code_txt,npass_txt;
    List<NameValuePair> params;
    SharedPreferences pref;
    Dialog reset;
    ServerRequest sr;
    boolean allowLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sr = new ServerRequest();

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.loginbtn);
        register = (Button)findViewById(R.id.register);
        forpass = (Button)findViewById(R.id.forgotpass);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

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
                    Toast.makeText(getApplication(), "User name cannot be blank", Toast.LENGTH_LONG).show();
                }
                else if (passwordtxt.equals("")){
                    Toast.makeText(getApplication(), "Password cannot be blank", Toast.LENGTH_LONG).show();
                }else{
                    String URL = "http://52.91.100.201:8080/user";
                    new JSONTask().execute(URL);
                }

            }//end event handler
        });//end login setOnClickListener

        forpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset = new Dialog(LoginActivity.this);
                reset.setTitle("Reset Password");
                reset.setContentView(R.layout.reset_pass_init);
                cont = (Button) reset.findViewById(R.id.resbtn);
                cancel = (Button) reset.findViewById(R.id.cancelbtn);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reset.dismiss();
                    }
                });
                res_email = (EditText) reset.findViewById(R.id.email);

                cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        email_res_txt = res_email.getText().toString();

                        params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("email", email_res_txt));

                        JSONObject json = sr.getJSON("http://52.91.100.201:8080/api/resetpass", params);

                        if (json != null) {
                            try {
                                String jsonstr = json.getString("response");
                                if (json.getBoolean("res")) {
                                    Log.e("JSON", jsonstr);
                                    Toast.makeText(getApplication(), jsonstr, Toast.LENGTH_LONG).show();
                                    reset.setContentView(R.layout.reset_pass_code);
                                    cont_code = (Button) reset.findViewById(R.id.conbtn);
                                    code = (EditText) reset.findViewById(R.id.code);
                                    newpass = (EditText) reset.findViewById(R.id.npass);
                                    cancel1 = (Button) reset.findViewById(R.id.cancel);
                                    cancel1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            reset.dismiss();
                                        }
                                    });
                                    cont_code.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            code_txt = code.getText().toString();
                                            npass_txt = newpass.getText().toString();
                                            Log.e("Code", code_txt);
                                            Log.e("New pass", npass_txt);
                                            params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("email", email_res_txt));
                                            params.add(new BasicNameValuePair("code", code_txt));
                                            params.add(new BasicNameValuePair("newpass", npass_txt));

                                            JSONObject json = sr.getJSON("http://52.91.100.201:8080/api/resetpass/chg", params);

                                            if (json != null) {
                                                try {

                                                    String jsonstr = json.getString("response");
                                                    if (json.getBoolean("res")) {
                                                        reset.dismiss();
                                                        Toast.makeText(getApplication(), jsonstr, Toast.LENGTH_LONG).show();

                                                    } else {
                                                        Toast.makeText(getApplication(), jsonstr, Toast.LENGTH_LONG).show();

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }
                                    });
                                } else {

                                    Toast.makeText(getApplication(), jsonstr, Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });//end forgetPassword event handler
                reset.show();
            }
        });//end forgetPassword setListener

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

                loginInformation.put("name", emailtxt);
                loginInformation.put("password", passwordtxt);
                loginInformation.put("type", "login");

                OutputStream os = connection.getOutputStream();
                os.write(loginInformation.toString().getBytes("UTF-8"));
                os.flush();

                StringBuilder sb = new StringBuilder();
                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    allowLogin = true;
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();

                    System.out.println("" + sb.toString());
                    return "Successfully logged in!";
                } else if (HttpResult ==HttpURLConnection.HTTP_FORBIDDEN){
                   return "Incorrect password";
                } else if (HttpResult == HttpURLConnection.HTTP_NOT_FOUND){
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

            if (allowLogin){
                //Intent profactivity = new Intent(LoginActivity.this,ProfileActivity2.class);
                //startActivity(profactivity);
                Intent i = ProfileActivity2.newIntent(LoginActivity.this, emailtxt);
                startActivity(i);
            }else{
                Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            }
        }

    }//end JSONTask class

}//end LoginActivity class