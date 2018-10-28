package bbitb.com.urbnvision;

import android.content.Intent;
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

import bbitb.com.urbnvision.dialogs.StudentProfileImageDialog;

public class StudentSettingsActivity extends AppCompatActivity {

    TextView userprofilepcTv, nameTv;
    ImageView userpicIv;
    EditText stud_email, stud_username, stud_name, stud_bio ,stud_website, stud_phone;
    Button saveDetails;

    private FirebaseAuth firebaseAuth;
    private static final String TAG = "StudentSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_settings);

        firebaseAuth = FirebaseAuth.getInstance();

        nameTv = findViewById(R.id.textView_name);
        userprofilepcTv = findViewById(R.id.edit_pic);
        userpicIv = findViewById(R.id.imageView_display);
        stud_email =findViewById(R.id.acc_email);
        stud_username = findViewById(R.id.acc_username);
        stud_name = findViewById(R.id.acc_name);
        stud_bio = findViewById(R.id.acc_description);
        stud_website = findViewById(R.id.acc_url);
        stud_phone = findViewById(R.id.acc_phone);

        saveDetails = findViewById(R.id.btnUpdateProfile);
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptProfileUpdate();
            }
        });

        userprofilepcTv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentProfileImageDialog dialog  = new StudentProfileImageDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        user_profileData();

    }

    private void user_profileData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference studentDatabase = FirebaseDatabase.getInstance().getReference().child("Student").child(registeredUserID);

        studentDatabase
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: Student id :- "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String email, name, username, phone, website, bio;

                        try {
                            email = dataSnapshot.child("email").getValue().toString();
                            stud_email.setText(email);
                        }catch (NullPointerException e){ }


                        try {
                            name = dataSnapshot.child("name").getValue().toString();
                            stud_name.setText(name);
                        }catch (NullPointerException e){ }

                        try {
                            username = dataSnapshot.child("username").getValue().toString();
                            stud_username.setText(username);
                            nameTv.setText(username);
                        }catch (NullPointerException e){ }

                        try {
                            website = dataSnapshot.child("website").getValue().toString();
                            stud_website.setText(website);
                        }catch (NullPointerException e){ }

                        try {
                            bio = dataSnapshot.child("bio").getValue().toString();
                            stud_bio.setText(bio);
                        }catch (NullPointerException e){ }

                        try {
                            phone = dataSnapshot.child("phone").getValue().toString();
                            stud_phone.setText(phone);
                        }catch (NullPointerException e){ }

                        String urlPhoto;
                        try {
                            urlPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                            if (urlPhoto != null && !urlPhoto.equals("default")) {
                                Glide.with(getApplicationContext()).load(urlPhoto).into(userpicIv);
                            }else if(urlPhoto != null){
                                userpicIv.setImageResource(R.drawable.ic_account);
                            }
                        }catch (NullPointerException e){ }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void attemptProfileUpdate() {

        final String username = stud_username.getText().toString().trim();
        final String email = stud_email.getText().toString().trim();
        final String name = stud_name.getText().toString().trim();
        final String bio = stud_bio.getText().toString().trim();
        final String website = stud_website.getText().toString().trim();
        final String phone = stud_phone.getText().toString().trim();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            stud_username.setError("Please enter a username");
            stud_username.requestFocus();
            return;
        }

        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference().child("Student").child(registeredUserID);
        //Company co = new Company(username, email, name, desc, website, phone);
        //Map<String, Object> values = co.toMap();
        Map<String, Object> studentUpdates = new HashMap<>();
        studentUpdates.put("username",username);
        studentUpdates.put("email", email);
        studentUpdates.put("name", name);
        studentUpdates.put("bio", bio);
        studentUpdates.put("website", website);
        studentUpdates.put("phone", phone);
        dbRef.updateChildren(studentUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentSettingsActivity.this, "Student details Successfully updated", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentSettingsActivity.this, "Student details could not be updated", Toast.LENGTH_LONG).show();
                    }
                });

    }

}
