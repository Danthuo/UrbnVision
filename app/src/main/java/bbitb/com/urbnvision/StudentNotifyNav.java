package bbitb.com.urbnvision;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import bbitb.com.urbnvision.models.Constants;
import bbitb.com.urbnvision.models.FirebaseUtils;
import bbitb.com.urbnvision.models.Post;

public class StudentNotifyNav extends Fragment {
    private View mRootView;
    private FirebaseRecyclerAdapter<Post, StudentNotifyNav.PostHolder> mPostAdapter;
    private RecyclerView mPostRecycleView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        mRootView = inflater.inflate(R.layout.fragment_notify, null);

        init();
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    private void init(){
        mPostRecycleView = mRootView.findViewById(R.id.recyclerview_post);
        mPostRecycleView.hasFixedSize();
        mPostRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupAdapter();
        mPostRecycleView.setAdapter(mPostAdapter);
        //mPostRecycleView.setHasFixedSize(true);
    }

    /*@GlideModule
    public class MyAppGlideModule extends AppGlideModule {

        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
            // Register FirebaseImageLoader to handle StorageReference
            registry.append(StorageReference.class, InputStream.class,
                    new FirebaseImageLoader.Factory());
        }
    }*/

    private void setupAdapter() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");
        Query postQuery = ref.orderByKey();

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(postQuery, Post.class).build();
        mPostAdapter = new FirebaseRecyclerAdapter<Post, StudentNotifyNav.PostHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final StudentNotifyNav.PostHolder holder, int position, @NonNull final Post model) {
                try {
                    holder.setNumComments(String.valueOf(model.getNumComments()));
                }catch (NullPointerException e){
                }

                try {
                    holder.setNumLikes(String.valueOf(model.getNumLikes()));
                }catch (NullPointerException e){
                }

                try {
                    holder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                }catch (NullPointerException e){
                }

                DatabaseReference postOwnerRef = FirebaseDatabase.getInstance().getReference().child("Company").child(model.getCompany());
                postOwnerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String coUsername = dataSnapshot.child("username").getValue().toString();
                        holder.setUsername(coUsername);

                        String coImage = dataSnapshot.child("image").getValue().toString();
                        if (coImage != null &&  !coImage.equals("default")) {
                            Glide.with(getContext()).load(coImage).into(holder.postOwnerDisplayImageView);
                        }else if(coImage != null){
                            holder.postOwnerDisplayImageView.setImageResource(R.drawable.ic_account);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                try {
                    holder.setPostText(model.getPostText());
                }catch (NullPointerException e){
                }

                holder.postDisplayImageView.setVisibility(View.VISIBLE);
                DatabaseReference url_db = FirebaseDatabase.getInstance().getReference("posts").child(model.getPostId()).child("postImageUrl");

                url_db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Task<Uri> storageReference = FirebaseStorage.getInstance().getReference(model.getPostImageUrl()).getDownloadUrl();
                        String url = dataSnapshot.getValue(String.class);
                        //Log.e("ürl", url.toString());
                        if (url != null) {
                            Glide.with(getContext()).load(url).into(holder.postDisplayImageView);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                /*try {
                if(model.getPostImageUrl() != null) {
                        holder.postDisplayImageView.setVisibility(View.VISIBLE);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(model.getPostImageUrl());
                        Glide.with(getContext())
                                .load(storageReference)
                                .into(holder.postOwnerDisplayImageView);
                    //Picasso.with(getContext()).load(String.valueOf(storageReference)).into(holder.postOwnerDisplayImageView);
                }else{
                    holder.postDisplayImageView.setImageBitmap(null);
                    holder.postDisplayImageView.setVisibility(View.GONE);
                }
                }catch (NullPointerException e){}*/

                holder.postLikeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onLikeClick(model.getPostId());
                    }
                });

                holder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CompanyPostCommentActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public StudentNotifyNav.PostHolder onCreateViewHolder(ViewGroup parent, int viewType){

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_post, parent, false);
                return new StudentNotifyNav.PostHolder(view);
            }

        };
    }

    private void onLikeClick(final String postId) {
        FirebaseUtils.getPostLikedRef(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            //Student liked
                            FirebaseUtils.getPostRef()
                                    .child(postId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num - 1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(postId)
                                                    .setValue(null);
                                        }
                                    });
                        }else{
                            FirebaseUtils.getPostRef()
                                    .child(postId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num+1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(postId)
                                                    .setValue(true);

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public static class PostHolder extends RecyclerView.ViewHolder{
        ImageView postOwnerDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        ImageView postDisplayImageView;
        TextView postTextTextView;
        LinearLayout postLikeLayout;
        LinearLayout postCommentLayout;
        TextView postNumLikesTextView;
        TextView postNumCommentsTextView;
        public PostHolder(View itemView) {
            super(itemView);
            postOwnerDisplayImageView = itemView.findViewById(R.id.profile_image);
            postOwnerUsernameTextView = itemView.findViewById(R.id.tv_post_username);
            postTimeCreatedTextView = itemView.findViewById(R.id.tv_time);
            postDisplayImageView = itemView.findViewById(R.id.iv_post_display);
            postLikeLayout = itemView.findViewById(R.id.like_layout);
            postCommentLayout = itemView.findViewById(R.id.comment_layout);
            postNumLikesTextView = itemView.findViewById(R.id.tv_likes);
            postNumCommentsTextView = itemView.findViewById(R.id.tv_comments);
            postTextTextView = itemView.findViewById(R.id.tv_post_text);
        }
        public void setUsername(String username){
            postOwnerUsernameTextView.setText(username);
        }
        public void setTime(CharSequence time){
            postTimeCreatedTextView.setText(time);
        }
        public void setNumLikes(String numLikes){
            postNumLikesTextView.setText(numLikes);
        }
        public void setNumComments(String numComments){
            postNumCommentsTextView.setText(numComments);
        }
        public void setPostText(String text){
            postTextTextView.setText(text);
        }
    }

}
