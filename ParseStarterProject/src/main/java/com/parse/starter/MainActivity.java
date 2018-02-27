/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Boolean signUpModeActive = true;
    Button signupButton;
    TextView changeSignupModeTextView;

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.changeSignupModeTextView) {

            if (signUpModeActive) {

                signUpModeActive = false;
                signupButton.setText("Login");
                changeSignupModeTextView.setText("Or, Signup");

            } else {

                signUpModeActive = true;
                signupButton.setText("Signup");
                changeSignupModeTextView.setText("Or, Login");

            }

        }

    }

    public void signUp(View view) {

        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {

            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();

        } else {

            if (signUpModeActive) {

                ParseUser user = new ParseUser();

                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());
                user.put("type","Student");

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            Log.i("Signup", "Successful");
//                            signUpModeActive = false;
                            Toast.makeText(MainActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
//                            signupButton.setText("Login");
//                            changeSignupModeTextView.setText("Or, Signup");
//                            usernameEditText.setText("");
//                            passwordEditText.setText("");
                            Intent intent = new Intent(MainActivity.this, ResumeAndSkillsActivity.class);
                            startActivity(intent);

                        } else {

                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            } else {

                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user != null) {

                            Log.i("Signup", "Login successful");
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(MainActivity.this,CounsellorListActivity.class);
                            Intent intent = new Intent(MainActivity.this,CounsellorListActivity.class);
                            startActivity(intent);

                        } else {

                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });


            }
        }


    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      changeSignupModeTextView = (TextView) findViewById(R.id.changeSignupModeTextView);
      signupButton = (Button) findViewById(R.id.signupButton);

      changeSignupModeTextView.setOnClickListener(this);

      if(ParseUser.getCurrentUser()!=null){
          startActivity(new Intent(this, WebViewActivity.class));
          finish();
      }


    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}


//   ParseObject score = new ParseObject("Score");
//      score.put("username", "Kb");
//      score.put("score",90);
//      score.saveInBackground(new SaveCallback() {
//          @Override
//          public void done(ParseException e) {
//              if(e==null){
//                  Log.d("saveCallback", "Success");
//              }
//          }
//      });

//      ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
//      query.getInBackground("aCe4iwCK5C", new GetCallback<ParseObject>() {
//          @Override
//          public void done(ParseObject object, ParseException e) {
//              if(e==null&&object!=null){
//                  Log.d("ObjectValue ", object.getString("username"));
//                  Log.d("ObjectValue ", Integer.toString(object.getInt("score")));
//              }
//          }
//      });