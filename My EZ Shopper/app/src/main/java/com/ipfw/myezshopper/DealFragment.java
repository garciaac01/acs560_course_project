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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DealFragment extends Fragment implements View.OnClickListener{
    private EditText productName;
    private EditText productPrice;
    private EditText storeName;
    private Button expirationDate;
    private EditText productDescription;
    private Button submitDeal;
    private Spinner productCategorySpinner, locationSpinner;
    private String product, price, store, location, expiration, description, cat, date,
            categoryText,  //holds the dropdown category choice
            locationText; //holds the dropdown location choice
    String expDate;
    private PreferencesManager prefManager;
    private static final String DIALOG_DATE = "DialogDate", EXPIRATION_DATE = "com.ipfw.myezshopper.expdate";
    private static final int REQUEST_DATE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState){

        View v = inflater.inflate(R.layout.fragment_new_deal, container, false);
        prefManager = new PreferencesManager((this.getContext()));
        productName = (EditText) v.findViewById(R.id.productName);
        productPrice = (EditText) v.findViewById(R.id.productPrice);
        storeName = (EditText) v.findViewById(R.id.storeName);
        expirationDate = (Button) v.findViewById(R.id.expirationDate);

        if(onSavedInstanceState != null)
        {
            date = onSavedInstanceState.getString(EXPIRATION_DATE, "Set Expiration Date");
            expirationDate.setText(date);
        }
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

        //Set up the drop down list for location categories
        locationSpinner = (Spinner) v.findViewById(R.id.location_dropdown);

        //create a list of product categories
        List<String> locations = new ArrayList<String>();
        locations.add("Online and In Stores");
        locations.add("In-Store Only");
        locations.add("Online Only");
        locations.add("Specific Store Location");

        //attach the ArrayList to the Spinner
        ArrayAdapter<String> locationDataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, locations);
        locationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationDataAdapter);


        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Set up the drop down list for product categories
        productCategorySpinner = (Spinner) v.findViewById(R.id.product_category_dropdown);

        //create a list of product categories
        List<String> categories = new ArrayList<String>();
        categories.add("Electronics");
        categories.add("Movies, Music, Books");
        categories.add("Home Goods");
        categories.add("Clothes, Shoes, Jewelry");
        categories.add("Toys, Kid and Baby Products");
        categories.add("Food and Drink");
        categories.add("Health, Beauty, and Pharmacy");
        categories.add("Sports, Fitness, and Outdoors");
        categories.add("Other");

        //attach the ArrayList to the Spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(dataAdapter);


        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryText = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        submitDeal = (Button) v.findViewById(R.id.submit_deal_button);
        submitDeal.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v){
        product = productName.getText().toString();
        price = productPrice.getText().toString();
        store = storeName.getText().toString();
        location = locationText;
        expiration = expDate;
        description = productDescription.getText().toString();
        cat = categoryText;

        //check if expiration date is valid
        Calendar calToday = Calendar.getInstance();
        int year = calToday.get(Calendar.YEAR);
        int month = calToday.get(Calendar.MONTH)+1;
        int day = calToday.get(Calendar.DATE);
        String strToday = year + "-" + month + "-" + day;
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(tz);

        Calendar enteredExpirationDate = Calendar.getInstance();

        try{
            Date today = formatter.parse(strToday);
            if (expDate != null){
                enteredExpirationDate.setTimeInMillis(Long.parseLong(expDate));
            }else{
                throw new ParseException("", 0);
            }
            if (enteredExpirationDate.getTimeInMillis() < today.getTime()){
                Toast.makeText(getActivity(), "Expiration date is invalid", Toast.LENGTH_SHORT).show();
            }else if (!tryParseDouble(price)){
                Toast.makeText(getActivity(), "Price is not valid", Toast.LENGTH_SHORT).show();
            }else{
                new JSONTask().execute("http://52.91.100.201:8080/api/deal");
            }
        }catch(ParseException e){
            Toast.makeText(getActivity(), "No date is entered", Toast.LENGTH_SHORT).show();
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

                if(!product.equals("")){
                    productInformation.put("name", product);
                }else{
                    productInformation.put("name", "No Product Given");
                }

                productInformation.put("price", Double.parseDouble(price));

                if(!store.equals("")){
                    productInformation.put("storeName", store);
                }else{
                    productInformation.put("storeName", "No Store Given");
                }

                if(!location.equals("")){
                    productInformation.put("location", location);
                }else{
                    productInformation.put("location", "No Location Given");
                }

                if(!expiration.equals("Set Expiration Date")){
                    productInformation.put("expirationDate", expiration);
                }else{
                    productInformation.put("expirationDate", "No Expiration Date Given");
                }

                if(!description.equals("")){
                    productInformation.put("description", description);
                }else{
                    productInformation.put("description", "No description given");
                }

                if(!cat.equals("")){
                    productInformation.put("category", cat);
                }else{
                    productInformation.put("category", "Not Given");
                }

                productInformation.put("userId", prefManager.getId());

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
                    return "Deal successfully added";
                }else if(HttpResult == HttpURLConnection.HTTP_FORBIDDEN){
                    return "You have already added this deal";
                }else{
                    System.out.println(connection.getResponseMessage());
                    return ("Error adding deal");
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            date = data.getStringExtra(DatePickerFragment.EXTRA_DATE);
            expirationDate.setText(date.toString());
            expDate = data.getStringExtra(DatePickerFragment.EXTRA_LONG_DATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EXPIRATION_DATE, date);
    }

    private boolean tryParseDouble(String value){
        boolean isDouble;
        try{
            Double.parseDouble(value);
            isDouble = true;
        }catch (NumberFormatException ex){
            isDouble = false;
        }
        return isDouble;
    }
}
