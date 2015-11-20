package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
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

public class ProfileActivity extends FragmentActivity {

    SharedPreferences sharedPref;
    public static final String EXTRA_USER_EMAIL = "com.ipfw.myezshopper.user_email";
    public static final String EXTRA_MEMBER_ID = "com.ipfw.myezshopper.member_id";
    private String user_email, member_id;

    Button btnLogout;
    //todo remove shoppingListLength

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        btnLogout = (Button) findViewById(R.id.go_to_logout_fragment);

        user_email = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        member_id = getIntent().getStringExtra(EXTRA_MEMBER_ID);
        Log.i("Profile Activity", user_email);
        Log.i("Profile Activity", "Member ID is: " + member_id);

//todo remove the database access
//        //get user's list from database
//        String URL = "http://52.91.100.201:8080/user/" + member_id;
//
//        new JSONTask().execute(URL);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null) {
            fragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    public void onButtonClick(View v){

        if (v.getId() == R.id.go_to_logout_fragment){
            sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor  editor = sharedPref.edit();
            editor.putBoolean("loggedin", false);
            editor.apply();

            Toast msg = Toast.makeText(this, "You have logged out", Toast.LENGTH_SHORT);
            msg.show();
        }
        
        if (v.getId() == R.id.go_to_delete_fragment){

            String URL = "http://52.91.100.201:8080/user/" + member_id;

            new JSONTaskDelete().execute(URL);

        }
    }


    public static Intent newIntent(Context packageContext, String email, String memberID)
    {
        Intent i = new Intent(packageContext, ProfileActivity.class);
        i.putExtra(EXTRA_USER_EMAIL, email);
        i.putExtra(EXTRA_MEMBER_ID, memberID);
        return i;
    }

//    public class JSONTask extends AsyncTask<String,String, String> {
//        @Override
//        protected String doInBackground(String... params) {
//
//            HttpURLConnection connection = null;
//            BufferedReader reader = null;
//            try{
//                URL url = new URL(params[0]);
//                connection = (HttpURLConnection)url.openConnection();
//
//                InputStream stream = connection.getInputStream();
//
//                reader = new BufferedReader(new InputStreamReader(stream));
//                StringBuffer buffer = new StringBuffer();
//
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//
//                String finalJSON = buffer.toString();
//                JSONObject jsonResponse = new JSONObject(new String(finalJSON));
//                JSONArray items = jsonResponse.getJSONArray("list");
//                String builtString = "";
//
//                shoppingListLength = items.length();
//
//                if (items.length() == 0){
//                    builtString = "No list items";
//                }
//                else{
//                    for (int i = 0; i < items.length(); i++){
//                        builtString += items.getString(i) + ",";
//                    }
//                }
//                return builtString;
//            }catch(MalformedURLException ex){
//                ex.printStackTrace();
//            }catch(IOException ex){
//                ex.printStackTrace();
//                return "No network connection";
//            } catch (JSONException e) {
//                e.printStackTrace();
//                System.out.println(e.getMessage());
//            }finally{
//                if (connection != null) {
//                    connection.disconnect();
//                }
//                try {
//                    if (reader != null){
//                        reader.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            if (result.equals("No network connection")){
//                Toast.makeText(getApplication(), result, Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(getApplication(), result, Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

    public class JSONTaskDelete extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("DELETE");

                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK){
                    return "User deleted";
                }
                else
                {
                    return "Error deleting user";
                }

            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
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

            if (result == "User deleted"){
                sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor  editor = sharedPref.edit();
                editor.putBoolean("loggedin", false);
                editor.apply();
            }

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

        }

    }//end JSONTaskGet class

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
    }



}


