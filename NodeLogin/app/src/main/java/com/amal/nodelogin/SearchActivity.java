package com.amal.nodelogin;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {

    EditText productText;
    Button searchButton;
    TextView response;
    List<NameValuePair> productJSON;
    SharedPreferences pref;
    String token, TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        productText = (EditText) findViewById(R.id.product);
        searchButton = (Button) findViewById(R.id.search_button);
        response = (TextView) findViewById(R.id.database_response);
        productJSON = new ArrayList<NameValuePair>();

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        token = pref.getString("token", "");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productString = productText.getText().toString();
                productJSON.add(new BasicNameValuePair("product", productString));
                //Log.d(TAG, productJSON.toString());
                ServerRequest sr = new ServerRequest();
                JSONObject json = sr.getJSON("http://52.23.206.253:8080/search", productJSON);
                //Toast.makeText(SearchActivity.this, json.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
