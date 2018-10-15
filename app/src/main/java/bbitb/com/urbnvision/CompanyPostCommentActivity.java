package bbitb.com.urbnvision;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import bbitb.com.urbnvision.models.Comment;
import bbitb.com.urbnvision.models.Constants;
import bbitb.com.urbnvision.models.FirebaseUtils;
import bbitb.com.urbnvision.models.Post;
import bbitb.com.urbnvision.models.Student;

public class CompanyPostCommentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BUNDLE_COMMENT = "comment";
    private Post mPost;
    private EditText mCommentEditTextView;
    private Comment mComment;
    FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        firebaseAuth = FirebaseAuth.getInstance();

        if(savedInstanceState != null){
            mComment = (Comment)savedInstanceState.getSerializable(BUNDLE_COMMENT);
        }

        Intent intent = getIntent();
        mPost = (Post)intent.getSerializableExtra(Constants.EXTRA_POST);

        init();
        initPost();
        initCommentSection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        commentAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentAdapter.stopListening();
    }

    private void initCommentSection() {
        RecyclerView commentRecyclerView = findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(CompanyPostCommentActivity.this));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("comments").child(mPost.getPostId());
        Query postQuery = ref.orderByKey();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(postQuery, Comment.class).build();

        commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentHolder holder, int position, @NonNull final Comment model) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Student").child(model.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            holder.setUsername(dataSnapshot.child("username").getValue().toString());
                        }catch (NullPointerException e){}

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                try{
                    holder.setComment(model.getComment());
                }catch (NullPointerException e){}

                try{
                    holder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                }catch (NullPointerException e){}


                try{
                    Glide.with(CompanyPostCommentActivity.this)
                            .load(model.getStudent().getPhotoUrl())
                            .into(holder.commentOwnerDisplay);
                }catch (NullPointerException e){}

            }
            @NonNull
            @Override
            public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_comment, parent, false);

                return new CommentHolder(view);
            }
        };


        commentRecyclerView.setAdapter(commentAdapter);
    }

    private void initPost() {
        ImageView postOwnerDisplayImageView = findViewById(R.id.profile_image);
        TextView postOwnerUsernameTextView = findViewById(R.id.tv_post_username);
        TextView postTimeCreatedTextView = findViewById(R.id.tv_time);
        ImageView postDisplayImageView = findViewById(R.id.iv_post_display);
        LinearLayout postLikeLayout = findViewById(R.id.like_layout);
        LinearLayout postCommentLayout = findViewById(R.id.comment_layout);
        TextView postNumLikesTextView = findViewById(R.id.tv_likes);
        TextView postNumCommentsTextView = findViewById(R.id.tv_comments);
        TextView postTextTextView = findViewById(R.id.tv_post_text);

        try {
            postOwnerUsernameTextView.setText(mPost.getUser().getUsername());
        }catch (NullPointerException e){}

        try {
            postTimeCreatedTextView.setText(DateUtils.getRelativeTimeSpanString(mPost.getTimeCreated()));
        }catch (NullPointerException e){}

        try {
            postTextTextView.setText(mPost.getPostText());
        }catch (NullPointerException e){}

        try {
            postNumLikesTextView.setText(String.valueOf(mPost.getNumLikes()));
        }catch (NullPointerException e){}

        try {
            postNumCommentsTextView.setText(String.valueOf(mPost.getNumComments()));
        }catch (NullPointerException e){}

        try {
            Glide.with(CompanyPostCommentActivity.this)
                    .load(mPost.getUser().getImage())
                    .into(postOwnerDisplayImageView);
        }catch (NullPointerException e){}

        try {
            if(mPost.getPostImageUrl() != null) {
                postDisplayImageView.setVisibility(View.VISIBLE);
                StorageReference mStorageReference = FirebaseStorage.getInstance().getReference(mPost.getPostImageUrl());

                Glide.with(CompanyPostCommentActivity.this).load(mStorageReference).into(postDisplayImageView);
            }else {
                postDisplayImageView.setImageBitmap(null);
                postDisplayImageView.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){}


    }

    private void init() {
        mCommentEditTextView = findViewById(R.id.et_comment);
        findViewById(R.id.iv_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_send:
                sendComment();
        }
        
    }

    private void sendComment() {
        final ProgressDialog progressDialog = new ProgressDialog(CompanyPostCommentActivity.this);
        progressDialog.setMessage("Sending comment...");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mComment = new Comment();
        final String uid = FirebaseUtils.getUid();
        String strComment = mCommentEditTextView.getText().toString();

        mComment.setCommentId(uid);
        mComment.setComment(strComment);
        mComment.setTimeCreated(System.currentTimeMillis());

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jDatabase = FirebaseDatabase.getInstance().getReference().child("Student").child(registeredUserID);

        mComment.setUid(registeredUserID);

        jDatabase
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Student student = dataSnapshot.getValue(Student.class);
                        FirebaseUtils.getCommentRef(mPost.getPostId())
                                .child(uid)
                                .setValue(mComment);
                        FirebaseUtils.getPostRef().child(mPost.getPostId())
                                .child(Constants.NUM_COMMENTS_KEY)
                                .runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        long num = (long)mutableData.getValue();
                                        mutableData.setValue(num + 1);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                        progressDialog.dismiss();
                                        FirebaseUtils.addToMyRecord(Constants.COMMENTS_KEY, uid);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private class CommentHolder extends RecyclerView.ViewHolder {
        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;

        public CommentHolder(View itemView) {
            super(itemView);
            commentOwnerDisplay = itemView.findViewById(R.id.iv_comment_owner_display);
            usernameTextView = itemView.findViewById(R.id.tv_username);
            timeTextView = itemView.findViewById(R.id.tv_time);
            commentTextView = itemView.findViewById(R.id.tv_comment);
        }

        public void setUsername(String username) {
            usernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            timeTextView.setText(time);
        }

        public void setComment(String comment) {
            commentTextView.setText(comment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_COMMENT, mComment);
        super.onSaveInstanceState(outState);
    }
}
