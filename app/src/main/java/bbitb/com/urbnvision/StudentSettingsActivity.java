package bbitb.com.urbnvision;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import bbitb.com.urbnvision.dialogs.StudentProfileImageDialog;

public class StudentSettingsActivity extends AppCompatActivity {

    TextView userprofilepcTv;
    ImageView userpicIv;
    EditText stud_email, stud_username, stud_name, stud_bio ,stud_website, stud_phone;

    private FirebaseAuth firebaseAuth;
    private static final String TAG = "StudentSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_settings);

        firebaseAuth = FirebaseAuth.getInstance();

        userprofilepcTv = findViewById(R.id.edit_pic);
        userpicIv = findViewById(R.id.profile_pic);
        stud_email =findViewById(R.id.acc_email);
        stud_username = findViewById(R.id.acc_username);
        stud_name = findViewById(R.id.acc_name);
        stud_bio = findViewById(R.id.acc_description);
        stud_website = findViewById(R.id.acc_url);
        stud_phone = findViewById(R.id.acc_phone);


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
                        String email, name, username, phone, urlPhoto;

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
                        }catch (NullPointerException e){ }

                        try {
                            phone = dataSnapshot.child("phone").getValue().toString();
                            stud_phone.setText(phone);
                        }catch (NullPointerException e){ }

                        try {
                            urlPhoto = dataSnapshot.child("profileImage").child("photoUrl").getValue().toString();
                            if(urlPhoto !=null){
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(urlPhoto);
                                Glide.with(getApplicationContext())
                                        .load(storageReference)
                                        .into(userpicIv);
                            }
                        }catch (NullPointerException e){ }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
