package com.ipfw.myezshopper;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;

/**
 * Created by garci_000 on 10/31/2015.
 */
public class DealFragment extends Fragment implements View.OnClickListener{
    private EditText productName;
    private EditText productPrice;
    private EditText storeName;
    private EditText locationName;
    private Button expirationDate;
    private EditText productDescription;
    private EditText category;
    private Button submitDeal;
    String product, price, store, location, expiration, description, cat;
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;


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
        expirationDate = (Button) v.findViewById(R.id.expirationDate);
        expirationDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.setTargetFragment(DealFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
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

        try {
            Double.parseDouble(price);
            new JSONTask().execute("http://52.91.100.201:8080/deal");
        }catch(NumberFormatException e)
        {
            Toast.makeText(getActivity(), R.string.invalid_price, Toast.LENGTH_SHORT).show();
        }
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
                    productInformation.put("name", product);
                }
                else
                {
                    productInformation.put("name", "No Product Given");
                }

                productInformation.put("price", Double.parseDouble(price));


                if(!store.equals(""))
                {
                    productInformation.put("storeName", store);
                }
                else
                {
                    productInformation.put("storeName", "No Store Given");
                }

                if(!location.equals(""))
                {
                    productInformation.put("location", location);
                }
                else
                {
                    productInformation.put("location", "No Location Given");
                }

                if(!expiration.equals("Set Expiration Date"))
                {
                    productInformation.put("expirationDate", expiration);
                }
                else
                {
                    productInformation.put("expirationDate", "No Expiration Date Given");
                }

                if(!description.equals(""))
                {
                    productInformation.put("description", description);
                }
                else
                {
                    productInformation.put("description", "No description given");
                }

                if(!cat.equals(""))
                {
                    productInformation.put("category", cat);
                }
                else
                {
                    productInformation.put("category", "Not Given");
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            //Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            String date = data.getStringExtra(DatePickerFragment.EXTRA_DATE);
            expirationDate.setText(date.toString());
        }
    }
}
