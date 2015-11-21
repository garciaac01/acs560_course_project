package com.ipfw.myezshopper;


import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesManager{

    private SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private final String PREFERENCE_NAME = "name";
    private final String PREFERENCE_EMAIL = "email";
    private final String PREFERENCE_PASSWORD = "password";
    private final String PREFERENCE_ID = "id";
    private final String PREFERENCE_LOGGEDIN = "loggedin";
    private final String PREFERENCES = "userInfo";


    public PreferencesManager(Context context){
        sharedPref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void setName(String name) {
        editor.putString(PREFERENCE_NAME, name);
        editor.apply();
    }

    public void setEmail(String email) {
        editor.putString(PREFERENCE_EMAIL, email);
        editor.apply();
    }

    public void setPassword(String password) {
        editor.putString(PREFERENCE_PASSWORD, password);
        editor.apply();
    }

    public void setId(String id) {
        editor.putString(PREFERENCE_ID, id);
        editor.apply();
    }

    public void setLoggedin(boolean loggedin) {
        editor.putBoolean(PREFERENCE_LOGGEDIN, loggedin);
        editor.apply();
    }

    public void setAllPreferences(User u){

        editor.putBoolean(PREFERENCE_LOGGEDIN, u.isLoggedin());
        editor.putString(PREFERENCE_NAME, u.getName());
        editor.putString(PREFERENCE_EMAIL, u.getEmail());
        editor.putString(PREFERENCE_PASSWORD, u.getPassword());
        editor.putString(PREFERENCE_ID, u.getId());
        editor.apply();
    }

    public void removeAllPreferences(){
        editor.remove(PREFERENCE_LOGGEDIN);
        editor.remove(PREFERENCE_NAME);
        editor.remove(PREFERENCE_EMAIL);
        editor.remove(PREFERENCE_PASSWORD);
        editor.remove(PREFERENCE_ID);
        editor.commit();
    }

    public boolean getLoggedIn(){
        return sharedPref.getBoolean(PREFERENCE_LOGGEDIN, false);
    }

    public String getName(){
        return sharedPref.getString(PREFERENCE_NAME, "");
    }

    public String getEmail(){
        return sharedPref.getString(PREFERENCE_EMAIL, "");
    }

    public String getPassword(){
        return sharedPref.getString(PREFERENCE_PASSWORD, "");
    }

    public String getId(){
        return sharedPref.getString(PREFERENCE_ID, "");
    }

}
