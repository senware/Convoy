package edu.temple.convoy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nambimobile.widgets.efab.FabOption;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JoinCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JoinCreateFragment extends Fragment {

    JoinCreateInterface parentActivity;

    public JoinCreateFragment() {
        // Required empty public constructor
    }

    public static JoinCreateFragment newInstance() {
        JoinCreateFragment fragment = new JoinCreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity parentActivityTemp = getActivity();
        if (parentActivityTemp instanceof JoinCreateInterface) {
            parentActivity = (JoinCreateInterface) parentActivityTemp;
        } else {
            throw new RuntimeException("JoinCreateInterface must be implemented in attached activity.");
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
        View layout = inflater.inflate(R.layout.fragment_join_create, container, false);

        FabOption joinConvoyButton = layout.findViewById(R.id.joinConvoyButton);
        FabOption startConvoyButton = layout.findViewById(R.id.startConvoyButton);
        joinConvoyButton.setOnClickListener(v -> parentActivity.joinConvoy());
        startConvoyButton.setOnClickListener(v -> parentActivity.createConvoy());

        return layout;
    }

    interface JoinCreateInterface {
        void joinConvoy();
        void createConvoy();
    }
}