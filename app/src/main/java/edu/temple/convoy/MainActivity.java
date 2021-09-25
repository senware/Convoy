package edu.temple.convoy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.WelcomeInterface,
        RegisterFragment.RegisterInterface, LoginFragment.LoginInterface {

    final static String EXTRA_USERNAME = "edu.temple.convoy.USERNAME";
    final static String EXTRA_SESSION_KEY = "edu.temple.convoy.SESSION_KEY";

    final static String ACCOUNT_URL ="https://kamorris.com/lab/convoy/account.php";
    final static String CONVOY_URL = "https://kamorris.com/lab/convoy/convoy.php";
    final static String ACTION = "action";
    final static String END = "END";
    final static String CREATE = "CREATE";
    final static String JOIN = "JOIN";
    final static String LEAVE = "LEAVE";
    final static String REGISTER = "REGISTER";
    final static String LOGIN = "LOGIN";
    final static String LOGOUT = "LOGOUT";
    final static String CONVOY_ID = "convoy_id";
    final static String FIRSTNAME = "firstname";
    final static String LASTNAME = "lastname";
    final static String USERNAME = "username";
    final static String PASSWORD = "password";
    final static String STATUS = "status";
    final static String SESSION_KEY = "session_key";
    final static String MESSAGE = "message";
    final static String SUCCESS = "SUCCESS";
    final static String UPDATE = "UPDATE";
    final static String LATITUDE = "latitude";
    final static String LONGITUDE = "longitude";
    final static String DATA = "data";

    RequestQueue reQueue;

    String usernameKept;
    String sessionKey;

    FragmentManager manager;
    WelcomeFragment welcomeFragment;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            welcomeFragment = WelcomeFragment.newInstance();

            manager.beginTransaction()
                    .add(R.id.mainContainer, welcomeFragment, "WELCOME")
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.popBackStack();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_USERNAME, usernameKept);
        outState.putString(EXTRA_SESSION_KEY, sessionKey);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void welcomeLogin() {
        manager = getSupportFragmentManager();
        loginFragment = LoginFragment.newInstance();

        manager.beginTransaction()
                .replace(R.id.mainContainer, loginFragment, "LOGIN")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void welcomeRegister() {
        manager = getSupportFragmentManager();
        registerFragment = RegisterFragment.newInstance();

        manager.beginTransaction()
                .replace(R.id.mainContainer, registerFragment, "REGISTER")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void login() {
        EditText usernameEditText = findViewById(R.id.loginUsernameEditText);
        EditText passwordEditText = findViewById(R.id.loginPasswordEditText);

        TextView errorTextView = findViewById(R.id.loginErrorTextView);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.equals("") || password.equals("")){
            errorTextView.setText(R.string.blank_fields);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, ACCOUNT_URL,
                response -> {
                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        String status = JSONResponse.getString(STATUS);
                        if (status.equals(SUCCESS)){
                            errorTextView.setText("");
                            usernameKept = username;
                            sessionKey = JSONResponse.getString(SESSION_KEY);
                            TextView debugSuccess = findViewById(R.id.debugLoginTextView);
                            String debugMessage = "Username: " + usernameKept +
                                    "\nSession Key: " + sessionKey;
                            debugSuccess.setText(debugMessage);
                            launchConvoy();
                        } else {
                            errorTextView.setText(JSONResponse.getString(MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    passwordEditText.setText("");
                    errorTextView.setText(R.string.network_error);
                }) {
            // send parameters here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(ACTION, LOGIN);
                params.put(USERNAME, username);
                params.put(PASSWORD, password);
                return params;
            }
        };
        reQueue = Volley.newRequestQueue(this);
        reQueue.add(request);
    }

    @Override
    public void register() {
        EditText firstnameEditText = findViewById(R.id.firstnameEditText);
        EditText lastnameEditText = findViewById(R.id.lastnameEditText);
        EditText usernameEditText = findViewById(R.id.registerUsernameEditText);
        EditText passwordEditText = findViewById(R.id.registerPasswordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        TextView errorTextView = findViewById(R.id.registerErrorTextView);

        String firstname = firstnameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (username.equals("") || password.equals("") || firstname.equals("") || lastname.equals("")){
            errorTextView.setText(R.string.blank_fields);
            return;
        }

        if (password.equals(confirmPassword)) {
            errorTextView.setText("");
            StringRequest request = new StringRequest(Request.Method.POST, ACCOUNT_URL,
                    response -> {
                        try {
                            JSONObject JSONResponse = new JSONObject(response);
                            String status = JSONResponse.getString(STATUS);
                            if (status.equals(SUCCESS)){
                                errorTextView.setText("");
                                usernameKept = username;
                                sessionKey = JSONResponse.getString(SESSION_KEY);
                                TextView debugSuccess = findViewById(R.id.debugRegisterTextView);
                                String debugMessage = "Username: " + usernameKept + "\nSession Key: " + sessionKey;
                                debugSuccess.setText(debugMessage);
                                launchConvoy();
                            } else {
                                errorTextView.setText(JSONResponse.getString(MESSAGE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        passwordEditText.setText("");
                        confirmPasswordEditText.setText("");
                        errorTextView.setText(R.string.network_error);
                    }) {
                // send parameters here
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(ACTION, REGISTER);
                    params.put(FIRSTNAME, firstname);
                    params.put(LASTNAME, lastname);
                    params.put(USERNAME, username);
                    params.put(PASSWORD, password);
                    return params;
                }
            };
            reQueue = Volley.newRequestQueue(this);
            reQueue.add(request);
        } else {
            passwordEditText.setText("");
            confirmPasswordEditText.setText("");
            errorTextView.setText(R.string.password_error);
        }
    }


    private void launchConvoy() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(EXTRA_USERNAME, usernameKept);
        intent.putExtra(EXTRA_SESSION_KEY, sessionKey);
        startActivity(intent);
        finish();
    }
}