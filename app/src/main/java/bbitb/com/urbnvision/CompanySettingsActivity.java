package bbitb.com.urbnvision;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import bbitb.com.urbnvision.dialogs.CompanyProfileImageDialog;
import bbitb.com.urbnvision.dialogs.StudentProfileImageDialog;
import bbitb.com.urbnvision.models.Constants;

public class CompanySettingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText editTextCompanyUserName, editTextEmail, editTextCompanyName,
            editTextCompanyBio, editTextCompanyWebsite, editTextCompanyPhone ;
    private Button update;
    private TextView currentLocationTV;
    private TextView userprofilepcTv, coNameTv;
    private ImageView userpicIv;
    private Button changeLocation;

    private static final String TAG = "CompanySettings";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CompanySettingsActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //user can make map requests
            Log.d(TAG, "isServicesOK: google services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can fix it
            Log.d(TAG, "isServicesOK: a fixable error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CompanySettingsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"You cannot make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        editTextCompanyUserName = findViewById(R.id.acc_username);
        editTextEmail = findViewById(R.id.acc_email);
        editTextCompanyName = findViewById(R.id.acc_name);
        editTextCompanyBio = findViewById(R.id.acc_description);
        editTextCompanyWebsite = findViewById(R.id.acc_url);
        editTextCompanyPhone = findViewById(R.id.acc_phone);
        currentLocationTV = findViewById(R.id.acc_location);

        userpicIv = findViewById(R.id.imageView_display);
        userprofilepcTv = findViewById(R.id.edit_pic);

        coNameTv = findViewById(R.id.textView_name);

        userprofilepcTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompanyProfileImageDialog dialog  = new CompanyProfileImageDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        update = findViewById(R.id.btnUpdateProfile);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptProfileUpdate();
            }
        });

        changeLocation = findViewById(R.id.locationChange);

    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){

            companyDetailsView();
            /*//Home list fragment here since user is already logged in
            finish();
            startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));*/
        }
    }

    public void companyDetailsView(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Company").child(registeredUserID);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        String companyUserName = dataSnapshot.child("username").getValue().toString();
                        editTextCompanyUserName.setText(companyUserName);
                        coNameTv.setText(companyUserName);
                    }catch (NullPointerException e){}
                    try {
                        String companyEmail = dataSnapshot.child("email").getValue().toString();
                        editTextEmail.setText(companyEmail);
                    }catch (NullPointerException e){}
                    try {
                        String companyName = dataSnapshot.child("name").getValue().toString();
                        editTextCompanyName.setText(companyName);
                    }catch (NullPointerException e){}
                    try {
                        String companyBio = dataSnapshot.child("desc").getValue().toString();
                        editTextCompanyBio.setText(companyBio);
                    }catch (NullPointerException e){}
                    try {
                        String companyWebsite = dataSnapshot.child("website").getValue().toString();
                        editTextCompanyWebsite.setText(companyWebsite);
                    }catch (NullPointerException e){}
                    try {
                        String companyPhone = dataSnapshot.child("phone").getValue().toString();
                        editTextCompanyPhone.setText(companyPhone);
                    }catch (NullPointerException e){}

                    companyLocation();

                    String urlPhoto = dataSnapshot.child("image").getValue().toString();
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference.child(urlPhoto).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(storageReference)
                                    .into(userpicIv);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                    /*try {
                        String urlPhoto = dataSnapshot.child("profileImage").child("photoUrl").getValue().toString();
                        if(urlPhoto !=null){
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference(urlPhoto);
                            Glide.with(getApplicationContext())
                                    .load(storageReference)
                                    .into(userpicIv);
                        }
                    }catch (NullPointerException e){ }*/

                    changeLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String mapUsername = (String) coNameTv.getText();
                            Intent intent = new Intent(getApplicationContext(), CompanyMapsActivity.class);
                            intent.putExtra(Constants.EXTRA_COMPANY, mapUsername);
                            startActivity(intent);
                            //startActivity(new Intent(CompanySettingsActivity.this, CompanyMapsActivity.class));
                        }
                    });

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void companyLocation() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Company_Location_Data").child(registeredUserID);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        String companyLocation = dataSnapshot.child("address").getValue().toString();
                        currentLocationTV.setText(companyLocation);
                    }catch (NullPointerException e){}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void attemptProfileUpdate() {

        final String username = editTextCompanyUserName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String name = editTextCompanyName.getText().toString().trim();
        final String desc = editTextCompanyBio.getText().toString().trim();
        final String website = editTextCompanyWebsite.getText().toString().trim();
        final String phone = editTextCompanyPhone.getText().toString().trim();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            editTextCompanyName.setError("Please enter a username");
            editTextCompanyName.requestFocus();
            return;
        }

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference().child("Company").child(registeredUserID);
        //Company co = new Company(username, email, name, desc, website, phone);
        //Map<String, Object> values = co.toMap();
        Map<String, Object> companyUpdates = new HashMap<>();
        companyUpdates.put("username",username);
        companyUpdates.put("email", email);
        companyUpdates.put("name", name);
        companyUpdates.put("desc", desc);
        companyUpdates.put("website", website);
        companyUpdates.put("phone", phone);
        dbRef.updateChildren(companyUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CompanySettingsActivity.this, "Company details Successfully updated", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CompanySettingsActivity.this, "Company details could not be updated", Toast.LENGTH_LONG).show();
                    }
                });

    }
}
