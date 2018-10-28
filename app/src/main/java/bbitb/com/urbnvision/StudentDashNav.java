package bbitb.com.urbnvision;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StudentDashNav extends Fragment{

    private FirebaseAuth firebaseAuth;
    private TextView username_text;

    public ImageView more_image, locate_companies, userDisplayImageView, editProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        View view = inflater.inflate(R.layout.fragment_dash, null);
        firebaseAuth = FirebaseAuth.getInstance();
        username_text = (TextView) view.findViewById(R.id.username);

        editProfile = view.findViewById(R.id.editprofile_img);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), StudentSettingsActivity.class));
            }
        });

        more_image = view.findViewById(R.id.more_image);
        locate_companies = view.findViewById(R.id.locatecompanies_image);
        more_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        locate_companies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StudentMapsActivity.class);
                        startActivity(intent);
            }
        });

        userDisplayImageView = view.findViewById(R.id.profile_pic);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            userNameCheck();
        }
    }

    public void userNameCheck(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Student").child(registeredUserID);
        /*String urlPhoto;
        Student user = new Student();
        urlPhoto = user.getPhotoUrl();*/
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("username").getValue().toString();
                    username_text.setText(userType);
                }

                String urlPhoto;
                try {
                    urlPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                    if (urlPhoto != null && !urlPhoto.equals("default")) {
                        Glide.with(getContext()).load(urlPhoto).into(userDisplayImageView);
                    }else if(urlPhoto != null){
                        userDisplayImageView.setImageResource(R.drawable.ic_account);
                    }
                }catch (NullPointerException e){ }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void showPopupMenu(View view){
        //inflate menu
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_student_profile, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popupMenu.show();
    }

    /*Click listener for popup menu items*/
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
        public  MyMenuItemClickListener(){

        }

        @Override
        public  boolean onMenuItemClick(MenuItem menuItem){
            switch (menuItem.getItemId()){
               /* case R.id.action_edit_image:
                    startActivity(new Intent(getContext(), StudentSettingsActivity.class));
                    *//*StudentProfileImageDialog dialog  = new StudentProfileImageDialog();
                    dialog.show(getActivity().getFragmentManager(), null);*//*
                    return true;*/
                case R.id.action_account_settings:
                    startActivity(new Intent(getContext(), StudentSettingsActivity.class));
                    return true;
                /*case R.id.action_notification_subs:
                    Toast.makeText(getContext(), "Functionality yet to be added", Toast.LENGTH_SHORT).show();
                    return true;*/
                default:
                    Toast.makeText(getContext(), "Functionality yet to be added", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

}
