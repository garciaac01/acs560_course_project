package com.ipfw.myezshopper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by garci_000 on 10/31/2015.
 */
public class DealFragment extends Fragment implements View.OnClickListener{
    private EditText productName;
    private EditText productPrice;
    private EditText storeName;
    private EditText locationName;
    private EditText expirationDate;
    private EditText productDescription;
    private EditText category;
    private Button submitDeal;
    String product, price, store, location, expiration, description, cat;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState){
        View v = inflater.inflate(R.layout.fragment_new_deal, container, false);

        productName = (EditText) v.findViewById(R.id.productName);
        productPrice = (EditText) v.findViewById(R.id.productPrice);
        storeName = (EditText) v.findViewById(R.id.storeName);
        locationName = (EditText) v.findViewById(R.id.locationName);
        expirationDate = (EditText) v.findViewById(R.id.expirationDate);
        productDescription = (EditText) v.findViewById(R.id.productDescription);
        category = (EditText) v.findViewById(R.id.productCategory);
        submitDeal = (Button) v.findViewById(R.id.submit_deal_button);

        submitDeal.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v){
        product = productName.getText().toString();
        price = productPrice.getText().toString();
        store = storeName.getText().toString();
        location = locationName.getText().toString();
        expiration = expirationDate.getText().toString();
        description = productDescription.getText().toString();
        cat = category.getText().toString();
        new JSONTask().execute("http://52.91.100.201:8080/deal");
    }

    public class JSONTask extends AsyncTask<String,String, String> {
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
                connection.setRequestMethod("POST");


                JSONObject productInformation = new JSONObject();

                if(!product.equals(""))
                {
                    productInformation.put("productName", product);
                }

                if(!price.equals(""))
                {
                    productInformation.put("price", Double.parseDouble(price));
                }

                if(!store.equals(""))
                {
                    productInformation.put("storeName", store);
                }

                if(!location.equals(""))
                {
                    productInformation.put("location", location);
                }

                if(!expiration.equals(""))
                {
                    productInformation.put("expirationDate", expiration);
                }

                if(!description.equals(""))
                {
                    productInformation.put("description", description);
                }

                if(!cat.equals(""))
                {
                    productInformation.put("category", cat);
                }

                OutputStream os = connection.getOutputStream();
                os.write(productInformation.toString().getBytes("UTF-8"));
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

                } else {
                    System.out.println(connection.getResponseMessage());
                }
            }
            catch(MalformedURLException ex){
                ex.printStackTrace();
            }
            catch(IOException ex){
                ex.printStackTrace();
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

    }
}
