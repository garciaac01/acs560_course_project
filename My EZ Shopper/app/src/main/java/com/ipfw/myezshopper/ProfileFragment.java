package com.ipfw.myezshopper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private PreferencesManager prefManager;

    private Button goToSearchFragment;
    private Button goToAddDealFragment;
    private Button goToListFragment;
    private Button goToChangePasswordFragment;
    private TextView userEmailTextView;
    private String name, member_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        prefManager = new PreferencesManager(this.getContext());

        userEmailTextView = (TextView) v.findViewById(R.id.welcome_text);
        name = prefManager.getName();
        member_id = prefManager.getId();

        if(name != null)
        {
            userEmailTextView.setText(Html.fromHtml("<H1>Welcome, " + name + "!</H1>"));
        }
        goToAddDealFragment = (Button) v.findViewById(R.id.go_to_new_deal_fragment);
        goToAddDealFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealFragment newDeal = new DealFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newDeal).addToBackStack(null).commit();
            }
        });

        goToSearchFragment = (Button) v.findViewById(R.id.go_to_search_fragment);
        goToSearchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDealsActivity newSearch = new SearchDealsActivity();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSearch).addToBackStack(null).commit();
            }
        });

        goToListFragment = (Button) v.findViewById(R.id.go_to_new_list_fragment);
        goToListFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragment newList = new ListFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newList).addToBackStack(null).commit();
            }
        });

        goToChangePasswordFragment = (Button) v.findViewById(R.id.go_to_chgpassword_fragment);
        goToChangePasswordFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment newPassword = new ChangePasswordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newPassword).addToBackStack(null).commit();
            }
        });

        return v;
    }


}
