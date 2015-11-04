//Currently this gets all user information from the db.
//using to test parsing JSON from server.


package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
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

//import android.support.v7.app.AppCompatActivity;

public class SearchDealsActivity extends Activity {

    EditText txtName;
    EditText productText;
    Button searchButton;
    private TextView tvResponse;
    SharedPreferences pref;
    String token, TAG = "SearchDealsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        txtName = (EditText) findViewById(R.id.txtName);
        productText = (EditText) findViewById(R.id.product);
        searchButton = (Button) findViewById(R.id.search_button);
        tvResponse = (TextView) findViewById(R.id.database_response);

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String inputText = txtName.getText().toString();
                //TODO tokenize text

                String URL = "http://52.91.100.201:8080/deal?name=" + inputText;

                new JSONTask().execute(URL);

            }
        });//end setOnclickListener

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
                            obj.getString("storeName") + " " + obj.get("expirationDate") + " " +
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
