package bbitb.com.urbnvision.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import bbitb.com.urbnvision.R;
import bbitb.com.urbnvision.models.Constants;
import bbitb.com.urbnvision.models.FirebaseUtils;
import bbitb.com.urbnvision.models.Post;

import static android.app.Activity.RESULT_OK;

public class PostCreateDialog extends DialogFragment implements View.OnClickListener {

    private static final int RC_PHOTO_PICKER = 1;
    private Post mPost;
    private ProgressDialog mProgressDialog;
    private Uri mSelectedUri;
    private ImageView mPostDisplay;
    private View mRootView;

    private FirebaseAuth firebaseAuth;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mPost = new Post();
        mProgressDialog = new ProgressDialog(getActivity());

        firebaseAuth = FirebaseAuth.getInstance();

        mRootView = getActivity().getLayoutInflater().inflate(R.layout.create_post_dialog, null);
        mPostDisplay = mRootView.findViewById(R.id.post_dialog_display);
        mRootView.findViewById(R.id.post_dialog_send_imageview).setOnClickListener(this);
        mRootView.findViewById(R.id.post_dialog_select_imageview).setOnClickListener(this);
        builder.setView(mRootView);
        return builder.create();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.post_dialog_send_imageview:
                sendPost();
                break;
            case R.id.post_dialog_select_imageview:
                selectImage();
                break;
        }

    }

    private void sendPost() {
        mProgressDialog.setMessage("Sending post...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jDatabase = FirebaseDatabase.getInstance().getReference().child("Company").child(registeredUserID);

        //FirebaseUtils.getCompanyRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
        jDatabase
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        /*String username = dataSnapshot.child("username").getValue().toString();
                                Log.d(TAG, "dataSnapshot: Student info :- "+ username);
                        String email = dataSnapshot.child("email").getValue().toString();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

                        Student student = new Student(username, email, uid );*/
                        String companyID = dataSnapshot.child("uid").getValue().toString();
                        //Log.d(TAG, "dataSnapshot: Student info :- "+ student);

                        final String postId = FirebaseUtils.getUid();
                        TextView postDialogTextView = mRootView.findViewById(R.id.post_dialog_edittext);
                        String text = postDialogTextView.getText().toString();

                        mPost.setCompany(companyID);
                        mPost.setNumComments(0);
                        mPost.setNumLikes(0);
                        mPost.setTimeCreated(System.currentTimeMillis());
                        mPost.setPostId(postId);
                        mPost.setPostText(text);

                        if(mSelectedUri != null){
                            FirebaseStorage.getInstance().getReference(Constants.POST_IMAGES)
                                    .child(mSelectedUri.getLastPathSegment())
                                    .putFile(mSelectedUri)
                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            String url = Constants.POST_IMAGES + "/" + mSelectedUri.getLastPathSegment();
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            mPost.setPostImageUrl(String.valueOf(downloadUrl));
                                            addToMyPostList(postId);
                                        }
                                    });
                        }else{
                            addToMyPostList(postId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void addToMyPostList(String postId) {
        FirebaseUtils.getPostRef().child(postId)
                .setValue(mPost);
        FirebaseUtils.getMyPostRef().child(postId).setValue(true)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        dismiss();
                    }
                });
        FirebaseUtils.addToMyRecord(Constants.POST_KEY, postId);
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
