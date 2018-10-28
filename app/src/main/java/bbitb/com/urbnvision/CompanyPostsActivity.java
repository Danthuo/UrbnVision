package bbitb.com.urbnvision;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import bbitb.com.urbnvision.dialogs.PostCreateDialog;
import bbitb.com.urbnvision.dialogs.PostUpdateDialog;
import bbitb.com.urbnvision.models.Constants;
import bbitb.com.urbnvision.models.FirebaseUtils;
import bbitb.com.urbnvision.models.Post;

public class CompanyPostsActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<Post, PostHolder> mPostAdapter;
    private RecyclerView mPostRecycleView;

    private FirebaseAuth firebaseAuth;
    protected FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_posts);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostCreateDialog dialog = new PostCreateDialog();
                dialog.show(getFragmentManager(), null);
            }
        });
        init();

    }


    @Override
    protected void onStart() {
        super.onStart();
        mPostAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    private void init() {
        mPostRecycleView = findViewById(R.id.recyclerview_post);
        mPostRecycleView.hasFixedSize();
        mPostRecycleView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();
        mPostRecycleView.setAdapter(mPostAdapter);
        //mPostRecycleView.setHasFixedSize(true);
    }

    private void setupAdapter() {
        String registeredUserID = mFirebaseUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");
        Query postQuery = ref.orderByChild("company").equalTo(registeredUserID).limitToFirst(15);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(postQuery, Post.class).build();
        mPostAdapter = new FirebaseRecyclerAdapter<Post, CompanyPostsActivity.PostHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CompanyPostsActivity.PostHolder holder, int position, @NonNull final Post model) {
                holder.setNumComments(String.valueOf(model.getNumComments()));
                holder.setNumLikes(String.valueOf(model.getNumLikes()));
                holder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                DatabaseReference postOwnerRef = FirebaseDatabase.getInstance().getReference().child("Company").child(model.getCompany());
                postOwnerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String coUsername = dataSnapshot.child("username").getValue().toString();
                        holder.setUsername(coUsername);

                        String coImage = dataSnapshot.child("image").getValue().toString();
                        if (coImage != null &&  !coImage.equals("default")) {
                            Glide.with(getApplicationContext()).load(coImage).into(holder.postOwnerDisplayImageView);
                        }else if(coImage != null){
                            holder.postOwnerDisplayImageView.setImageResource(R.drawable.ic_account);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.setPostText(model.getPostText());

                /*if(model.getPostImageUrl() != null) {*/
                holder.postDisplayImageView.setVisibility(View.VISIBLE);
                DatabaseReference url_db = FirebaseDatabase.getInstance().getReference("posts").child(model.getPostId()).child("postImageUrl");

                url_db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Task<Uri> storageReference = FirebaseStorage.getInstance().getReference(model.getPostImageUrl()).getDownloadUrl();
                        String url = dataSnapshot.getValue(String.class);
                       //Log.e("ürl", url.toString());
                        if (url != null) {
                            Glide.with(getApplicationContext()).load(url).into(holder.postDisplayImageView);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                /*}else{
                    holder.postDisplayImageView.setImageBitmap(null);
                    holder.postDisplayImageView.setVisibility(View.GONE);
                }*/


                holder.postLikeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onLikeClick(model.getPostId());
                    }
                });

                try {
                    model.getPostId();
                    //Log.e("mPostEditID", model.getPostId());
                }catch (NullPointerException e){}

                holder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), CompanyPostCommentActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model);
                        startActivity(intent);
                    }
                });

                holder.editLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // Intent intent = new Intent(getApplicationContext(), PostUpdateDialog.class);
                        getIntent().putExtra(Constants.EXTRA_POST, model);
                        PostUpdateDialog dialog = new PostUpdateDialog();
                        dialog.show(getFragmentManager(), null);

                    }
                });
            }

            @Override
            public CompanyPostsActivity.PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
                return new CompanyPostsActivity.PostHolder(view);
            }

        };
    }

    private void onLikeClick(final String postId) {
        FirebaseUtils.getPostLikedRef(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //Student liked
                    FirebaseUtils.getPostRef().child(postId).child(Constants.NUM_LIKES_KEY).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            long num = (long) mutableData.getValue();
                            mutableData.setValue(num - 1);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            FirebaseUtils.getPostLikedRef(postId).setValue(null);
                        }
                    });
                } else {
                    FirebaseUtils.getPostRef().child(postId).child(Constants.NUM_LIKES_KEY).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            long num = (long) mutableData.getValue();
                            mutableData.setValue(num + 1);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            FirebaseUtils.getPostLikedRef(postId).setValue(true);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static class PostHolder extends RecyclerView.ViewHolder {
        ImageView postOwnerDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        ImageView postDisplayImageView;
        TextView postTextTextView;
        LinearLayout postLikeLayout;
        LinearLayout postCommentLayout;
        TextView postNumLikesTextView;
        TextView postNumCommentsTextView;
        LinearLayout editLinearLayout;

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

            editLinearLayout = itemView.findViewById(R.id.edit_layout);
            editLinearLayout.setVisibility(View.VISIBLE);
        }

        public void setUsername(String username) {
            postOwnerUsernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            postTimeCreatedTextView.setText(time);
        }

        public void setNumLikes(String numLikes) {
            postNumLikesTextView.setText(numLikes);
        }

        public void setNumComments(String numComments) {
            postNumCommentsTextView.setText(numComments);
        }

        public void setPostText(String text) {
            postTextTextView.setText(text);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
