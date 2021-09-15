package edu.temple.convoy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

    private WelcomeInterface parentActivity;

    private Button loginButton;
    private Button registerButton;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity parentActivityTemp = getActivity();
        if (parentActivityTemp instanceof WelcomeInterface) {
            parentActivity = (WelcomeInterface) parentActivityTemp;
        } else {
            throw new RuntimeException("WelcomeInterface must be implemented in attached activity.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_welcome, container, false);

        loginButton = layout.findViewById(R.id.welcomeLoginButton);
        registerButton = layout.findViewById(R.id.welcomeRegisterButton);

        loginButton.setOnClickListener(v -> {
            parentActivity.welcomeLogin();
        });

        registerButton.setOnClickListener(v -> {
            parentActivity.welcomeRegister();
        });

        // Inflate the layout for this fragment
        return layout;
    }

    interface WelcomeInterface {
        public void welcomeLogin();
        public void welcomeRegister();
    }
}