package edu.temple.convoy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Send a POST request to: https://kamorris.com/lab/convoy/account.php
 *
 * For user registration:
 *  action: REGISTER
 *  username: a string representing the user's chosen username
 *  firstname: a string representing the user's first name
 *  lastname: a string representing the user's last name
 *  password: an alphanumeric string representing the user's password
 *
 * For user login:
 *  action: LOGIN
 *  username: a string representing the user's chosen username
 *  password: an alphanumeric string representing the user's password
 *
 * Response Format:
 *  On Success: {"status":"SUCCESS", "session_key": "String"}
 *  On Error: {"status":"ERROR", "message": "String"}
 */

public class MainActivity extends AppCompatActivity implements WelcomeFragment.WelcomeInterface,
        RegisterFragment.RegisterInterface, LoginFragment.LoginInterface {

    final String ACCOUNT_URL =" https://kamorris.com/lab/convoy/account.php";
    final String ACTION = "action";
    final String REGISTER = "REGISTER";
    final String LOGIN = "LOGIN";
    final String LOGOUT = "LOGOUT";
    final String FIRSTNAME = "firstname";
    final String LASTNAME = "lastname";
    final String USERNAME = "username";
    final String PASSWORD = "password";
    final String STATUS = "status";
    final String SESSION_KEY = "session_key";
    final String MESSAGE = "message";
    final String SUCCESS = "SUCCESS";
    final String ERROR = "ERROR";

    RequestQueue reQueue;
    String sessionKey;
    String status;
    String errorMessage;

    String nameKept;
    String usernameKept;

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
    protected void onDestroy() {
        // THIS IS ONLY TEMPORARY LIKE ALL THINGS IN THIS WORLD
        logout();
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
                        status = JSONResponse.getString(STATUS);
                        if (status.equals(SUCCESS)){
                            errorTextView.setText("");
                            usernameKept = username;
                            sessionKey = JSONResponse.getString(SESSION_KEY);
                            TextView debugSuccess = findViewById(R.id.debugLoginTextView);
                            String debugMessage = "Username: " + usernameKept +
                                    "\nSession Key: " + sessionKey;
                            debugSuccess.setText(debugMessage);
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
            protected Map<String, String> getParams() throws AuthFailureError {
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
                            status = JSONResponse.getString(STATUS);
                            if (status.equals(SUCCESS)){
                                errorTextView.setText("");
                                nameKept = firstname + " " + lastname;
                                usernameKept = username;
                                sessionKey = JSONResponse.getString(SESSION_KEY);
                                TextView debugSuccess = findViewById(R.id.debugRegisterTextView);
                                String debugMessage = "Name: " + nameKept + "\nUsername: " + usernameKept +
                                        "\nSession Key: " + sessionKey;
                                debugSuccess.setText(debugMessage);
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
                protected Map<String, String> getParams() throws AuthFailureError {
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

    public void logout() {
        StringRequest request = new StringRequest(Request.Method.POST, ACCOUNT_URL,
                response -> {
                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        status = JSONResponse.getString(STATUS);
                        if (status.equals(SUCCESS)){
                            usernameKept = "";
                            sessionKey = "";
                            Log.d("LOGOUT", "Success");
                        } else {
                            Log.d("LOGOUT", "Error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("LOGOUT", "Network Error");
                }) {
            // send parameters here
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(ACTION, LOGOUT);
                params.put(USERNAME, usernameKept);
                params.put(SESSION_KEY, sessionKey);
                return params;
            }
        };
        reQueue = Volley.newRequestQueue(this);
        reQueue.add(request);
    }
}