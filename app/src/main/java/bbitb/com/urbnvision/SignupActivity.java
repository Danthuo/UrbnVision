package bbitb.com.urbnvision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import bbitb.com.urbnvision.models.Review;
import bbitb.com.urbnvision.models.Student;

public class SignupActivity extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextPassword;
    ProgressDialog mProgressDialog;
    View msignUpFormView;
    Spinner accTypeSpinner;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //if the user is already logged in we will directly start the main activity
        /*if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, StudentMainActivity.class));
            return;
        }*/

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.pass);

        accTypeSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SignupActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.accountTypes));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accTypeSpinner.setAdapter(myAdapter);


        //mProgressView = findViewById(R.id.signup_progress);
        msignUpFormView = findViewById(R.id.signUpForm);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                registerUser();
            }
        });

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            //handle the already logged in user
            finish();
            startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));
        }
    }

    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        //final String phone = editTextPhone.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String spin = accTypeSpinner.getSelectedItem().toString().trim();
        final String photoUrl = "default";
        final String phone = "none";


        final String companyUsername = editTextUsername.getText().toString().trim();
        final String companyName = "Not Provided";
        final String companyEmail = editTextEmail.getText().toString().trim();
        final String companySpin = accTypeSpinner.getSelectedItem().toString().trim();
        final String companyPhone = "none";
        final String companyDesc = "none";
        final String companyPhotoUrl = "default";
        final String companyWebsite = "none";
        final String companyNotifications = "none";

        final double totalRating = 0;
        final int totalVoters = 0;
        final int star1 = 0;
        final int star2 = 0;
        final int star3 = 0;
        final int star4 = 0;
        final int star5 = 0;

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter username");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }


        if(accTypeSpinner == null && accTypeSpinner.getSelectedItem() == null){
            Toast.makeText(SignupActivity.this, getString(R.string.spinnerSelectionFailure), Toast.LENGTH_LONG).show();
            accTypeSpinner.requestFocus();
            return;
        }

       /* if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Enter a phone number");
            editTextPhone.requestFocus();
            return;
        }*/

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter a password");
            editTextPassword.requestFocus();
            return;
        }


        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                        //we will store additional fields in FireBase database
                            Student student = new Student(
                              username, email, spin, photoUrl, phone
                            );

                            String companyUid = mAuth.getCurrentUser().getUid();
                            //Review review = new Review();

                            Company company = new Company(companyUid, companyUsername, companyName, companyEmail,
                                    companySpin, companyPhone, companyDesc,
                                     companyWebsite, companyNotifications, companyPhotoUrl);



                            if(spin.equals("Student")){
                            FirebaseDatabase.getInstance().getReference("Student")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        dismissProgressDialog();
                                        mAuth.signOut();
                                        Toast.makeText(SignupActivity.this, getString(R.string.registrationSuccess), Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    }else{
                                        dismissProgressDialog();
                                        Toast.makeText(SignupActivity.this, getString(R.string.registrationFailure), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            }else if(spin.equals("Company")) {
                                FirebaseDatabase.getInstance().getReference("Company")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(company).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Company companyRatings = new Company(totalVoters, totalRating, star1, star2, star3, star4, star5);
                                            FirebaseDatabase.getInstance().getReference("Company")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ratings")
                                                    .setValue(companyRatings)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        dismissProgressDialog();
                                                        mAuth.signOut();
                                                        Toast.makeText(SignupActivity.this, getString(R.string.registrationSuccess), Toast.LENGTH_LONG).show();
                                                        finish();
                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                    }else {
                                                        dismissProgressDialog();
                                                        Toast.makeText(SignupActivity.this, getString(R.string.registrationFailure), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }else{
                                            dismissProgressDialog();
                                            Toast.makeText(SignupActivity.this, getString(R.string.registrationFailure), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else{
                                FirebaseDatabase.getInstance().getReference("Default")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dismissProgressDialog();
                                            mAuth.signOut();
                                            Toast.makeText(SignupActivity.this, getString(R.string.registrationSuccess), Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        }else{
                                            dismissProgressDialog();
                                            Toast.makeText(SignupActivity.this, getString(R.string.registrationFailure), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }


                        }else{
                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            dismissProgressDialog();
                        }
                        }
                });

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            msignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            msignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    msignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            msignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/

    protected void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    protected void dismissProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

}
