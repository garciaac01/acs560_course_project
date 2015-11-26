package com.ipfw.myezshopper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

public class ListFragment extends Fragment{ // implements View.OnClickListener{
    private RecyclerView mShoppingRecyclerView;
    private ShoppingAdapter mAdapter;
    private int itemListLength;  //may want to move this to a newIntent method in ProfileFragment
    private ArrayList<String> shoppingList;
    private String member_id, itemToDelete;
    public static String EXTRA_PRODUCT_SEARCH = "com.ipfw.myezshopper.myProductName", ADD_ITEM = "add item";
    public static final int ADD_ITEM_REQUEST_CODE = 0;
    private FloatingActionButton floatingActionButton;

    private PreferencesManager prefManager;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState){

        prefManager = new PreferencesManager(this.getContext());

        member_id = prefManager.getId();
        shoppingList = new ArrayList<String>();

        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        mShoppingRecyclerView = (RecyclerView) v.findViewById(R.id.shopping_recycler_view);
        mShoppingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();


        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                AddItemFragment addFragment = new AddItemFragment();
                addFragment.setTargetFragment(ListFragment.this, ADD_ITEM_REQUEST_CODE);
                addFragment.show(manager, ADD_ITEM);
            }
        });

        //btnSubmitListItem.setOnClickListener(this);   Should bring this back later as a floating button

        //Access users shopping list on database.
        //Store items locally
        //Display items in list textView
        //get user's list from database
        String URL = "http://52.91.100.201:8080/api/user/" + member_id;

        new JSONTaskGet().execute(URL);

        //updateUI();
        return v;
    }

    private class ShoppingListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mMyProductName;
        private Button mDeleteButton;

        public ShoppingListHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            mMyProductName = (TextView) itemView.findViewById(R.id.my_product);
            mDeleteButton = (Button) itemView.findViewById(R.id.delete_button);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(mMyProductName.getText().toString());
                }
            });
        }

        @Override
        public void onClick(View v){
            Bundle args = new Bundle();
            args.putString(EXTRA_PRODUCT_SEARCH, mMyProductName.getText().toString());
            SearchDealsActivity newSearch = new SearchDealsActivity();
            newSearch.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSearch).addToBackStack(null).commit();
        }
    }

    private class ShoppingAdapter extends RecyclerView.Adapter<ShoppingListHolder>{
        private List<String> mShoppingItems;

        public ShoppingAdapter(List<String> shoppingItems)
        {
            mShoppingItems = shoppingItems;
        }

        @Override
        public ShoppingListHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.fragment_single_shopping_item, parent, false);
            return new ShoppingListHolder(view);
        }

        @Override
        public void onBindViewHolder(ShoppingListHolder holder, int position)
        {
            String shoppingListItem = mShoppingItems.get(position);
            holder.mMyProductName.setText(shoppingListItem);
        }

        @Override
        public int getItemCount()
        {
            return mShoppingItems.size();
        }
    }

    private void updateUI(){
        mAdapter = new ShoppingAdapter(shoppingList);
        mShoppingRecyclerView.setAdapter(mAdapter);
    }

    private void showDeleteDialog(String itemToDelete)
    {
        this.itemToDelete = itemToDelete;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setMessage("Delete " + itemToDelete + " from shopping list?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    shoppingList.remove(itemToDelete);
                    new JSONTaskPost().execute("http://52.91.100.201:8080/api/user/" + member_id);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    /*
     *Get the value returned from the AddItemFragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == ADD_ITEM_REQUEST_CODE){
            if(data.getStringExtra(AddItemFragment.EXTRA_NEW_ITEM) != null && !data.getStringExtra(AddItemFragment.EXTRA_NEW_ITEM).equals("")) {
                shoppingList.add(data.getStringExtra(AddItemFragment.EXTRA_NEW_ITEM));

                new JSONTaskPost().execute("http://52.91.100.201:8080/api/user/" + member_id);
            }
            else
            {
                Toast.makeText(getActivity(), "Product name cannot be blank", Toast.LENGTH_LONG).show();
            }
        }
    }

   /* @Override
    public void onClick(View v){
        product = productName.getText().toString();

        if (product.equals("")){
            Toast.makeText(getActivity(), "Product name cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else{
            //todo save new product locally
            //add item to local list
            shoppingList.add(product);

            new JSONTaskPost().execute("http://52.91.100.201:8080/user/" + member_id);
        }
    }*/

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
                    return "Shopping List Updated";
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

            //loop through local list and display
            String builtString = "";
            for (String str : shoppingList){
                builtString += str + ",";
            }

            updateUI();
            //txtList.setText(builtString);
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
           // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            updateUI();

          /*  if (result.equals("No network connection")){
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }else{
                //display to textview
                txtList.setText(result);
            }*/
        }

    }//end JSONTaskGet class


}
