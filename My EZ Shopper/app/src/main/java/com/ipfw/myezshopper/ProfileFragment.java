package com.ipfw.myezshopper;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private Button goToSearchFragment;
    private Button goToAddDealFragment;
    private TextView userEmailTextView;
    private String user_email, member_id;
    private List<String> shoppingList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        userEmailTextView = (TextView) v.findViewById(R.id.welcome_text);
        user_email = getActivity().getIntent().getStringExtra(ProfileActivity2.EXTRA_USER_EMAIL);
        member_id = getActivity().getIntent().getStringExtra(ProfileActivity2.EXTRA_MEMBER_ID);
        shoppingList = new ArrayList<>();

        Log.i("Profile Fragment", String.valueOf(ProfileActivity2.getShoppingListLength()));

        if(user_email != null)
        {
            userEmailTextView.setText(Html.fromHtml("<H1>Welcome, " + user_email.toString() + "!</H1>"));
        }
        goToAddDealFragment = (Button) v.findViewById(R.id.go_to_new_deal_fragment);
        goToAddDealFragment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                DealFragment newDeal = new DealFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newDeal).addToBackStack(null).commit();
            }
        });

        goToSearchFragment = (Button) v.findViewById(R.id.go_to_search_fragment);
        goToSearchFragment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                SearchDealsActivity newSearch = new SearchDealsActivity();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSearch).addToBackStack(null).commit();
            }
        });
        return v;
    }


}
