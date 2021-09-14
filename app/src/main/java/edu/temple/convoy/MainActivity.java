package edu.temple.convoy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout mainActivityLayout = (FrameLayout) findViewById(R.id.mainContainer);

        FragmentManager manager = getSupportFragmentManager();

        WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();

        manager.beginTransaction()
                .add(R.id.mainContainer, welcomeFragment, null)
                .commit();
    }
}