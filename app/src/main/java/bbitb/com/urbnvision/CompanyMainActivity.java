package bbitb.com.urbnvision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DecimalFormat;

import bbitb.com.urbnvision.models.Company;
import bbitb.com.urbnvision.models.Constants;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CompanyMainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    protected FirebaseUser mFirebaseUser;

    private ImageView settingsIcon;

    private ValueEventListener mUserValueEventListener;
    private DatabaseReference mUserRef;

    private RelativeLayout rate, locate, subscribe;

    private static ConstraintLayout constraintLayout1;
    private static LinearLayout linearPercentage1;
    private static LinearLayout linearPercentage2;
    private static LinearLayout linearPercentage3;
    private static LinearLayout linearPercentage4;
    private static LinearLayout linearPercentage5;
    private static TextView total_people_rated;
    private static TextView total_number_rating;
    private static MaterialRatingBar totalStarRating;

    private ProgressDialog progressDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_main_new);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        rate = findViewById(R.id.relRate);
        locate = findViewById(R.id.relLocate);
        subscribe = findViewById(R.id.relSubscribe);



        init();

        constraintLayout1 = findViewById(R.id.constrain_layout_1);
        linearPercentage1 = findViewById(R.id.percentage_1);
        linearPercentage2 = findViewById(R.id.percentage_2);
        linearPercentage3 = findViewById(R.id.percentage_3);
        linearPercentage4 = findViewById(R.id.percentage_4);
        linearPercentage5 = findViewById(R.id.percentage_5);

        total_people_rated = findViewById(R.id.tv_total_people_rated);
        total_number_rating = findViewById(R.id.tv_total_number_rating);
        totalStarRating =findViewById(R.id.total_star_rating);
        initView();

    }

    private void initView() {

        //this method used to get the width of view
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();

        String registeredUserID = mFirebaseUser.getUid();
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference(Constants.COMPANY_KEY).child(registeredUserID);
        ratingsRef.child("ratings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot ratingsSnapshot: dataSnapshot.getChildren()) {
                if(dataSnapshot.exists()){
                    //Company rat = ratingsSnapshot.getValue(Company.class);
                    String username = (String) dataSnapshot.child("username").getValue();
                    String id = (String) dataSnapshot.child("uid").getValue();
                    Long totalAllVoters1 = (Long) dataSnapshot.child("totalVoters").getValue();
                    int totalAllVoters = totalAllVoters1.intValue();
                    String totalAllRating1 = dataSnapshot.child("totalRating").getValue().toString();
                    double totalAllRating = Double.parseDouble(totalAllRating1);
                    Long totalRateStar11 = (Long) dataSnapshot.child("star1").getValue();
                    int totalRateStar1 = totalRateStar11.intValue();
                    Long totalRateStar22 = (Long) dataSnapshot.child("star2").getValue();
                    int totalRateStar2 = totalRateStar22.intValue();
                    Long totalRateStar33 = (Long) dataSnapshot.child("star3").getValue();
                    int totalRateStar3 = totalRateStar33.intValue();
                    Long totalRateStar44 = (Long) dataSnapshot.child("star4").getValue();
                    int totalRateStar4 = totalRateStar44.intValue();
                    Long totalRateStar55 = (Long) dataSnapshot.child("star5").getValue();
                    int totalRateStar5 = totalRateStar55.intValue();
                    Company rat = new Company(username, id, totalAllRating, totalAllVoters, totalRateStar1, totalRateStar2, totalRateStar3, totalRateStar4, totalRateStar5);
                    setRatingByColor(rat);}
                //}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        }
        }, 3000);
    }

    public static void setRatingByColor(Company companyModel) {
        int widthView = constraintLayout1.getWidth();
        int totalAllVoters = companyModel.getTotalVoters();
        int totalRateStar1 = companyModel.getStar1();
        int totalRateStar2 = companyModel.getStar2();
        int totalRateStar3 = companyModel.getStar3();
        int totalRateStar4 = companyModel.getStar4();
        int totalRateStar5 = companyModel.getStar5();

        //convert to double
        double votersInDouble = (double) totalAllVoters;


        //RATING STAR 1
        double star1 = (double) totalRateStar1;
        double sum1 = (star1 / votersInDouble);
        int rating1 = (int) (sum1 * widthView);
        ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(rating1, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(0, 5, 0, 5);
        linearPercentage1.setBackgroundColor(Color.parseColor("#ff6f31"));
        linearPercentage1.setLayoutParams(layoutParams1);

        //RATING STAR 2
        double star2 = (double) totalRateStar2;
        double sum2 = (star2 / votersInDouble);
        int rating2 = (int) (sum2 * widthView);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(rating2, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams2.setMargins(0, 5, 0, 5);
        linearPercentage2.setBackgroundColor(Color.parseColor("#ff9f02"));
        linearPercentage2.setLayoutParams(layoutParams2);

        //RATING STAR 3
        double star3 = (double) totalRateStar3;
        double sum3 = (star3 / votersInDouble);
        int rating3 = (int) (sum3 * widthView);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(rating3, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams3.setMargins(0, 5, 0, 5);
        linearPercentage3.setBackgroundColor(Color.parseColor("#ffcf02"));
        linearPercentage3.setLayoutParams(layoutParams3);

        //RATING STAR 4
        double star4 = (double) totalRateStar4;
        double sum4 = (star4 / votersInDouble);
        int rating4 = (int) (sum4 * widthView);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(rating4, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams4.setMargins(0, 5, 0, 5);
        linearPercentage4.setBackgroundColor(Color.parseColor("#9ace6a"));
        linearPercentage4.setLayoutParams(layoutParams4);

        //RATING STAR 5
        double star5 = (double) totalRateStar5;
        double sum5 = (star5 / votersInDouble);
        int rating5 = (int) (sum5 * widthView);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(rating5, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams5.setMargins(0, 5, 0, 5);
        linearPercentage5.setBackgroundColor(Color.parseColor("#57bb8a"));
        linearPercentage5.setLayoutParams(layoutParams5);

        // show ratings based on figures
        int totalFirstStar = totalRateStar1 * 1;
        int totalSecondStar = totalRateStar2 * 2;
        int totalThirdStar = totalRateStar3 * 3;
        int totalFourthStar = totalRateStar4 * 4;
        int totalFifthStar = totalRateStar5 * 5;

        double totalStars = totalFirstStar +
                totalSecondStar +
                totalThirdStar +
                totalFourthStar +
                totalFifthStar;

        double rating = (totalStars / votersInDouble);
        DecimalFormat format = new DecimalFormat(".#");

        total_number_rating.setText(String.valueOf(format.format(rating)));

        totalStarRating.setRating(Float.parseFloat(String.valueOf(rating)));
        total_people_rated.setText(String.valueOf(totalAllVoters) + " total");


    }

    private void init() {
        if(firebaseAuth != null){
            String registeredUserID = mFirebaseUser.getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Company").child(registeredUserID);
        }

        if(mUserRef != null){
            settingsIcon = findViewById(R.id.settings_icon);
            settingsIcon.setVisibility(View.VISIBLE);
            settingsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   showPopupMenu(view);
                }
            });
        }

        //Gets the company address
        companyLocation();

        final TextView mEmailTextView = findViewById(R.id.email);
        final TextView mPhoneTextView = findViewById(R.id.phone);
        final TextView mWebsiteTextView = findViewById(R.id.website);
        final TextView mFacebookTextView = findViewById(R.id.facebook);
        final TextView mAboutTextView = findViewById(R.id.about);
        final TextView mNameTextView = findViewById(R.id.textView_name);
        final ImageView mDisplayImageView = findViewById(R.id.imageView_display);

        mUserValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    //final Company company = dataSnapshot.getValue(Company.class);
                    String username, email, phone, website, about, image;
                    try{
                      username = dataSnapshot.child("username").getValue().toString();
                     mNameTextView.setText(username);
                    }catch (NullPointerException e){}
                    try {
                        email = dataSnapshot.child("email").getValue().toString();
                        mEmailTextView.setText(email);
                    }catch (NullPointerException e){}
                    try {
                        phone = dataSnapshot.child("phone").getValue().toString();
                        mPhoneTextView.setText(phone);
                    }catch (NullPointerException e){}
                    try {
                        website = dataSnapshot.child("website").getValue().toString();
                        mWebsiteTextView.setText(website);
                    }catch (NullPointerException e){}
                    try {
                        about = dataSnapshot.child("desc").getValue().toString();
                        mAboutTextView.setText(about);
                    }catch (NullPointerException e){}

                    try {
                        image = dataSnapshot.child("image").getValue().toString();
                        if (image != null && !image.equals("default")) {
                            Glide.with(getApplicationContext()).load(image).into(mDisplayImageView);
                        }else if(image != null){
                            mDisplayImageView.setImageResource(R.drawable.ic_account);
                        }

                    }catch (NullPointerException e){ }

                    //company.getUid();
                    rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           /* Intent intent = new Intent(getApplicationContext(), CompanyReviews.class);
                            intent.putExtra(Constants.EXTRA_COMPANY, company);
                            startActivity(intent);*/
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUserRef != null){
            mUserRef.addValueEventListener(mUserValueEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mUserRef != null){
            mUserRef.removeEventListener(mUserValueEventListener);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

    }


    private void showPopupMenu(View view){
        //inflate menu
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.company_set, popupMenu.getMenu());
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
                case R.id.add_opportunity:
                    Intent intent = new Intent(getApplicationContext(), CompanyPostsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), CompanySettingsActivity.class));
                    return true;
                case R.id.logout:
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    return true;
                default:
                    Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    private void companyLocation() {
        final TextView tvAddress = findViewById(R.id.tv_address);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Company_Location_Data").child(registeredUserID);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        String companyLocation = dataSnapshot.child("address").getValue().toString();
                        tvAddress.setText(companyLocation);
                    }catch (NullPointerException e){}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
