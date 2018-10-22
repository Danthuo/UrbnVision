package bbitb.com.urbnvision.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bbitb.com.urbnvision.R;
import bbitb.com.urbnvision.models.Constants;
import bbitb.com.urbnvision.models.Post;

import static android.app.Activity.RESULT_OK;

public class PostUpdateDialog extends DialogFragment implements View.OnClickListener {

    private static final int RC_PHOTO_PICKER = 1;
    private Post mPost;
    private ProgressDialog mProgressDialog;
    private Uri mSelectedUri;
    private ImageView mPostDisplay;
    private View mRootView;
    private EditText postDialogEditText;

    private FirebaseAuth firebaseAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.create_post_dialog, null);

        mPost = new Post();
        mProgressDialog = new ProgressDialog(getActivity());

        Intent intent = getActivity().getIntent();
        mPost = (Post)intent.getSerializableExtra(Constants.EXTRA_POST);

        //Log.e("mPostEditID", mPost.getPostId());

        firebaseAuth = FirebaseAuth.getInstance();
        postDialogEditText = mRootView.findViewById(R.id.post_dialog_edittext);
        //postDialogEditText.setText(mPost.getPostText());

        mPostDisplay = mRootView.findViewById(R.id.post_dialog_display);

        DatabaseReference jDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(mPost.getPostId());
        jDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String postImage = dataSnapshot.child("postImageUrl").getValue().toString();
                if (postImage != "default" && postImage != null) {
                    Glide.with(getContext()).load(postImage).into(mPostDisplay);
                }else{
                    mPostDisplay.setImageResource(R.drawable.ic_account);
                }
                String postText = dataSnapshot.child("postText").getValue().toString();
                postDialogEditText.setText(postText);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
                });

        mRootView.findViewById(R.id.post_dialog_send_imageview).setOnClickListener(this);
        mRootView.findViewById(R.id.post_dialog_select_imageview).setOnClickListener(this);
        builder.setView(mRootView);
        return builder.create();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.post_dialog_send_imageview:
                updatePost();
                break;
            case R.id.post_dialog_select_imageview:
                selectImage();
                break;
        }

    }

    private void updatePost() {
    }


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER){
            if(resultCode == RESULT_OK){
                mSelectedUri = data.getData();
                mPostDisplay.setImageURI(mSelectedUri);
            }
        }
    }
}
