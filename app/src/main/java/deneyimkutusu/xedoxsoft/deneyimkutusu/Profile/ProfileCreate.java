package deneyimkutusu.xedoxsoft.deneyimkutusu.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;
import java.util.Locale;

import deneyimkutusu.xedoxsoft.deneyimkutusu.MainActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Maps.GetCurrentLocation;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.UserModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class ProfileCreate extends AppCompatActivity implements LocationListener {
    ProgressBar progressDialog;
    Button olustur;
    Button getKonum;
    ImageButton resimsec;
    EditText isim;
    EditText soyisim;
    EditText ulke;
    EditText sehir;
    TextView profilResmiSecildi;
    LocationManager locationManager;
    private FirebaseAuth auth;
    StorageReference storageReference;   // Creating StorageReference and DatabaseReference object.
    DatabaseReference databaseReference;
    Uri FilePathUri; // Creating URI.
    String Storage_Path = "Profil_Resimleri/"; // Firabase storage da kaydedilicek root nodu
    String Database_Path = "Uyeler";// Firabase database de kayıt yapılacak Root nodu
    String kullanici;
    int Image_Request_Code = 7;    // Image request code for onActivityResult() .
    int uploadFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Remove notification bar
        setContentView(R.layout.activity_profile_create);
        //Activity çalıştığında otomatik klavye açılmasını önleme
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        auth = FirebaseAuth.getInstance();
        kullanici = auth.getUid();
        isim = (EditText) findViewById(R.id.editText3);
        soyisim = (EditText) findViewById(R.id.soyisimEditText);
        ulke = (EditText) findViewById(R.id.editText6);
        sehir = (EditText) findViewById(R.id.editText7);
        profilResmiSecildi = (TextView) findViewById(R.id.textView25);
        olustur = (Button) findViewById(R.id.profileOlBtn);
        resimsec = (ImageButton) findViewById(R.id.imageButton);
        getKonum = (Button) findViewById(R.id.create_konum);
        progressDialog = (ProgressBar) findViewById(R.id.prflcr_UploadProgressBar);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        resimsec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).setActivityTitle("Kırpma işlemi")
                        .setCropMenuCropButtonTitle("Onayla")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileCreate.this);
            }
        });
        olustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isim.getText().length() > 0 && soyisim.getText().length() > 0 && sehir.getText().length() > 0 && ulke.getText().length() > 0) {
                    if (auth.getCurrentUser() != null) {
                        UploadImageFileToFirebaseStorage();
                    }
                } else {
                    Snackbar.make(view, R.string.snackbar_update, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        getKonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {
        final String name = isim.getText().toString();
        final String surname = soyisim.getText().toString();
        final String country = ulke.getText().toString();
        final String city = sehir.getText().toString();
        UserModel user = new UserModel();
        final String kapakUrl = user.getKapakUrl();
        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {
            progressDialog.setVisibility(View.VISIBLE);
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + "jpg");
            storageReference2nd.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String TempImageName = "Profil resmi";
                    @SuppressWarnings("VisibleForTests")
                    UserModel imageUploadInfo = new UserModel(TempImageName, taskSnapshot.getDownloadUrl().toString(), kapakUrl, kullanici, name, surname, country, city, "Acemi", "0");
                    if (auth.getCurrentUser() != null) {
                        String userId = auth.getUid();
                        databaseReference.child(userId).setValue(imageUploadInfo);
                        uploadFlag = 1;
                        if (uploadFlag == 1) {
                            //Profili_Olustur(auth.getUid(),name,surname,country,city);
                            progressDialog.setVisibility(View.GONE);
                            Toast.makeText(ProfileCreate.this,R.string.profil_olusturuldu, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            uploadFlag = 0;
                            finish();
                        }
                    } else {
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //Toast.makeText(ProfileCreate.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        } else {
            Toast.makeText(ProfileCreate.this,R.string.profil_resmi_sec, Toast.LENGTH_LONG).show();
            uploadFlag = 0;
        }
    }

    //Resim seçtikten sonra sonuc döndüren activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //Uri sonuc = result.getUri();
                FilePathUri = result.getUri();
                //resimsec.setImageURI(FilePathUri);
                resimsec.setBackgroundColor(getResources().getColor(R.color.fsq_transparent));
                Picasso.with(this).load(FilePathUri).transform(new RoundedCornersTransformation(100, 1)).fit().centerCrop().into(resimsec);

                profilResmiSecildi.setText(R.string.profil_resmi_secildi);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
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
        Toast.makeText(ProfileCreate.this,R.string.konum_servisi_kontrol, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(ProfileCreate.this,R.string.konum_bekleniyor, Toast.LENGTH_SHORT).show();
    }
}
