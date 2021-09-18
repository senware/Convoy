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
 * Use the {@link EndFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EndFragment extends Fragment {

    EndConvoyInterface parentActivity;

    public EndFragment() {
        // Required empty public constructor
    }

    public static EndFragment newInstance() {
        EndFragment fragment = new EndFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity parentActivityTemp = getActivity();
        if (parentActivityTemp instanceof EndConvoyInterface) {
            parentActivity = (EndConvoyInterface) parentActivityTemp;
        } else {
            throw new RuntimeException("EndConvoyInterface must be implemented in attached activity.");
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
        View layout = inflater.inflate(R.layout.fragment_end, container, false);

        FloatingActionButton endConvoyButton = layout.findViewById(R.id.endConvoyButton);
        endConvoyButton.setOnClickListener(v -> parentActivity.endConvoy());

        return layout;
    }

    interface EndConvoyInterface {
        void endConvoy();
    }
}