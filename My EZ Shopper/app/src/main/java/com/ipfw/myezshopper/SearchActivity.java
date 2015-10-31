//Currently this gets all user information from the db.


package com.ipfw.myezshopper;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
//        productJSON = new ArrayList<NameValuePair>();
//
//        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
//        token = pref.getString("token", "");

        // check if you are connected or not
//        if(isConnected()){
//            response.setBackgroundColor(0xFF00CC00);
//            response.setText("You are connected");
//        }
//        else{
//            response.setText("You are NOT connected");
//        }

        // call AsyncTask to perform network operation on separate thread
//        new HttpAsyncTask().execute("http://hmkcode.appspot.com/rest/controller/get.json");
        new HttpAsyncTask().execute("http://52.91.100.201:8080/user");

//        commented out to prevent crashing client
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String productString = productText.getText().toString();
//                productJSON.add(new BasicNameValuePair("product", productString));
//
//                //TODO modify to work as async task
//                //Added to remove onMainThreadException
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//
//                Toast.makeText(SearchActivity.this, "", Toast.LENGTH_LONG).show();
//
//            }
//        });//end setOnClickListener
    }//end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }//end onCreateOptionsMenu

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
    }//end onOptionsItemSelected

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }


        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            //result coming in is a string of JSON



            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            productText.setText(result);
        }
    }

}//end SearchActivity class
