package com.meghaagarwal.pixbomb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private ImageButton mImageButton;
    private EditText mEditTitle;
    private EditText mEditDesc;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("PixBomb");

        mEditTitle = (EditText) findViewById(R.id.editTitle);
        mEditDesc = (EditText) findViewById(R.id.editDesc);
    }

    public void imageButtonClicked(View view)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            uri = data.getData();
            mImageButton = (ImageButton) findViewById(R.id.imageButton);
            mImageButton.setImageURI(uri);
        }

    }
    public void submitButtonClicked(View view)
    {
        final String titleValue = mEditTitle.getText().toString().trim();
        final String descValue = mEditDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(descValue))
        {
            StorageReference filepath = mStorageReference.child("PostedImages").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this, "Upload Complete", Toast.LENGTH_LONG).show();

                    DatabaseReference newPost = mDatabaseReference.push();
                    newPost.child("title").setValue(titleValue);
                    newPost.child("desc").setValue(descValue);
                    newPost.child("image").setValue(downloadUrl);
                }
            });
        }
    }
}