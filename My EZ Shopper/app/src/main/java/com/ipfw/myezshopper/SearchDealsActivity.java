package com.ipfw.myezshopper;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
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
import java.util.StringTokenizer;

public class SearchDealsActivity extends Fragment {

    EditText txtName;
    Spinner categorySpinner;
    Button searchButton, searchAllButton;
    String queryText = "name", concatenatedText;
    private final String walmartAPIkey = "e9rgk7ujvh43jaqxsytfcucm", SEARCH_ALL = "com.ipfw.myezshopper.search_all",
        BEST_BUY_API_KEY = "9vachckadjgrvc9htuhz2mn2";
    private String builtString = "", memberId, lastSearch = "", bestBuySearchString = "", lastBestBuySearch = "";
    private final int PRODUCTS_PER_API = 5;
    private RecyclerView mSearchRecyclerView, mApiRecyclerView;
    private ArrayList<String> searchResultList, searchIdList, apiResultList, bestBuySearchWords;
    DealAdapter mAdapter;
    ApiDealAdapter mApiAdapter;
    private TextView mUserDealTextView;
    PreferencesManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        prefManager = new PreferencesManager(this.getContext());
        memberId = prefManager.getId();

        View v = inflater.inflate(R.layout.activity_search_deals, container, false);
       // setContentView(R.layout.activity_search_deals);

        mUserDealTextView = (TextView) v.findViewById(R.id.top_user_deals);
        mSearchRecyclerView = (RecyclerView) v.findViewById(R.id.deals_recycler_view);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchResultList = new ArrayList<String>();
        searchIdList = new ArrayList<String>();
        bestBuySearchWords = new ArrayList<String>();

        updateUI();

        mApiRecyclerView = (RecyclerView) v.findViewById(R.id.api_recycler_view);
        mApiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        apiResultList = new ArrayList<String>();
        updateApiUI();

        txtName = (EditText) v.findViewById(R.id.txtName);
        categorySpinner = (Spinner) v.findViewById(R.id.category_spinner);
        searchButton = (Button) v.findViewById(R.id.search_button);
        searchAllButton = (Button) v.findViewById(R.id.search_all_button);

        List<String> categories = new ArrayList<String>();
        categories.add("Product Name");
        categories.add("Store Name");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queryText = parent.getItemAtPosition(position).toString();

