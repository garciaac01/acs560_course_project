package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends FragmentActivity {

    private String member_id;
    private PreferencesManager prefManager;

    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefManager = new PreferencesManager(this);

        btnLogout = (Button) findViewById(R.id.go_to_logout_fragment);

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
            showDeleteDialog();
        }
    }

    private void showDeleteDialog(){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("Are you sure you want to delete your MyEZShopper profile?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    String URL = "http://52.91.100.201:8080/api/user/" + member_id;
                    new JSONTaskDelete().execute(URL);
                    prefManager.removeAllPreferences();
                    Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public static Intent newIntent(Context packageContext)
    {
        Intent i = new Intent(packageContext, ProfileActivity.class);
        return i;
    }

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
                else{
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
                try{
                    if (reader != null){
                        reader.close();
                    }
                }catch (IOException e) {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
    }
}