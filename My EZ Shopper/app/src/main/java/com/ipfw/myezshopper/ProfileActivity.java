package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends FragmentActivity {

    public static final String EXTRA_USER_EMAIL = "com.ipfw.myezshopper.user_email";
    public static final String EXTRA_MEMBER_ID = "com.ipfw.myezshopper.member_id";
    private String email, member_id;
    private PreferencesManager prefManager;

    Button btnLogout;
    //todo remove shoppingListLength

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefManager = new PreferencesManager(this);

        btnLogout = (Button) findViewById(R.id.go_to_logout_fragment);

        email = prefManager.getEmail();
        member_id = prefManager.getId();


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null) {
            fragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    public void onButtonClick(View v){

        if (v.getId() == R.id.go_to_logout_fragment){

            prefManager.setLoggedin(false);
            Toast msg = Toast.makeText(this, "You have logged out", Toast.LENGTH_SHORT);
            msg.show();
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        
        if (v.getId() == R.id.go_to_delete_fragment){

            String URL = "http://52.91.100.201:8080/user/" + member_id;

            new JSONTaskDelete().execute(URL);
            prefManager.removeAllPreferences();
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }


    public static Intent newIntent(Context packageContext)
    {
        Intent i = new Intent(packageContext, ProfileActivity.class);
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
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        }

    }//end JSONTaskGet class

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
    }



}


