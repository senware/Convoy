package edu.temple.convoy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.WelcomeInterface {

    FragmentManager manager;
    WelcomeFragment welcomeFragment;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();

        welcomeFragment = WelcomeFragment.newInstance();

        manager.beginTransaction()
                .add(R.id.mainContainer, welcomeFragment, "WELCOME")
                .commit();
    }

    @Override
    public void gotoLogin() {
        manager = getSupportFragmentManager();
        loginFragment = LoginFragment.newInstance();

        manager.beginTransaction()
                .replace(R.id.mainContainer, loginFragment, "LOGIN")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void gotoRegister() {
        manager = getSupportFragmentManager();
        registerFragment = RegisterFragment.newInstance();

        manager.beginTransaction()
                .replace(R.id.mainContainer, registerFragment, "REGISTER")
                .addToBackStack(null)
                .commit();
    }
}