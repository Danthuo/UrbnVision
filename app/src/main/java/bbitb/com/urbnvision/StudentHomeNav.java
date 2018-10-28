package bbitb.com.urbnvision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import bbitb.com.urbnvision.models.Company;
import bbitb.com.urbnvision.models.Constants;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class StudentHomeNav extends Fragment {

    private static final String TAG = "StudentHomeNav";
    private RecyclerView mCompanies;
    //private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Company, CompanyViewHolder> mCompaniesAdapter;
    private Activity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View view = inflater.inflate(R.layout.fragment_home, null);

        //setTitle("Companies");

        //mDatabase = FirebaseDatabase.getInstance().getReference().child("Company");
        //mDatabase.keepSynced(true);

        mCompanies = view.findViewById(R.id.RecyclerView);

        final DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference().child("Company");
        Query companyQuery = companyRef.orderByKey();
        mCompanies.hasFixedSize();
        mCompanies.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions companyOptions = new FirebaseRecyclerOptions.Builder<Company>().setQuery(companyQuery, Company.class).build();
        mCompaniesAdapter = new FirebaseRecyclerAdapter<Company, StudentHomeNav.CompanyViewHolder>(companyOptions){
            @Override
            protected  void onBindViewHolder(final StudentHomeNav.CompanyViewHolder holder, final int position, final Company model){
                //final Company company = mCompaniesAdapter.getItem(position);
                final Intent intent = new Intent(getContext(),StudentCompanyView.class);

               companyRef.child(model.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String coUsername = dataSnapshot.child("username").getValue().toString();
                        holder.setUsername(coUsername);

                        String bio = dataSnapshot.child("desc").getValue().toString();
                        holder.setDesc(bio);

                        String coImage = dataSnapshot.child("image").getValue().toString();
                        if (coImage != null &&  !coImage.equals("default") && mActivity != null) {
                            Glide.with(mActivity).load(coImage).into(holder.companyDisplayImageView);
                        }else if(coImage != null){
                            holder.companyDisplayImageView.setImageResource(R.drawable.ic_account);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //for the student company view
                try{
                    //intent.putExtra("Username", company.getUsername());
                    model.getUsername();
                }catch (NullPointerException e){
                    Log.e(TAG, "companyAdapter :- "+ e.getMessage());
                }

                try{
                    //intent.putExtra("Email", company.getEmail());
                    holder.setCompanyEmail(model.getEmail());
                }catch (NullPointerException e){Log.e(TAG, "companyAdapter :- "+ e.getMessage());}

                try{
                    model.getDesc();
                }catch (NullPointerException e){
                    Log.e(TAG, "companyAdapter :- "+ e.getMessage());
                }

                try{
                    //intent.putExtra("Phone", company.getPhone());
                    model.getPhone();
                }catch (NullPointerException e){Log.e(TAG, "companyAdapter :- "+ e.getMessage());}

                try{
                    //intent.putExtra("Website", company.getUrl());
                    model.getImage();
                }catch (NullPointerException e){Log.e(TAG, "companyAdapter :- "+ e.getMessage());}


                try {
                   model.getUid();
                }catch (NullPointerException e){}

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       intent.putExtra(Constants.EXTRA_COMPANY, model);
                        startActivity(intent);
                    }
                });
                holder.getOverflow().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(holder.overflow);
                    }
                });

                try{
                holder.totalStarRating.setRating(Float.parseFloat(String.valueOf(model.getTotalRating())));
                }catch (NullPointerException e){

                }
            }

            @Override
            public StudentHomeNav.CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_home_card, parent, false);

                return new StudentHomeNav.CompanyViewHolder(view);
            }
        };

        mCompanies.setAdapter(mCompaniesAdapter);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        mActivity = getActivity();
        mCompaniesAdapter.startListening();
    }

    @Override
    public void onStop(){
        super.onStop();
        mActivity = null;
        mCompaniesAdapter.stopListening();
    }


    public static class CompanyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView overflow;
        ImageView companyDisplayImageView;
        TextView companyNameTextView;
        TextView companyNotificationsTextView;
        TextView companyDescriptionTextView;

        TextView companyEmail;

        MaterialRatingBar totalStarRating;

        public CompanyViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            overflow = itemView.findViewById(R.id.overflow);
            companyDisplayImageView = itemView.findViewById(R.id.thumbnail);
            companyNameTextView = itemView.findViewById(R.id.co_name);
            companyNotificationsTextView = itemView.findViewById(R.id.noti_status);
            companyDescriptionTextView = itemView.findViewById(R.id.co_desc);

            companyEmail = itemView.findViewById(R.id.co_email);

            totalStarRating = itemView.findViewById(R.id.total_star_rating);
        }

        public ImageView getOverflow() {
            return overflow;
        }

        public void setCompanyEmail(String email) {
            companyEmail.setText(email);
        }

        public  void setUsername(String title){
            companyNameTextView.setText(title);
        }
        public void setDesc(String desc){
            companyDescriptionTextView.setText(desc);
        }
        public void setNotifications(String notifications){
            companyNotificationsTextView.setText(notifications);
        }

    }

    private void showPopupMenu(View view){
        //inflate menu
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_company_card, popupMenu.getMenu());
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
                case R.id.action_rate_company:
                    Toast.makeText(getContext(), "Functionality yet to be added", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_locate_company:
                    Toast.makeText(getContext(), "Functionality yet to be added", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_company_website:
                    Toast.makeText(getContext(), "Functionality yet to be added", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    Toast.makeText(getContext(), "Functionality yet to be added", Toast.LENGTH_SHORT).show();

            }
            return false;
        }
    }
}
