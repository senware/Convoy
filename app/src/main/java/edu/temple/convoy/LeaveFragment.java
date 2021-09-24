package edu.temple.convoy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaveFragment extends Fragment {

   LeaveConvoyInterface parentActivity;

    public LeaveFragment() {
        // Required empty public constructor
    }

    public static LeaveFragment newInstance() {
        LeaveFragment fragment = new LeaveFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity parentActivityTemp = getActivity();
        if (parentActivityTemp instanceof LeaveConvoyInterface) {
            parentActivity = (LeaveConvoyInterface) parentActivityTemp;
        } else {
            throw new RuntimeException("LeaveConvoyInterface must be implemented in attached activity.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_leave, container, false);

        FloatingActionButton leaveConvoyButton = layout.findViewById(R.id.leaveConvoyButton);
        leaveConvoyButton.setOnClickListener(v -> parentActivity.leaveConvoy());

        return layout;
    }

    interface LeaveConvoyInterface {
        void leaveConvoy();
    }
}