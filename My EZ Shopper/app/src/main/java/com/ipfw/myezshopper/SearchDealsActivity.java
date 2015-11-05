//Currently this gets all user information from the db.
//using to test parsing JSON from server.


package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.StringTokenizer;

//import android.support.v7.app.AppCompatActivity;

public class SearchDealsActivity extends Fragment {

    EditText txtName, txtQuery;
    EditText productText;
    Button searchButton, searchAllButton;
    private TextView tvResponse;
    SharedPreferences pref;
    String token, TAG = "SearchDealsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_search2, container, false);
       // setContentView(R.layout.activity_search2);

        txtName = (EditText) v.findViewById(R.id.txtName);
        txtQuery = (EditText) v.findViewById(R.id.txtQuery);
        productText = (EditText) v.findViewById(R.id.product);
        searchButton = (Button) v.findViewById(R.id.search_button);
        searchAllButton = (Button) v.findViewById(R.id.search_all_button);
        tvResponse = (TextView) v.findViewById(R.id.database_response);

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String concatenatedText = "";
                String inputText = txtName.getText().toString();
                String queryText = txtQuery.getText().toString();

                if (inputText == null || inputText.equals("") || queryText == null || queryText.equals("")){
                    //make toast
                }
                else{
                    StringTokenizer st = new StringTokenizer(inputText, " ");

                    while (st.hasMoreTokens()){
                        concatenatedText += st.nextToken() + "+";
                    }

                    //remove last + symbol
                    concatenatedText = concatenatedText.substring(0, concatenatedText.length() - 1);

                    String URL = "http://52.91.100.201:8080/deal?" + queryText + "=" + concatenatedText;

                    new JSONTask().execute(URL);
                }
            }
        });//end setOnclickListener

        searchAllButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String URL = "http://52.91.100.201:8080/deal";

                new JSONTask().execute(URL);
            }
        });//end setOnclickListener

        return v;
    }//end onCreate



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
                JSONArray parentArray = new JSONArray(finalJSON);

                String builtString = "";

                for (int i = 0; i < parentArray.length(); i++)
                {
                    JSONObject obj = parentArray.getJSONObject(i);
                    builtString += obj.getString("name") + " " + obj.getDouble("price") + " " +
                            obj.getString("storeName") + " " + obj.getString("location") + " " +
                            obj.get("description") + " " + obj.get("category") + "\n";
                }
                return builtString;


            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
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
            productText.setText(result.toString());
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return true;
//    }//end onCreateOptionsMenu
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }//end onOptionsItemSelected

}//end SearchActivity class.

