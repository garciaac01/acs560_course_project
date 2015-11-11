package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by garci_000 on 10/31/2015.
 */
public class ListFragment extends Fragment implements View.OnClickListener{
    private EditText productName;
    private Button btnSubmitListItem;
    private TextView txtList;
    String product;
    public static int itemListLength;  //may want to move this to a newIntent method in ProfileFragment
    private ArrayList<String> shoppingList = new ArrayList<String>();


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState){


        View v = inflater.inflate(R.layout.fragment_new_list, container, false);

        productName = (EditText) v.findViewById(R.id.productName);
        btnSubmitListItem = (Button) v.findViewById(R.id.submit_list_button);
        txtList = (TextView) v.findViewById(R.id.list);

        btnSubmitListItem.setOnClickListener(this);


        //Access users shopping list on database.
        //Store items locally
        //Display items in list textView
        //get user's list from database
        //todo change to member id
        String URL = "http://52.91.100.201:8080/user/5642c18331ea22d9cd83cc7a";

        new JSONTaskGet().execute(URL);

        return v;
    }

    @Override
    public void onClick(View v){
        product = productName.getText().toString();

        if (product.equals("")){
            Toast.makeText(getActivity(), "Product name cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else{
            //todo save new product locally
            //add item to local list
            shoppingList.add(product);
            //todo need memberid for API call
            new JSONTaskPost().execute("http://52.91.100.201:8080/user/5642c18331ea22d9cd83cc7a");
        }
    }

    public class JSONTaskPost extends AsyncTask<String,String, String> {
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
                connection.setRequestMethod("PUT");

                JSONObject userInformation = new JSONObject();

                userInformation.put("name", "andy");
                userInformation.put("password", "garcia");
                userInformation.put("loginAttempts", 0);
                userInformation.put("list", new JSONArray(shoppingList));

                OutputStream os = connection.getOutputStream();
                os.write(userInformation.toString().getBytes("UTF-8"));
                os.flush();

                StringBuilder sb = new StringBuilder();
                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();

                    System.out.println("" + sb.toString());
                    return "List successfully added";
                } else {
                    System.out.println(connection.getResponseMessage());
                    return ("Error adding list item");
                }
            }
            catch(MalformedURLException ex){
                ex.printStackTrace();
            }
            catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            }catch(JSONException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }finally{
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (br != null){
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
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
        }

    }//end JSONTaskPost class

    public class JSONTaskGet extends AsyncTask<String,String, String> {
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

                itemListLength = items.length();

                if (items.length() == 0){
                    builtString = "No list items";
                }
                else{
                    for (int i = 0; i < items.length(); i++){
                        builtString += items.getString(i) + ",";
                        shoppingList.add(items.getString(i));
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
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }else{
                //display to textview
                txtList.setText(result);
            }
        }

    }//end JSONTaskGet class

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
    }

    public static int geItemListLength()
    {
        return itemListLength;
    }
}
