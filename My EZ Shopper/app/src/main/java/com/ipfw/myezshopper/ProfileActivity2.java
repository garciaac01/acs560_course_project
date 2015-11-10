package com.ipfw.myezshopper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity2 extends FragmentActivity {
    public static final String EXTRA_USER_EMAIL = "com.ipfw.myezshopper.user_email";
    private String user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        user_email = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        Log.i("Profile Activity", user_email);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        //get user's list from database
        //todo modify id to use memberID from login page
        String URL = "http://52.91.100.201:8080/user/56416f330e1e94f3c79bf225";

        new JSONTask().execute(URL);

    }

    public static Intent newIntent(Context packageContext, String email)
    {
        Intent i = new Intent(packageContext, ProfileActivity2.class);
        i.putExtra(EXTRA_USER_EMAIL, email);
        return i;
    }

    public class JSONTask extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();
                JSONObject jsonResponse = new JSONObject(new String(finalJSON));
                JSONArray items = jsonResponse.getJSONArray("list");
                String builtString = "";

                if (items.length() == 0){
                    builtString = "No list items";
                }
                else{
                    for (int i = 0; i < items.length(); i++){
                        builtString += items.getString(i) + ",";
                    }
                }
                return builtString;
            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }finally{
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null){
                        reader.close();
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

            if (result.equals("No network connection")){
                Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

}


