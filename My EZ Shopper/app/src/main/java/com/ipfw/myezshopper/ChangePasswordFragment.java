package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by garci_000 on 10/31/2015.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener{
    private EditText txtOldPassword;
    private EditText txtNewPassword;
    private EditText txtConfirmPassword;
    private Button btnChangePassword;
    private Button btnCancel;

    private String member_id;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState){


        View v = inflater.inflate(R.layout.chgpassword_frag, container, false);
        member_id = getActivity().getIntent().getStringExtra(ProfileActivity.EXTRA_MEMBER_ID);
        txtOldPassword = (EditText) v.findViewById(R.id.oldpass);
        txtNewPassword = (EditText) v.findViewById(R.id.newpass);
        txtConfirmPassword = (EditText) v.findViewById(R.id.confirmpass);
        btnChangePassword = (Button) v.findViewById(R.id.chgbtn);
        btnCancel= (Button) v.findViewById(R.id.cancelbtn);

        btnChangePassword.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v){
        String oldPassword = txtOldPassword.getText().toString();
        String newPassword = txtNewPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if (newPassword.equals("")){
            Toast.makeText(getActivity(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else if (!newPassword.equals(confirmPassword)){
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else{
            //todo Update user's password locally as well
            String URL = "http://52.91.100.201:8080/user/" + member_id;

            new JSONTaskUpdate().execute(URL, newPassword);
        }
    }

    public class JSONTaskUpdate extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(params[0]);
                String newPassword = params[1];
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("PUT");

                JSONObject userInformation = new JSONObject();

                userInformation.put("password", newPassword);

                OutputStream os = connection.getOutputStream();
                os.write(userInformation.toString().getBytes("UTF-8"));
                os.flush();

                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK){
                    return "Password updated";
                }
                else
                {
                    return "Error updating password";
                }

            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
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
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }else{
                //display to textview
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
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
