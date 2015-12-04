package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RegisterActivity extends Activity {

    DBHelper helper = new DBHelper(this);
    User newUser;
    PreferencesManager prefManager;
    private final String REGISTER_ENABLED = "com.ipfw.myezshopper.register_enabled";
    private final String EMAIL_ENABLED = "com.ipfw.myezshopper.email_enabled";
    private final String NAME_ENABLED = "com.ipfw.myezshopper.name_enabled";
    private final String LOGIN_ENABLED = "com.ipfw.myezshopper.login_enabled";
    private final String PASSWORD_ENABLED = "com.ipfw.myezshopper.password_enabled";
    private final String NAME = "com.ipfw.myezshopper.name";
    private final String PASSWORD = "com.ipfw.myezshopper.password";
    private final String EMAIL = "com.ipfw.myezshopper.email";
    EditText email,password, name;
    Button login,register;
    String emailtxt,passwordtxt, nametxt;
    boolean isLogin = false;
    boolean allowLogin;
    StringBuilder memberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefManager = new PreferencesManager(this);

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        register = (Button)findViewById(R.id.registerbtn);
        login = (Button)findViewById(R.id.login);
        login.setEnabled(false);
        login.setBackgroundColor(Color.GRAY);


        if(savedInstanceState != null)
        {
            name.setEnabled(savedInstanceState.getBoolean(NAME_ENABLED));
            email.setEnabled(savedInstanceState.getBoolean(EMAIL_ENABLED));
            password.setEnabled(savedInstanceState.getBoolean(PASSWORD_ENABLED));
            register.setEnabled(savedInstanceState.getBoolean(REGISTER_ENABLED));
            login.setEnabled(savedInstanceState.getBoolean(LOGIN_ENABLED));
            name.setText(savedInstanceState.getString(NAME));
            email.setText(savedInstanceState.getString(EMAIL));
            password.setText(savedInstanceState.getString(PASSWORD));

            if(!register.isEnabled())
            {
                register.setBackgroundColor(Color.GRAY);
            }

            if(login.isEnabled())
            {
                login.setBackgroundColor(Color.parseColor("#99D9EA"));
            }
        }


    }//end onCreate

    public void onButtonClick(View v){

        if (v.getId() == R.id.login){
            allowLogin = false;
            emailtxt = email.getText().toString();
            passwordtxt = password.getText().toString();

            if (emailtxt.equals("")) {
                Toast.makeText(getApplication(), "Email cannot be blank", Toast.LENGTH_SHORT).show();
            } else if (passwordtxt.equals("")) {
                Toast.makeText(getApplication(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
            }else {
                String URL = "http://52.91.100.201:8080/api/user/login";
                isLogin = true;
                new JSONTask().execute(URL);
            }
        }

        if (v.getId() == R.id.registerbtn){
            isLogin = false;
            allowLogin = false;
            emailtxt = email.getText().toString();
            passwordtxt = password.getText().toString();
            nametxt = name.getText().toString();

            if (emailtxt.equals("")) {
                Toast.makeText(getApplication(), "User name cannot be blank", Toast.LENGTH_SHORT).show();
            }else if (passwordtxt.equals("")) {
                Toast.makeText(getApplication(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
            }else if (nametxt.equals("")) {
                Toast.makeText(getApplication(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
            }else {
                String URL = "http://52.91.100.201:8080/api/user";
                new JSONTask().execute(URL);
            }
        }

    }

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

                registerInformation.put("email", emailtxt);
                registerInformation.put("password", passwordtxt);
                registerInformation.put("name", nametxt);

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
                    msg = "Email already exists";
                    return msg;
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

                prefManager.setLoggedin(true);
                //todo return user to Login screen
                Intent i = ProfileActivity.newIntent(RegisterActivity.this);
                startActivity(i);

                finish();
            }else if (result == "Successfully added"){
                newUser = new User();
                newUser.setEmail(emailtxt);
                newUser.setName(nametxt);
                newUser.setPassword(passwordtxt);
                newUser.setId(memberID.toString());

                helper.insertUser(newUser);
                prefManager.setAllPreferences(newUser);
                register.setEnabled(false);
                register.setBackgroundColor(Color.GRAY);
                login.setEnabled(true);
                login.setBackgroundColor(Color.parseColor("#99D9EA"));
                name.setEnabled(false);
                password.setEnabled(false);
                email.setEnabled(false);


                Toast.makeText(getApplication(), result, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplication(), result, Toast.LENGTH_SHORT).show();
            }
        }

    }//end JSONTask class

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putBoolean(REGISTER_ENABLED, register.isEnabled());
        savedInstanceState.putBoolean(LOGIN_ENABLED, login.isEnabled());
        savedInstanceState.putBoolean(NAME_ENABLED, name.isEnabled());
        savedInstanceState.putBoolean(PASSWORD_ENABLED, password.isEnabled());
        savedInstanceState.putBoolean(EMAIL_ENABLED, email.isEnabled());
        savedInstanceState.putString(NAME, name.getText().toString());
        savedInstanceState.putString(EMAIL, email.getText().toString());
        savedInstanceState.putString(PASSWORD, password.getText().toString());
    }

}//end RegisterActivity class