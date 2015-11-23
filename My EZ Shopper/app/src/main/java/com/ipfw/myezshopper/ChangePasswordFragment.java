package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener{

    private PreferencesManager prefManager;
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

        prefManager = new PreferencesManager((this.getContext()));

        View v = inflater.inflate(R.layout.chgpassword_frag, container, false);
        member_id = prefManager.getId();
        txtOldPassword = (EditText) v.findViewById(R.id.oldpass);
        txtNewPassword = (EditText) v.findViewById(R.id.newpass);
        txtConfirmPassword = (EditText) v.findViewById(R.id.confirmpass);
        btnChangePassword = (Button) v.findViewById(R.id.chgbtn);
        btnCancel= (Button) v.findViewById(R.id.cancelbtn);

        btnChangePassword.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v){

        if (v.getId() == R.id.chgbtn){
            String currentPassword = txtOldPassword.getText().toString();
            String newPassword = txtNewPassword.getText().toString();
            String confirmPassword = txtConfirmPassword.getText().toString();

            if (currentPassword.equals("")){
                Toast.makeText(getActivity(), "Current password cannot be blank", Toast.LENGTH_SHORT).show();
            }else if (!currentPassword.equals(prefManager.getPassword())){
                Toast.makeText(getActivity(), "Current password is not correct", Toast.LENGTH_SHORT).show();
            }else if (newPassword.equals("")){
                Toast.makeText(getActivity(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
            }else if (!newPassword.equals(confirmPassword)){
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }else{
                prefManager.setPassword(newPassword);
                String URL = "http://52.91.100.201:8080/user/" + member_id;
                new JSONTaskUpdate().execute(URL, newPassword);
            }
        }
        if (v.getId() == R.id.cancelbtn) {
            //todo wire this up!
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
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }

    }//end JSONTaskGet class

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
    }

}
