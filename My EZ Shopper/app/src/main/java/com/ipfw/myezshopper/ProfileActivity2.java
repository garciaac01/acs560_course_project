package com.ipfw.myezshopper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class ProfileActivity2 extends FragmentActivity {
    public static final String EXTRA_USER_EMAIL = "com.ipfw.myezshopper.user_email";
    private String user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        user_email = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        Log.i("Profile Activity", user_email);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = new ProfileFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    public static Intent newIntent(Context packageContext, String email)
    {
        Intent i = new Intent(packageContext, ProfileActivity2.class);
        i.putExtra(EXTRA_USER_EMAIL, email);
        return i;
    }

}


