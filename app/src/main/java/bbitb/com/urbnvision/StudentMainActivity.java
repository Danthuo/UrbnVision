package bbitb.com.urbnvision;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StudentMainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = StudentMainActivity.class.getSimpleName();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //userNameCheck();
                    fragment = new StudentHomeNav();
                    break;
                case R.id.navigation_dashboard:
                    /*mTextMessage.setText(R.string.title_dashboard);
                    return true;*/
                    fragment = new StudentDashNav();
                    break;
                case R.id.navigation_notifications:
                   /* mTextMessage.setText(R.string.title_notifications);
                    return true;*/
                    fragment = new StudentNotifyNav();
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //loading the default fragment
        loadFragment(new StudentDashNav());

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){

            userDetailsCheck();
            /*//Home list fragment here since user is already logged in
            finish();
            startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dots,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {

            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), StudentSettingsActivity.class));
                break;
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            /*case R.id.changeUsername:
                *//*finish();
                startActivity(new Intent(getApplicationContext(), CompanySettingsActivity.class));*//*
                break;*/
        }

        return true;
    }


    public void userDetailsCheck(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String RegisteredUserID = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Student").child(RegisteredUserID);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Welcome to the students section");
                }else{
                    finish();
                    startActivity(new Intent(getApplicationContext(), CompanyMainActivity.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*private void deleteAccount(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        *//*FirebaseDatabase database = null;
                        DatabaseReference ref = database.getReference().child("Users");
                        ref.child(user.getUid())*//*
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(user.getUid())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.d(TAG, "Student account deleted.");
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("TAG","onComplete",task.getException());
                                }
                            }
                        });
                    }
                });

    }*/
}
