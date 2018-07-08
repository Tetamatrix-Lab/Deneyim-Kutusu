package deneyimkutusu.xedoxsoft.deneyimkutusu.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.UserModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.YorumModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfilUpdate extends AppCompatActivity implements LocationListener {
    Button guncelle;
    Button getkonum;
    Button delete_acount;
    ImageView profilresmi;
    ImageView kapakresmi;
    EditText isim;
    EditText soyisim;
    EditText sehir;
    EditText ulke;
    ProgressBar progressBar;

    DatabaseReference databaseread;
    DatabaseReference yorumlar_database;
    DatabaseReference yazilar_database;
    FirebaseAuth auth;
    Query mQueryYorum;
    Query mQueryYazilar2;
    Uri FilePathUri; // Profil resmi
    Uri FilePathUri2;// Kapak resmi
    StorageReference storageReference;
    String Storage_Path = "Profil_Resimleri/";
    LocationManager locationManager;
    String kullanici_id;
    String rsm1;
    String rsm2;
    String resims2_snapshot_url = "null";
    String resim1_snapshot_url = "null";
    String post_id;
    ArrayList<YorumModel> yorumlarArrayList;
    ArrayList<DataClass> yazilarGel;
    ArrayList<String> id_list;
    int resim_tiklandi=0;
    int resim2_tiklandi=0;
    int resim_flag = 0; //2.resim boş bırakılabilir bayrak değişkeni
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_update);
        profilresmi=(ImageView)findViewById(R.id.profil_img_update);
        kapakresmi=(ImageView)findViewById(R.id.kapak_update);
        isim=(EditText)findViewById(R.id.isim_updt);
        soyisim=(EditText)findViewById(R.id.soyisim_uptd);
        sehir=(EditText)findViewById(R.id.sehir_updt);
        ulke=(EditText)findViewById(R.id.ulke_uptd);
        guncelle=(Button)findViewById(R.id.prl_uptl_button);
        getkonum=(Button)findViewById(R.id.create_konum_uplt);
        delete_acount=(Button) findViewById(R.id.button_delete_acount);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        kullanici_id = auth.getUid();
        databaseread = FirebaseDatabase.getInstance().getReference().child("Uyeler").child(kullanici_id);
        yazilar_database = FirebaseDatabase.getInstance().getReference().child("Yazilar");
        yorumlar_database = FirebaseDatabase.getInstance().getReference("Kisi_yorumlari");
        databaseread.keepSynced(true);
        yazilar_database.keepSynced(true);
        yorumlar_database.keepSynced(true);
       if (auth.getCurrentUser() != null) {
            ValueEventListener oku = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel profil = new UserModel();
                    profil = dataSnapshot.getValue(UserModel.class);
                    isim.setText(profil.getIsim());
                    soyisim.setText(profil.getSoyisim());
                    ulke.setText(profil.getUlke());
                    sehir.setText(profil.getSehir());
                    String pResimUrl = profil.getResimUrl();
                    String kResimUrl = profil.getKapakUrl();
                    Picasso.with(getApplicationContext()).load(pResimUrl).transform(new CropCircleTransformation()).fit().centerCrop().into(profilresmi);
                    Picasso.with(getApplicationContext()).load(kResimUrl).fit().centerCrop().into(kapakresmi);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            databaseread.addValueEventListener(oku);//databaseread te verdiğimiz referansa göre oku value listenirimız sayesende okuma yapar
        } else {
        }
        getkonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
        profilresmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resim_tiklandi=1;
                resim_flag = 1;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity().setActivityTitle("Kırpma işlemi")
                        .setCropMenuCropButtonTitle("Onayla")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfilUpdate.this);
            }
        });
        kapakresmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resim2_tiklandi=1;
                resim_flag = 2;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity().setActivityTitle("Kırpma işlemi")
                        .setCropMenuCropButtonTitle("Onayla")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfilUpdate.this);
            }
        });
        guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateFirebase();
                if (resim_tiklandi==1){
                    if (FilePathUri != null) {
                        StorageReference storageReference3nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + "jpg");
                        storageReference3nd.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (auth.getCurrentUser() != null) {
                                    resim1_snapshot_url=taskSnapshot.getDownloadUrl().toString();
                                    databaseread.child("resimUrl").setValue(resim1_snapshot_url);
                                    prepareData(resim1_snapshot_url);
                                    prepareDataYazilar(resim1_snapshot_url);
                                }
                            }
                        });
                    }
                }
                if (resim2_tiklandi==1){
                    if (FilePathUri2 != null) {
                        StorageReference storageReference3nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + "jpg");
                        storageReference3nd.putFile(FilePathUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (auth.getCurrentUser() != null) {
                                    resims2_snapshot_url=taskSnapshot.getDownloadUrl().toString();
                                    databaseread.child("kapakUrl").setValue(resims2_snapshot_url);
                                    finish();
                                }
                            }
                        });
                    }
                }
                if (resim_tiklandi==0&&resim2_tiklandi==0){
                    finish();
                }
            }
        });
        delete_acount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showYesNoDialog(view);
            }
        });
    }
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private ArrayList prepareData(final String url) {
        mQueryYorum = yorumlar_database.orderByChild("uye_id").equalTo(kullanici_id);
        yorumlarArrayList = new ArrayList<YorumModel>();
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yorumlarArrayList.clear();//Bunu yazmassan Duplicate ediyor her veritabaninda değişiklik olduğunda
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    YorumModel yorum = postSnapshot.getValue(YorumModel.class);
                    //id_list.add(yorum.getYorum_id());
                    yorumlar_database.child(yorum.getYorum_id()).child("kisi_resim_url").setValue(url);
                    yorumlar_database.child(yorum.getYorum_id()).child("uye_adi").setValue(""+isim.getText().toString()+" "+soyisim.getText().toString());
                    //Toast.makeText(ProfilUpdate.this, ""+id_list.size(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mQueryYorum.addListenerForSingleValueEvent(oku);
        return yorumlarArrayList;
    }
    private ArrayList prepareDataYazilar(final String url) {
        mQueryYorum = yazilar_database.orderByChild("uye_id").equalTo(kullanici_id);
        yazilarGel = new ArrayList<DataClass>();
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yazilarGel.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DataClass yazi = postSnapshot.getValue(DataClass.class);
                    yazilar_database.child(yazi.getIcerik_id()).child("profileImage").setValue(url);
                    yazilar_database.child(yazi.getIcerik_id()).child("nameSurname").setValue(isim.getText().toString());
                    yazilar_database.child(yazi.getIcerik_id()).child("surname").setValue(soyisim.getText().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mQueryYorum.addListenerForSingleValueEvent(oku);
        return yorumlarArrayList;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resim_flag == 1) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    FilePathUri = result.getUri();//Uri sonuc = result.getUri();
                    profilresmi.setPadding(0, 0, 0, 0);
                    profilresmi.setImageURI(FilePathUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
        if (resim_flag == 2) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    FilePathUri2 = result.getUri();//Uri sonuc = result.getUri();
                    kapakresmi.setPadding(0, 0, 0, 0);
                    kapakresmi.setImageURI(FilePathUri2);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            ulke.setText(addresses.get(0).getCountryName());
            sehir.setText(addresses.get(0).getAdminArea());
        } catch (Exception e) {
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(ProfilUpdate.this,R.string.konum_servisi_kontrol, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(ProfilUpdate.this, R.string.konum_bilgisi_alınıyor, Toast.LENGTH_SHORT).show();
    }
    public void UpdateFirebase() {// Creating UploadImageFileToFirebaseStorage method to upload image on storage.
        mQueryYorum = yorumlar_database.orderByChild("uye_id").equalTo(kullanici_id);
        yorumlarArrayList = new ArrayList<YorumModel>();
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yorumlarArrayList.clear();//Bunu yazmassan Duplicate ediyor her veritabaninda değişiklik olduğunda
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    YorumModel yorum = postSnapshot.getValue(YorumModel.class);
                    yorumlar_database.child(yorum.getYorum_id()).child("uye_adi").setValue(""+isim.getText().toString()+" "+soyisim.getText().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mQueryYorum.addListenerForSingleValueEvent(oku);
        mQueryYazilar2 = yazilar_database.orderByChild("uye_id").equalTo(kullanici_id);
        yazilarGel = new ArrayList<DataClass>();
        ValueEventListener oku2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yazilarGel.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DataClass yazi = postSnapshot.getValue(DataClass.class);
                    yazilar_database.child(yazi.getIcerik_id()).child("nameSurname").setValue(isim.getText().toString());
                    yazilar_database.child(yazi.getIcerik_id()).child("surname").setValue(soyisim.getText().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mQueryYazilar2.addListenerForSingleValueEvent(oku2);

        databaseread.child("isim").setValue(isim.getText().toString());
        databaseread.child("soyisim").setValue(soyisim.getText().toString());
        databaseread.child("sehir").setValue(sehir.getText().toString());
        databaseread.child("ulke").setValue(ulke.getText().toString());
    }

    public void showYesNoDialog(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hesabı kaldırmak istediğinize eminmisiniz?");
        builder.setCancelable(false);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = auth.getCurrentUser();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfilUpdate.this, R.string.hesabınız_kaldırıldı, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                finish();
            }
        });

        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
            }
        });
        builder.create().show();

    }
}
