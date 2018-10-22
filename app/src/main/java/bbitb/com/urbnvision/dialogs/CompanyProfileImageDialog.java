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

import bbitb.com.urbnvision.models.Company;
import bbitb.com.urbnvision.R;
import bbitb.com.urbnvision.models.Constants;

import static android.app.Activity.RESULT_OK;


public class CompanyProfileImageDialog extends DialogFragment implements View.OnClickListener {

    private static final int RC_PHOTO_PICKER = 1;
    private Company iCompany;
    private ProgressDialog mProgressDialog;
    private Uri mSelectedUri;
    private ImageView mImageDisplay;
    private View mRootView;
    private FirebaseAuth firebaseAuth;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        iCompany = new Company();
        mProgressDialog = new ProgressDialog(getActivity());

        firebaseAuth = FirebaseAuth.getInstance();

        mRootView = getActivity().getLayoutInflater().inflate(R.layout.user_image_dialog, null);
        mImageDisplay = mRootView.findViewById(R.id.image_dialog_display);
        mRootView.findViewById(R.id.image_dialog_send_imageview).setOnClickListener(this);
        mRootView.findViewById(R.id.image_dialog_select_imageview).setOnClickListener(this);
        builder.setView(mRootView);
        return builder.create();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image_dialog_send_imageview:
                sendProfilePic();
                break;
            case R.id.image_dialog_select_imageview:
                selectImage();
                break;
        }
    }

    private void sendProfilePic() {
        mProgressDialog.setMessage("Sending profile picture...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference jDatabase = FirebaseDatabase.getInstance().getReference().child("Company").child(registeredUserID);

        //FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
               jDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Student user = dataSnapshot.getValue(Student.class);
                        //final String userId = FirebaseUtils.getUid();

                        if(mSelectedUri != null){
                            FirebaseStorage.getInstance().getReference(Constants.PROFILE_IMAGES)
                                    .child(mSelectedUri.getLastPathSegment())
                                    .putFile(mSelectedUri)
                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            String url = Constants.PROFILE_IMAGES + "/" + mSelectedUri.getLastPathSegment();
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            //iCompany.setImage(String.valueOf(downloadUrl));
                                            String basicImage = String.valueOf(downloadUrl);
                                            addImageToUserProfile(basicImage);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void addImageToUserProfile(String image) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String registeredUserID = currentUser.getUid();
        DatabaseReference studentDatabase = FirebaseDatabase.getInstance().getReference().child("Company").child(registeredUserID).child("image");


        studentDatabase
                .setValue(image)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        dismiss();
                    }
                });

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
                mImageDisplay.setImageURI(mSelectedUri);
            }
        }
    }
}
