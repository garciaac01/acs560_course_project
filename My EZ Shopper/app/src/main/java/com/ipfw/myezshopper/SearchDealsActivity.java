//Currently this gets all user information from the db.
//using to test parsing JSON from server.


package com.ipfw.myezshopper;

import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

//import android.support.v7.app.AppCompatActivity;

public class SearchDealsActivity extends Fragment {

    EditText txtName;
    Spinner categorySpinner;
    Button searchButton, searchAllButton;
    private TextView tvResponse, productText;
    SharedPreferences pref;
    String token, TAG = "SearchDealsActivity", queryText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_search2, container, false);
       // setContentView(R.layout.activity_search2);

        txtName = (EditText) v.findViewById(R.id.txtName);
        categorySpinner = (Spinner) v.findViewById(R.id.category_spinner);
        productText = (TextView) v.findViewById(R.id.product);
        searchButton = (Button) v.findViewById(R.id.search_button);
        searchAllButton = (Button) v.findViewById(R.id.search_all_button);
        tvResponse = (TextView) v.findViewById(R.id.database_response);

        List<String> categories = new ArrayList<String>();
        categories.add("Product Name");
        categories.add("Store Name");
        categories.add("Location");
        categories.add("Category");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queryText = parent.getItemAtPosition(position).toString();

                if(queryText.equals("Product Name")){
                    txtName.setHint("Enter Product Name");
                    queryText = "name";
                }
                else if(queryText.equals("Store Name")){
                    txtName.setHint("Enter Store Name");
                    queryText = "storeName";
                }
                else if(queryText.equals("Location")){
                    txtName.setHint("Enter Location");
                    queryText = "location";
                }
                else if(queryText.equals("Category")){
                    txtName.setHint("Enter Category");
                    queryText = "category";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String concatenatedText = "";
                String inputText = txtName.getText().toString();
                //queryText = txtQuery.getText().toString();

                if (inputText.equals("")){
                    Toast.makeText(getActivity(), "Search field cannot be blank", Toast.LENGTH_LONG).show();
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

                //count the products that are returned
                int hitsCounter = 0;

                if (parentArray.length() == 0){
                    builtString = "No matches";
                }
                else{
                    for (int i = 0; i < parentArray.length(); i++)
                    {
                        if(hitsCounter == 0)
                        {
                            builtString += "<h4><b><u>User Submitted Deals</u>:</b></h4>";
                        }

                        hitsCounter++;

                        JSONObject obj = parentArray.getJSONObject(i);

                        builtString += "<b>" + hitsCounter + ". " + obj.getString("name") + "<br>Price:</b> $" + obj.getDouble("price")
                                + "<br><b>Store:</b> " + obj.getString("storeName") + "<br><b>Location:</b> " + obj.getString("location")
                                + "<br><b>Details:</b> " + obj.get("description") + "<br><b>Category:</b> " + obj.get("category")
                                + "<br><b>Expiration Date:</b> " + obj.get("expirationDate") + "<br><br>";
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
                productText.setText(Html.fromHtml(result.toString()));
            }
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

