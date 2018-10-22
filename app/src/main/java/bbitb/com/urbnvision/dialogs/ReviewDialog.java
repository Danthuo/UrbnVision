package bbitb.com.urbnvision.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Date;

import bbitb.com.urbnvision.models.Company;
import bbitb.com.urbnvision.CompanyReviews;
import bbitb.com.urbnvision.R;
import bbitb.com.urbnvision.models.Constants;
import bbitb.com.urbnvision.models.FirebaseUtils;
import bbitb.com.urbnvision.models.Review;
import bbitb.com.urbnvision.models.Student;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewDialog extends DialogFragment implements View.OnClickListener {


    private ProgressDialog mProgressDialog;
    private View mRootView;

    private Review mReview;

    private TextView username_tv;
    private EditText review_et;
    private Button sendReview;
    private MaterialRatingBar rating;

    private Company companyModelGlobal;

    private FirebaseAuth firebaseAuth;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mReview = new Review();
        mProgressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jDatabase = FirebaseDatabase.getInstance().getReference().child("Student").child(registeredUserID);

        //this gets the company serialized variable from CompanyReview activity
        CompanyReviews xxx = (CompanyReviews)getActivity();
        companyModelGlobal = xxx.company;

        mRootView = getActivity().getLayoutInflater().inflate(R.layout.review_dialog, null);

        username_tv = mRootView.findViewById(R.id.tv_name);
        jDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("username").getValue().toString();
                    username_tv.setText(userType);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rating = mRootView.findViewById(R.id.rate_star);
        review_et = mRootView.findViewById(R.id.et_review);
        sendReview = mRootView.findViewById(R.id.btn_send_review);

        sendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDataReview();
            }
        });
        builder.setView(mRootView);
        return builder.create();

    }

    private void insertDataReview() {
        mProgressDialog.setMessage("Sending review...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jDatabase = FirebaseDatabase.getInstance().getReference().child("Student").child(registeredUserID);

        String registeredUserEmail = currentUser.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Company").child(companyModelGlobal.getUid()).child("reviews");
        final Query postQuery = ref.orderByChild("student/email").equalTo(registeredUserEmail);

        jDatabase
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);

                        /*if(postQuery == null){*/
                        final String reviewId = FirebaseUtils.getReviewUid();
                        String text = review_et.getText().toString();

                        mReview.setStudent(student);
                        mReview.setTotalStarGiven(Math.round(rating.getRating()));
                        mReview.setReview(text);
                        mReview.setTimeStamp(new Date());
                        mReview.setReviewId(reviewId);

                        addToMyReviewsList(reviewId);
                        /*}else{
                            Toast.makeText(getActivity(), "Your review for this company already exists", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                            dismiss();

                        }*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToMyReviewsList(String reviewId) {
        FirebaseDatabase.getInstance()
                .getReference(Constants.COMPANY_KEY).child(companyModelGlobal.getUid()).child("reviews").child(reviewId)
                .setValue(mReview);
        FirebaseUtils.getMyReviewsRef().child(reviewId).setValue(true)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        dismiss();
                    }
                });
        FirebaseUtils.addToMyRecord(Constants.REVIEWS_KEY, reviewId);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance()
                .getReference(Constants.COMPANY_KEY).child(companyModelGlobal.getUid()).child("ratings");

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot ratingsSnapshot: dataSnapshot.getChildren()) {
                if(dataSnapshot.exists()){
                    //Company rat = ratingsSnapshot.getValue(Company.class);
                    String username = (String) dataSnapshot.child("username").getValue();
                    //String id = (String) dataSnapshot.child("uid").getValue();

                    Long totalAllVoters1 = (Long) dataSnapshot.child("totalVoters").getValue();
                    int totalAllVoters = totalAllVoters1.intValue();
                    String totalAllRating1 =  dataSnapshot.child("totalRating").getValue().toString();
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
                    Company rat = new Company(username, companyModelGlobal.getUid(), totalAllRating, totalAllVoters, totalRateStar1, totalRateStar2, totalRateStar3, totalRateStar4, totalRateStar5);
                    //after success, then update the rating in company node
                    updateRating(mReview, rat);
                }
                //}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }


    /**
     * this method used to update rating of company
     *
     * @param reviewModel
     * @param companyModel
     */
    private void updateRating(Review reviewModel, final Company companyModel) {
        final Company rate = new Company();
        rate.setUid(companyModel.getUid());
        rate.setUsername(companyModel.getUsername());

        //update stars
        double totalStars;
        int totalVoters = 0;
        if (reviewModel.getTotalStarGiven() == 1.0) {
            totalStars = 1.0 + (double) companyModel.getStar1();
            rate.setStar1((int) totalStars);
            rate.setStar2(companyModel.getStar2());
            rate.setStar3(companyModel.getStar3());
            rate.setStar4(companyModel.getStar4());
            rate.setStar5(companyModel.getStar5());

            totalVoters = (int) (totalStars + companyModel.getStar2() + companyModel.getStar3() + companyModel.getStar4() + companyModel.getStar5());
            if (companyModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 2.0) {
            totalStars = 1.0 + (double) companyModel.getStar2();
            rate.setStar1(companyModel.getStar1());
            rate.setStar2((int) totalStars);
            rate.setStar3(companyModel.getStar3());
            rate.setStar4(companyModel.getStar4());
            rate.setStar5(companyModel.getStar5());

            totalVoters = (int) (totalStars + companyModel.getStar1() + companyModel.getStar3() + companyModel.getStar4() + companyModel.getStar5());
            if (companyModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 3.0) {
            totalStars = 1.0 + (double) companyModel.getStar3();
            rate.setStar1(companyModel.getStar1());
            rate.setStar2(companyModel.getStar2());
            rate.setStar3((int) totalStars);
            rate.setStar4(companyModel.getStar4());
            rate.setStar5(companyModel.getStar5());

            totalVoters = (int) (totalStars + companyModel.getStar1() + companyModel.getStar2() + companyModel.getStar4() + companyModel.getStar5());
            if (companyModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 4.0) {
            totalStars = 1.0 + (double) companyModel.getStar4();
            rate.setStar1(companyModel.getStar1());
            rate.setStar2(companyModel.getStar2());
            rate.setStar3(companyModel.getStar3());
            rate.setStar4((int) totalStars);
            rate.setStar5(companyModel.getStar5());

            totalVoters = (int) (totalStars + companyModel.getStar1() + companyModel.getStar2() + companyModel.getStar3() + companyModel.getStar5());
            if (companyModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 5.0) {
            totalStars = 1.0 + (double) companyModel.getStar5();
            rate.setStar1(companyModel.getStar1());
            rate.setStar2(companyModel.getStar2());
            rate.setStar3(companyModel.getStar3());
            rate.setStar4(companyModel.getStar4());
            rate.setStar5((int) totalStars);

            totalVoters = (int) (totalStars + companyModel.getStar1() + companyModel.getStar2() + companyModel.getStar3() + companyModel.getStar4());
            if (companyModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        }

        //update rate
        int totalStar1 = rate.getStar1() * 1;
        int totalStar2 = rate.getStar2() * 2;
        int totalStar3 = rate.getStar3() * 3;
        int totalStar4 = rate.getStar4() * 4;
        int totalStar5 = rate.getStar5() * 5;

        int totalStarz1 = rate.getStar1();
        int totalStarz2 = rate.getStar2();
        int totalStarz3 = rate.getStar3();
        int totalStarz4 = rate.getStar4();
        int totalStarz5 = rate.getStar5();


        String username = companyModel.getUsername();
        String uid = companyModel.getUid();

        double sumOfStars = totalStar1 + totalStar2 + totalStar3 + totalStar4 + totalStar5;
        double totalRating = sumOfStars / (double) totalVoters;
        DecimalFormat format = new DecimalFormat(".#");
        rate.setTotalRating(Double.parseDouble(format.format(totalRating)));

        double totalAllRating = Double.parseDouble(format.format(totalRating));


        final Company ratezzz = new Company(username, uid, totalAllRating, totalVoters, totalStarz1, totalStarz2, totalStarz3, totalStarz4, totalStarz5);

        //DatabaseReference jDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.COMPANY_KEY);
        FirebaseDatabase.getInstance().getReference().child(Constants.COMPANY_KEY).child(companyModel.getUid()).child("ratings")
                .setValue(ratezzz)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgressDialog.dismiss();
                            //Toast.makeText(getContext(), "Successfully update Rating", Toast.LENGTH_SHORT).show();
                            //companyModelGlobal = rate;
                            CompanyReviews.setRatingByColor(ratezzz);
                            CompanyReviews.setupAdapter(companyModel.getUid());
                        }else{
                            mProgressDialog.dismiss();
                            //Toast.makeText(getContext(), "Failed Update Rating : " , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                /*.addOnSuccessListener(aVoid -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), "Successfully update Rating", Toast.LENGTH_SHORT).show();
                    companyModelGlobal = rate;
                    CompanyReviews.setRatingByColor(rate);
                    CompanyReviews.getAllReview(companyModel.getUid());
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed Update Rating : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
    }


    @Override
    public void onClick(View view) {

    }
}
