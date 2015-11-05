package com.ipfw.myezshopper;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

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