                if (queryText.equals("Product Name")) {
                    txtName.setHint("Enter Product Name");
                    queryText = "name";
                } else if (queryText.equals("Store Name")) {
                    txtName.setHint("Enter Store Name");
                    queryText = "storeName";
                } else if (queryText.equals("Location")) {
                    txtName.setHint("Enter Location");
                    queryText = "location";
                } else if (queryText.equals("Category")) {
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
                mUserDealTextView.setText(Html.fromHtml("<b><u>Top User Submitted Deals</b></u>:"));
                String inputText = txtName.getText().toString();

                if (inputText.equals("")){
                    Toast.makeText(getActivity(), "Search field cannot be blank", Toast.LENGTH_LONG).show();
                }
                else{
                    //reset the string that we'll display to the user
                    builtString = "";
                    concatenatedText = "";
                    searchResultList.clear();
                    searchIdList.clear();
                    apiResultList.clear();
                    bestBuySearchWords.clear();

                    StringTokenizer st = new StringTokenizer(inputText, " ");

                    while (st.hasMoreTokens()){
                        String nextWord = st.nextToken();
                        concatenatedText += nextWord + "+";
                        bestBuySearchWords.add(nextWord);
                    }

                    //remove last + symbol
                    concatenatedText = concatenatedText.substring(0, concatenatedText.length() - 1);

                    //get walmart deals
                    String walmartURL = "http://api.walmartlabs.com/v1/search?query=" + concatenatedText + "&format=json&apiKey=" + walmartAPIkey;
                    new JSONTaskSearchWalMart().execute(walmartURL, "walmart");

                    bestBuySearchString = ""; //reset the best buy product search string

                    for(int i = 0; i < bestBuySearchWords.size(); i++){
                        if(i == 0){
                            bestBuySearchString += "((";
                        }
                        bestBuySearchString += "search=" + bestBuySearchWords.get(i);

                        if(i != bestBuySearchWords.size() - 1){
                            bestBuySearchString += "&";
                        }else{
                            bestBuySearchString += "))";
                        }
                    }

                    lastBestBuySearch = bestBuySearchString; //store this best buy search to use again after a thumb up/down

                    String bestBuyURL = "https://api.bestbuy.com/v1/products" + bestBuySearchString +"?apiKey=" + BEST_BUY_API_KEY + "&sort=inStoreAvailability.asc"
                        + "&show=inStoreAvailability,inStoreAvailabilityText,name,onlineAvailability,onlineAvailabilityText,onSale,regularPrice,salePrice,"
                        + "shortDescription&format=json";

                    new JSONTaskSearchBestBuy().execute(bestBuyURL);

                      //get user created deals
                    String URL = "http://52.91.100.201:8080/api/deal/search/" + queryText + "/" + concatenatedText;
                    new JSONTaskSearchParker().execute(URL);
                    lastSearch = concatenatedText; //store this search to use after thumb up/down call
                }
            }
        });//end setOnclickListener

        searchAllButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                mUserDealTextView.setText(Html.fromHtml("<b><u>Top User Submitted Deals</b></u>:"));

                String URL = "http://52.91.100.201:8080/api/deal";

                builtString = "";
                searchResultList.clear();
                searchIdList.clear();
                apiResultList.clear();
                bestBuySearchWords.clear();

                new JSONTaskSearchAll().execute(URL);
                lastSearch = SEARCH_ALL; //store this search to use after thumb up/down call
            }
        });

        if(getArguments() != null)
        {
            //User is searching for an item that has been clicked in their list.

            String myListProduct = getArguments().getString(ListFragment.EXTRA_PRODUCT_SEARCH, null);
            txtName.setText(myListProduct);

            String concatenatedText = "";
            String inputText = myListProduct;

            //todo andy: can this if statement be removed????
            if (inputText.equals("")){
                Toast.makeText(getActivity(), "Search field cannot be blank", Toast.LENGTH_LONG).show();
            }else{
                StringTokenizer st = new StringTokenizer(inputText, " ");

                bestBuySearchWords.clear();

                while (st.hasMoreTokens()){
                    String nextWord = st.nextToken();
                    concatenatedText += nextWord + "+";
                    bestBuySearchWords.add(nextWord);
                }

                //remove last + symbol
                concatenatedText = concatenatedText.substring(0, concatenatedText.length() - 1);

                builtString = "";
                searchResultList.clear();
                searchIdList.clear();
                apiResultList.clear();


                mUserDealTextView.setText(Html.fromHtml("<b><u>Top User Submitted Deals</b></u>:"));

                //get walmart deals
                String walmartURL = "http://api.walmartlabs.com/v1/search?query=" + concatenatedText + "&format=json&apiKey=" + walmartAPIkey;
                new JSONTaskSearchWalMart().execute(walmartURL, "walmart");

                bestBuySearchString = ""; //reset the best buy product search string

                for(int i = 0; i < bestBuySearchWords.size(); i++){
                    if(i == 0){
                        bestBuySearchString += "((";
                    }
                    bestBuySearchString += "search=" + bestBuySearchWords.get(i);

                    if(i != bestBuySearchWords.size() - 1){
                        bestBuySearchString += "&";
                    }else{
                        bestBuySearchString += "))";
                    }
                }

                lastBestBuySearch = bestBuySearchString; //store this search to use again after thumb up/down

                String bestBuyURL = "https://api.bestbuy.com/v1/products" + bestBuySearchString +"?apiKey=" + BEST_BUY_API_KEY + "&sort=inStoreAvailability.asc"
                        + "&show=inStoreAvailability,inStoreAvailabilityText,name,onlineAvailability,onlineAvailabilityText,onSale,regularPrice,salePrice,"
                        + "shortDescription&format=json";

                new JSONTaskSearchBestBuy().execute(bestBuyURL);

                String URL = "http://52.91.100.201:8080/api/deal/search/name/" + concatenatedText;
                new JSONTaskSearchParker().execute(URL);
                lastSearch = concatenatedText; //store this search to use after thumb up/down deal
            }
        }
        return v;
    }//end onCreate

    private class DealHolder extends RecyclerView.ViewHolder{
        private TextView mUserProductTextView;
        private Button thumbUp, thumbDown;
        String productID;

        public DealHolder(View itemView){
            super(itemView);

            mUserProductTextView = (TextView) itemView.findViewById(R.id.user_product_textview);
            thumbUp = (Button) itemView.findViewById(R.id.thumb_up);
            thumbDown = (Button) itemView.findViewById(R.id.thumb_down);

            thumbUp.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    //Toast.makeText(getActivity(), "Your ID is " + memberId + " and you clicked the thumb up on ID " + productID, Toast.LENGTH_LONG).show();
                    new JSONTaskLikeDeal().execute("http://52.91.100.201:8080/api/deal/like/" + productID, memberId, "like");
                }
            });

            thumbDown.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                   // Toast.makeText(getActivity(), "Your ID is " + memberId + " and you clicked the thumb down on ID " + productID, Toast.LENGTH_LONG).show();
                    new JSONTaskLikeDeal().execute("http://52.91.100.201:8080/api/deal/like/" + productID, memberId, "dislike");
                }
            });
        }
    }

    private class DealAdapter extends RecyclerView.Adapter<DealHolder>{
        private List<String> mDeals;
        private List<String> mDealIds;

        private DealAdapter(List<String> deals, List<String> dealIds){
            mDeals = deals;
            mDealIds = dealIds;
        }

        @Override
        public DealHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.single_user_deal_result, parent, false);
            return new DealHolder(view);
        }

        @Override
        public void onBindViewHolder (DealHolder holder, int position){
            String deal = mDeals.get(position);
            holder.mUserProductTextView.setText(Html.fromHtml(deal));
            holder.productID = mDealIds.get(position);
        }

        @Override
        public int getItemCount()
        {
            return mDeals.size();
        }
    }

    private void updateUI()
    {
        mAdapter = new DealAdapter(searchResultList, searchIdList);
        mSearchRecyclerView.setAdapter(mAdapter);
    }

    //set up the recycler view for the api results
    private class ApiDealHolder extends RecyclerView.ViewHolder{
        private TextView mApiTextView;

        public ApiDealHolder(View itemView){
            super(itemView);

            mApiTextView = (TextView) itemView.findViewById(R.id.api_deal_textview);
        }
    }

    private class ApiDealAdapter extends RecyclerView.Adapter<ApiDealHolder>{
        private List<String> mApiDeals;

        private ApiDealAdapter(List<String> apiDeals){
            mApiDeals = apiDeals;
        }

        @Override
        public ApiDealHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.single_shopping_api_result, parent, false);
            return new ApiDealHolder(view);
        }

        @Override
        public void onBindViewHolder (ApiDealHolder holder, int position){
            String deal = mApiDeals.get(position);
            holder.mApiTextView.setText(Html.fromHtml(deal));
        }

        @Override
        public int getItemCount()
        {
            return mApiDeals.size();
        }
    }

    private void updateApiUI()
    {
        mApiAdapter = new ApiDealAdapter(apiResultList);
        mApiRecyclerView.setAdapter(mApiAdapter);
    }

    public class JSONTaskSearchWalMart extends AsyncTask<String,String, String> {
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

                builtString += "";

                //there will be more than one parameter when we are searching the shopping APIs
                if(params.length > 1)
                {
                    String line;

                    //if the extra parameter is walmart, we will parse the walmart response and add it to builtString
                    if(params[1].equals("walmart")) {
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        String walmartString = buffer.toString();
                        JSONObject walmartDeals = new JSONObject(new String(walmartString));

                        if (Integer.parseInt(walmartDeals.get("totalResults").toString()) == 0) {
                            builtString += "<br><br>No Results Found from Walmart";
                        }else {
                            JSONArray walmartJSON = walmartDeals.getJSONArray("items");

                            for (int i = 0; i < PRODUCTS_PER_API; i++) {
                                //convert the next product in the array into JSON
                                JSONObject thisWalmartProduct = walmartJSON.getJSONObject(i);

                                if(i == 0){
                                    //begin building up the string with walmart results
                                    builtString += "<br><h4><b><u>Top Results from Walmart</b></u></h4>";
                                }else{
                                    builtString = "<br>";
                                }

                                builtString += "<br><b>" + (i + 1) + ". Product Name: </b>" + thisWalmartProduct.get("name");
                                builtString += "<br><b>Price: </b> $" + thisWalmartProduct.get("salePrice");
                                builtString += "<br><b>Location: </b>";

                                if (thisWalmartProduct.get("availableOnline").toString().equals("true")) {
                                    builtString += "Online and In-Store";
                                } else {
                                    builtString += "In-Store";
                                }

                                if (thisWalmartProduct.has("shortDescription")) {
                                    builtString += "<br><b>Description: </b>" + thisWalmartProduct.get("shortDescription");
                                }
                                apiResultList.add(builtString);
                            }
                            //for now, we will display the entire string on the TextView
                            //builtString += buffer.toString();
                        }
                    }
                }
                else{
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJSON = buffer.toString();
                    JSONArray parentArray = new JSONArray(finalJSON);

                    //count the products that are returned
                    int hitsCounter = 0;

                    if (parentArray.length() == 0){
                        builtString += "<br>No user submitted deals found";
                    }
                    else{
                        for (int i = 0; i < parentArray.length(); i++)
                        {
                            hitsCounter++;

                            JSONObject obj = parentArray.getJSONObject(i);

                            String format = "yyyy-MM-dd";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            Date d = sdf.parse(obj.getString("expirationDate"));
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(d);
                            String expDate = "";
                            expDate += cal.get(Calendar.MONTH) + 1 + "/";
                            expDate += cal.get(Calendar.DATE) + "/";
                            expDate += cal.get(Calendar.YEAR);

                            builtString += "<b>" + hitsCounter + ". " + obj.getString("name") + "<br>Price:</b> $" + obj.getDouble("price")
                                    + "<br><b>Store:</b> " + obj.getString("storeName") + "<br><b>Location:</b> " + obj.getString("location")
                                    + "<br><b>Details:</b> " + obj.get("description") + "<br><b>Category:</b> " + obj.get("category")
                                    + "<br><b>Expiration Date:</b> " +expDate + "<br><br>";
                        }
                    }
                }
                return builtString;
            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            }catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }catch (ParseException ex){
                return "DATE EXCEPTION";
            }
            finally{
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

            updateApiUI();
            updateUI();

            if (result != null && result.equals("No network connection")){
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }else{
               // productText.setText(Html.fromHtml(result.toString()));
            }
        }
    }

    public class JSONTaskSearchBestBuy extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                builtString += "";

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String bestBuyResponse = buffer.toString();
                JSONObject bestBuyDeals = new JSONObject(new String(bestBuyResponse));

                Log.i("Total Results", bestBuyDeals.get("products").toString());

                if (Integer.parseInt(bestBuyDeals.get("total").toString()) == 0) {
                    builtString += "<br><br>No Results Found from Best Buy";
                }
                else {
                    JSONArray bestBuyJSON = bestBuyDeals.getJSONArray("products");

                    for (int i = 0; i < PRODUCTS_PER_API; i++) {
                        if(i == 0){
                            //begin building up the string with best buy results
                            builtString = "<br><h4><b><u>Top Results from Best Buy</b></u></h4>";
                        }else{
                            builtString = "<br>";

                        }
                        //convert the next product in the array into JSON
                        JSONObject thisBestBuyProduct = bestBuyJSON.getJSONObject(i);

                        builtString += "<br><b>" + (i + 1) + ". Product Name: </b>" + thisBestBuyProduct.get("name");

                        if(thisBestBuyProduct.has("salePrice")){
                            builtString += "<br><b>Price: </b> $" + thisBestBuyProduct.get("salePrice");
                        }else{
                            builtString += "<br><b>Price: </b> $" + thisBestBuyProduct.get("regularPrice");
                        }

                        builtString += "<br><b>Location: </b>";

                        if (thisBestBuyProduct.get("inStoreAvailability").toString().equals("true") && thisBestBuyProduct.get("onlineAvailability").toString().equals("true")){
                            builtString += "Online and In-Store";
                        } else if(thisBestBuyProduct.get("inStoreAvailability").toString().equals("true")){
                            builtString += "In-Store only";
                        }else{
                            builtString += "Online only";
                        }

                        if (thisBestBuyProduct.has("shortDescription")) {
                            if(!thisBestBuyProduct.get("shortDescription").toString().equals("null")) {
                                builtString += "<br><b>Description: </b>" + thisBestBuyProduct.get("shortDescription");
                                System.out.println(thisBestBuyProduct.get("shortDescription"));
                            }
                        }
                        apiResultList.add(builtString);
                    }
                            //for now, we will display the entire string on the TextView
                            //builtString += buffer.toString();

                }
                return builtString;
            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            }catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            finally{
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

            updateApiUI();
            updateUI();

            if (result != null && result.equals("No network connection")){
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }else{
                // productText.setText(Html.fromHtml(result.toString()));
            }
        }
    }

    public class JSONTaskSearchParker extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();

                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();

                    builtString = "";

                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJSON = buffer.toString();
                    JSONArray parentArray = new JSONArray(finalJSON);

                    //count the products that are returned
                    int hitsCounter = 0;

                    if (parentArray.length() == 0){
                        builtString += "<br><br>No user submitted deals found";
                    }
                    else{
                        for (int i = 0; i < parentArray.length(); i++){
                            if(hitsCounter == 0){
                                //begin building up the results for user posted deals
                                builtString += "<br><h4><b><u>User Submitted Deals</u>:</b></h4>";
                            }
                            builtString = "";
                            hitsCounter++;

                            JSONObject obj = parentArray.getJSONObject(i);

                            Log.i("JSON response", obj.toString());
                            String format = "yyyy-MM-dd";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            Date d = sdf.parse(obj.getString("expirationDate"));
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(d);
                            String expDate = "";
                            expDate += cal.get(Calendar.MONTH) + 1 + "/";
                            expDate += cal.get(Calendar.DATE) + "/";
                            expDate += cal.get(Calendar.YEAR);

                            builtString += "<b>" + hitsCounter + ". " + obj.getString("name") + "<br>Price:</b> $" + obj.getDouble("price")
                                    + "<br><b>Store:</b> " + obj.getString("storeName") + "<br><b>Location:</b> " + obj.getString("location")
                                    + "<br><b>Details:</b> " + obj.get("description") + "<br><b>Category:</b> " + obj.get("category")
                                    + "<br><b>Likes: </b>" + obj.get("likeCount") + "<br><b>Dislikes: </b>" + obj.get("dislikeCount") + "<br><b>Expiration Date:</b> " +expDate + "<br><br>";
                            searchResultList.add(builtString);
                            searchIdList.add(obj.getString("_id"));
                        }
                    }
                }else if (HttpResult == 404){
                    builtString += "<br>No user submitted deals found";
                }else{
                    builtString += "Error";
                }
                return builtString;
            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            }catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }catch (ParseException ex){
                return "DATE EXCEPTION";
            }
            finally{
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

            updateUI();
            updateApiUI();

            if (result.equals("No network connection")){
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class JSONTaskSearch extends AsyncTask<String,String, String> {
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

                builtString += "";

                //there will be more than one parameter when we are searching the shopping APIs
                if(params.length > 1)
                {
                    String line;

                    //if the extra parameter is walmart, we will parse the walmart response and add it to builtString
                    if(params[1].equals("walmart")) {
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        String walmartString = buffer.toString();
                        JSONObject walmartDeals = new JSONObject(new String(walmartString));

                        //begin building up the string with walmart results
                        builtString += "<h4><b><u>Top Results from Walmart</b></u></h4>";

                        if (Integer.parseInt(walmartDeals.get("totalResults").toString()) == 0) {
                            builtString += "<br><br>No Results Found from Walmart";
                        }else {
                            JSONArray walmartJSON = walmartDeals.getJSONArray("items");

                            //this JSON object will hold the JSON each time we loop through the products
                            JSONObject nextWalmartProduct;

                            for (int i = 0; i < PRODUCTS_PER_API; i++) {
                                //convert the next product in the array into JSON
                                JSONObject thisWalmartProduct = walmartJSON.getJSONObject(i);

                                if (i != 0) {
                                    builtString += "<br>";
                                }
                                builtString += "<br><b>" + (i + 1) + ". Product Name: </b>" + thisWalmartProduct.get("name");
                                builtString += "<br><b>Price: </b> $" + thisWalmartProduct.get("salePrice");
                                builtString += "<br><b>Location: </b>";

                                if (thisWalmartProduct.get("availableOnline").toString().equals("true")) {
                                    builtString += "Online and In-Store";
                                } else {
                                    builtString += "In-Store";
                                }

                                if (thisWalmartProduct.has("shortDescription")) {
                                    builtString += "<br><b>Description: </b>" + thisWalmartProduct.get("shortDescription");
                                }
                            }
                            //for now, we will display the entire string on the TextView
                            //builtString += buffer.toString();
                        }
                    }
                }
                else{
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJSON = buffer.toString();
                    JSONArray parentArray = new JSONArray(finalJSON);

                    //count the products that are returned
                    int hitsCounter = 0;

                    if (parentArray.length() == 0){
                        builtString += "<br>No user submitted deals found";
                    }
                    else{
                        for (int i = 0; i < parentArray.length(); i++){

                            hitsCounter++;

                            JSONObject obj = parentArray.getJSONObject(i);

                            String format = "yyyy-MM-dd";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            Date d = sdf.parse(obj.getString("expirationDate"));
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(d);
                            String expDate = "";
                            expDate += cal.get(Calendar.MONTH) + 1 + "/";
                            expDate += cal.get(Calendar.DATE) + "/";
                            expDate += cal.get(Calendar.YEAR);

                            builtString += "<b>" + hitsCounter + ". " + obj.getString("name") + "<br>Price:</b> $" + obj.getDouble("price")
                                    + "<br><b>Store:</b> " + obj.getString("storeName") + "<br><b>Location:</b> " + obj.getString("location")
                                    + "<br><b>Details:</b> " + obj.get("description") + "<br><b>Category:</b> " + obj.get("category")
                                    + "<br><b>Expiration Date:</b> " +expDate + "<br><br>";
                        }
                    }
                }
                return builtString;
            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            }catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }catch (ParseException ex){
                return "DATE EXCEPTION";
            }
            finally{
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
            updateUI();
            updateApiUI();

            if (result.equals("No network connection")){
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class JSONTaskSearchAll extends AsyncTask<String,String, String> {
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

                builtString = "";

                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJSON = buffer.toString();
                    JSONArray parentArray = new JSONArray(finalJSON);


                    //count the products that are returned
                    int hitsCounter = 0;

                    if (parentArray.length() == 0){
                        builtString += "<br>No user submitted deals found";
                    }
                    else{
                        for (int i = 0; i < parentArray.length(); i++){
                            hitsCounter++;

                            JSONObject obj = parentArray.getJSONObject(i);

                            String format = "yyyy-MM-dd";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            Date d = sdf.parse(obj.getString("expirationDate"));
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(d);
                            String expDate = "";
                            expDate += cal.get(Calendar.MONTH) + 1 + "/";
                            expDate += cal.get(Calendar.DATE) + "/";
                            expDate += cal.get(Calendar.YEAR);

                            builtString += "<b>" + hitsCounter + ". " + obj.getString("name") + "<br>Price:</b> $" + obj.getDouble("price")
                                    + "<br><b>Store:</b> " + obj.getString("storeName") + "<br><b>Location:</b> " + obj.getString("location")
                                    + "<br><b>Details:</b> " + obj.get("description") + "<br><b>Category:</b> " + obj.get("category")
                                    + "<br><b>Likes: </b>" + obj.get("likeCount") + "<br><b>Dislikes: </b>" + obj.get("dislikeCount") + "<br><b>Expiration Date:</b> " +expDate + "<br><br>";
                            searchResultList.add(builtString);
                            searchIdList.add(obj.getString("_id"));
                            builtString = "";
                        }
                    }
                return builtString;
            }catch(MalformedURLException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
                return "No network connection";
            }catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }catch (ParseException ex){
                return "DATE EXCEPTION";
            }
            finally{
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

            updateUI();
            updateApiUI();

            if (result.equals("No network connection")){
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class JSONTaskLikeDeal extends AsyncTask<String,String, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader br = null;
            try{
                URL url = new URL(params[0]);
                String userID = params[1];
                String likeDeal = params[2];
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("PUT");

                JSONObject userInformation = new JSONObject();

                userInformation.put("userId", userID);
                userInformation.put("type", likeDeal);

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

                    JSONObject obj = new JSONObject(sb.toString());

                    if(obj.getString("voteChanged").equals("true")){
                        return "You changed your vote to \"" + params[2] + "\"";
                    }else{
                        return "You " + params[2] + "d this deal";
                    }
                } else {
                    System.out.println("Else connection response: " + connection.getResponseMessage());
                    return ("You've already voted on this deal");
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
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();

            if(lastSearch.equals(SEARCH_ALL))
            {
                String URL = "http://52.91.100.201:8080/api/deal";

                builtString = "";
                searchResultList.clear();
                searchIdList.clear();
                apiResultList.clear();
                bestBuySearchWords.clear();
                new JSONTaskSearchAll().execute(URL);
            }
            else
            {
                builtString = "";
                searchResultList.clear();
                searchIdList.clear();
                apiResultList.clear();
                bestBuySearchWords.clear();

                String walmartURL = "http://api.walmartlabs.com/v1/search?query=" + lastSearch + "&format=json&apiKey=" + walmartAPIkey;
                new JSONTaskSearchWalMart().execute(walmartURL, "walmart");

                String bestBuyURL = "https://api.bestbuy.com/v1/products" + lastBestBuySearch +"?apiKey=" + BEST_BUY_API_KEY + "&sort=inStoreAvailability.asc"
                        + "&show=inStoreAvailability,inStoreAvailabilityText,name,onlineAvailability,onlineAvailabilityText,onSale,regularPrice,salePrice,"
                        + "shortDescription&format=json";

                new JSONTaskSearchBestBuy().execute(bestBuyURL);

                //get user created deals
                String URL = "http://52.91.100.201:8080/api/deal/search/" + queryText + "/" + lastSearch;
                new JSONTaskSearchParker().execute(URL);
            }
        }
    }
}

