package com.prayerkidsstore;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentKonfirmasiActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    final int REQUEST_CAPTURE_IMAGE = 1;
    final int REQUEST_IMAGE_GALLERY = 2;
    int quality = 40;
    Uri photo_location;
    Bitmap bitmap;
    byte[] datatyp;
    File imagefile = null;
    ImageView mbukti_img;
    TextView mupload_gallery,mmsubmit_photo;
    ///firebase Storage
    FirebaseStorage storage;
    StorageReference storageRef;
    String orderId="";
    String page="";
    String userId="";
    ///update status
    FirebaseFirestore firebaseFirestore;
    ProgressDialog loading;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_konfirmasi);
        mbukti_img = findViewById(R.id.bukti_img);
        mupload_gallery = findViewById(R.id.upload_gallery);
        mmsubmit_photo = findViewById(R.id.msubmit_photo);
        //Mengambil orderId Product Kemudian Load Data Product
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getString("OrderId");
            checkLogin();
        }
        mupload_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestImage();

            }
        });
        mmsubmit_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBukti();
            }
        });
    }
    public void checkLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in

            userId = user.getUid();

            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // No user is signed in
            Intent logoout = new Intent(PaymentKonfirmasiActivity.this, LoginActivity.class);
            startActivity(logoout);
            finish();
            Log.d("kondisi login","Tidak Login");
        }


    }
    private void setRequestImage() {
        if ((ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

        ActivityCompat.requestPermissions((Activity)this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);

    }else {
        Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntentGallery, REQUEST_IMAGE_GALLERY);
        photo_location = imageIntentGallery.getData();
    }


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,  resultCode,  data);

        if (requestCode==REQUEST_IMAGE_GALLERY) {

            if (photo_location!=null){
//                    Toast.makeText(AddRequest.this, photo_location.toString(),Toast.LENGTH_LONG).show();
                if (data!=null){
                    photo_location = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo_location);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                        datatyp = byteArrayOutputStream.toByteArray();
                        imagefile = null;
                        try {
                            File file;
                            String string2 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("JPEG_");
                            stringBuilder.append(string2);
                            stringBuilder.append("_");
                            imagefile = file = File.createTempFile((String)stringBuilder.toString(), (String)".jpg", (File)this.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                            file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(imagefile);
                            fileOutputStream.write(datatyp);
                            fileOutputStream.flush();
                            fileOutputStream.close();

                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                            Toast.makeText(PaymentKonfirmasiActivity.this, exception.toString(),Toast.LENGTH_LONG).show();
                        }
                        mbukti_img.setImageBitmap(bitmap);

                    }
                    catch (IOException iOException) {
                        iOException.printStackTrace();
                        Toast.makeText(PaymentKonfirmasiActivity.this, iOException.toString(),Toast.LENGTH_LONG).show();
                    }
                }

            }

        }
        else {

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults.length > 0 && (grantResults[0]
                == PackageManager.PERMISSION_GRANTED)) {
            if ((ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);

            } else {
                Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageIntentGallery, REQUEST_IMAGE_GALLERY);
                photo_location = imageIntentGallery.getData();


            }

        } else {
            Toast.makeText(this, "Akses Photo & Storage Wajib", Toast.LENGTH_LONG).show();

        }
    }
    public void uploadBukti(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.show();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("img/uploadBukti/"+orderId+"/");
        mbukti_img.setDrawingCacheEnabled(true);
        mbukti_img.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) mbukti_img.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads\
                Log.d("gagal_upload",exception.toString());
                loading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String profileImageUrl=task.getResult().toString();
                        Log.i("URL_photo:",profileImageUrl);
                        updateStatusorder();
                    }
                });
            }
        });
    }
    public void updateStatusorder(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        Map<String, Object> dataEmailNama = new HashMap();
        dataEmailNama.put("StatusOrder","Menunggu Konfirmasi");
        DocumentReference documentReference = firebaseFirestore.collection("OrderPk").document(userId).collection("OrderList").document(orderId);
        documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loading.dismiss();
                Log.d("UpdateRespon: ","Update Berhasil");

                Intent backhome = new Intent(PaymentKonfirmasiActivity.this, MyOrder.class);
                backhome.putExtra("OrderId",orderId);
                backhome.putExtra("pages",page);
                startActivity(backhome);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.dismiss();
                Log.d("UpdateRespon: ",e.toString());


            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, PaymentActivity.class);
        backhome.putExtra("OrderId",orderId);
        backhome.putExtra("pages",page);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}