package bbitb.com.urbnvision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import bbitb.com.urbnvision.dialogs.ReviewDialog;
import bbitb.com.urbnvision.models.Constants;
import bbitb.com.urbnvision.models.Review;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CompanyReviews extends AppCompatActivity{

    private static FirebaseRecyclerAdapter<Review, ReviewsHolder> mReviewsAdapter;
    private RecyclerView mReviewsRecycleView;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();

    public Company company;
    private static ConstraintLayout constraintLayout1;
    private ConstraintLayout constraintLayout2;
    private ConstraintLayout constraintLayout3;
    private ConstraintLayout constraintLayout4;
    private ConstraintLayout constraintLayout5;
    private static LinearLayout linearPercentage1;
    private static LinearLayout linearPercentage2;
    private static LinearLayout linearPercentage3;
    private static LinearLayout linearPercentage4;
    private static LinearLayout linearPercentage5;
    private static TextView total_people_rated;
    private static TextView total_number_rating;
    private static MaterialRatingBar totalStarRating;

    private static RecyclerView rvReview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_reviews);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewDialog dialog  = new ReviewDialog();
                dialog.show(getFragmentManager(), null);
            }
        });

        constraintLayout1 = findViewById(R.id.constrain_layout_1);
        constraintLayout2 = findViewById(R.id.constrain_layout_2);
        constraintLayout3 = findViewById(R.id.constrain_layout_3);
        constraintLayout4 = findViewById(R.id.constrain_layout_4);
        constraintLayout5 = findViewById(R.id.constrain_layout_5);

        linearPercentage1 = findViewById(R.id.percentage_1);
        linearPercentage2 = findViewById(R.id.percentage_2);
        linearPercentage3 = findViewById(R.id.percentage_3);
        linearPercentage4 = findViewById(R.id.percentage_4);
        linearPercentage5 = findViewById(R.id.percentage_5);

        total_people_rated = findViewById(R.id.tv_total_people_rated);
        total_number_rating = findViewById(R.id.tv_total_number_rating);
        totalStarRating =findViewById(R.id.total_star_rating);

        //rvReview = findViewById(R.id.rv_review);

        Intent intent = getIntent();
        company = (Company) intent.getSerializableExtra(Constants.EXTRA_COMPANY);
        try {
            setupAdapter(company.getUid());
        }catch (NullPointerException e){}
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mReviewsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mReviewsAdapter.stopListening();
    }


    private void initView() {


        mReviewsRecycleView = findViewById(R.id.rv_review);
        mReviewsRecycleView.hasFixedSize();
        mReviewsRecycleView.setLayoutManager(new LinearLayoutManager(this));


        mReviewsRecycleView.setAdapter(mReviewsAdapter);
        //this method used to get the width of view
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Count Width Of View");
        progressDialog.show();
        handler.postDelayed(
                new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();


                DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference(Constants.COMPANY_KEY).child(company.getUid());
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


                //setRatingByColor(company);

            }
        }, 3000);

    }

    public static void setupAdapter(String idCompany) {
        //progressBar.setVisibility(View.VISIBLE);
        //rvReview.setVisibility(View.GONE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Company").child(idCompany).child("reviews");
        Query reviewQuery = ref.orderByKey();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Review>().setQuery(reviewQuery, Review.class).build();
        mReviewsAdapter = new FirebaseRecyclerAdapter<Review, ReviewsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewsHolder holder, int position, @NonNull Review model) {
                try {
                    holder.setUsername(model.getStudent().getUsername());
                }catch (NullPointerException e){}

                try{
                    holder.setTotalStarGiven(Float.parseFloat(String.valueOf(model.getTotalStarGiven())));
                }catch (NullPointerException e){}

                try {
                    holder.setTimeStamp(String.valueOf(model.getTimeStamp()));
                }catch (NullPointerException e){}

                try {
                    holder.setReview(model.getReview());
                }catch (NullPointerException e){}

                //rvReview.setVisibility(View.VISIBLE);

            }

            @NonNull
            @Override
            public CompanyReviews.ReviewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_review, parent, false);

                return new CompanyReviews.ReviewsHolder(view);
            }
        };
    }



    public static class ReviewsHolder extends RecyclerView.ViewHolder{

        TextView tv_ReviewUsername;
        MaterialRatingBar rate;
        TextView tvTglRating;
        TextView tvDescReview;

        public ReviewsHolder(View itemView) {
            super(itemView);

            tv_ReviewUsername = itemView.findViewById(R.id.tv_user);
            rate = itemView.findViewById(R.id.total_star_rating);
            tvTglRating = itemView.findViewById(R.id.tv_tgl_rating);
            tvDescReview = itemView.findViewById(R.id.tv_desc_review);

        }

        public void setUsername(String username){
            tv_ReviewUsername.setText(username);
        }
        public void setTimeStamp(String date){
            tvTglRating.setText(date);
        }
        public void setReview(String text){
            tvDescReview.setText(text);
        }
        public void setTotalStarGiven(Float rating){
            rate.setRating(rating);}
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

        double sumBintang = totalFirstStar +
                totalSecondStar +
                totalThirdStar +
                totalFourthStar +
                totalFifthStar;

        double rating = (sumBintang / votersInDouble);
        DecimalFormat format = new DecimalFormat(".#");

        total_number_rating.setText(String.valueOf(format.format(rating)));

        totalStarRating.setRating(Float.parseFloat(String.valueOf(rating)));
        total_people_rated.setText(String.valueOf(totalAllVoters) + " total");


    }
}
