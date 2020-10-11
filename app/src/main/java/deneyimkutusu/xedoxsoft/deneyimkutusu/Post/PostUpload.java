package deneyimkutusu.xedoxsoft.deneyimkutusu.Post;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foursquare.api.types.Venue;
import com.foursquare.placepicker.PlacePicker;
import com.foursquare.placepicker.PlacePickerSdk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.KategoriModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.UserModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;

public class PostUpload extends AppCompatActivity implements LocationListener {

    //Foursqaure api için gerekli olan nesnelerimiz ve değişkenlerimiz
    private TextView pickupLocation;
    public static final String FOURSQUARE_CLIENT_KEY = "I4KGN51TLDRF1PES3KJPY3JGMQREHZA4JGNE3WSH3PS1ZGQE";
    public static final String FOURSQUARE_CLIENT_SECRET = "EYL5PMDNSUG2F5UKBFYF0QCZ0KOGRLAYVJVNVQOZEZ2SXOXL";
    private ProgressBar progressBar;
    Button yukle;
    Button kategoriEkle;
    Button ek_resim;
    ImageButton yazi_resmi;
    ImageButton yazi_resmi2;
    ImageView help_image;
    ImageView foursquare_delete;
    ImageView foursquare_search;
    EditText yazi_baslik;
    EditText yazi_icerik;
    EditText bulunamayan_edit;
    Spinner spinner;
    Dialog dialog;
    LocationManager locationManager;
    FirebaseAuth auth;
    DatabaseReference databaseKullanici;
    DatabaseReference databaseKategori;
    DatabaseReference databaseReferenceYazilar;
    StorageReference storageReference;
    Uri FilePathUri; // Creating URI.
    Uri FilePathUri2;
    Calendar cal = Calendar.getInstance();
    DateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
    String secilen_kategori;
    String location="";
    String Storage_Path = "Yazi_Resimleri/";
    String resims2_snapshot_url = "null";
    String kul_id;
    String kul_adi;
    String kul_soyadi;
    String kul_profil_resim;
    String yazilar_id;
    String ulke_b="";
    String sehir_b="";
    String enlem;
    String boylam;
    int resim_flag = 0; //2.resim boş bırakılabilir bayrak değişkeni
    int incelem_s;
    int uploadFlag = 0;
    int konum_bul_basilma=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
        setContentView(R.layout.activity_post_upload);
        yazi_baslik = (EditText) findViewById(R.id.editText4);
        yazi_icerik = (EditText) findViewById(R.id.editText15);
        pickupLocation = (TextView) findViewById(R.id.pickUpTextView);
        bulunamayan_edit = (EditText) findViewById(R.id.bulunamayanEditText);
        kategoriEkle = (Button) findViewById(R.id.button14);
        yukle = (Button) findViewById(R.id.button4);
        ek_resim = (Button) findViewById(R.id.button6);
        progressBar = (ProgressBar) findViewById(R.id.UploadProgressBar);
        yazi_resmi = (ImageButton) findViewById(R.id.imageButton4);
        yazi_resmi2 = (ImageButton) findViewById(R.id.imageButton5);
        help_image = (ImageView) findViewById(R.id.help_image);
        foursquare_delete = (ImageView) findViewById(R.id.upld_imageView_clear);
        foursquare_search = (ImageView) findViewById(R.id.imageView);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        databaseKategori = FirebaseDatabase.getInstance().getReference();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//Activity çalıştığında otomatik klavye açılmasını önleme
        //if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        //}
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        if (auth.getCurrentUser() != null) {
            kul_id = auth.getUid(); //Şu anki kullanıcı id sini getir
            databaseKategori.getRoot().child("Kategoriler").addValueEventListener(new ValueEventListener() { //Kategorileri spinnera yükler
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<String> kategoriesList = new ArrayList<String>();
                    for (DataSnapshot kSnapshot : dataSnapshot.getChildren()) {
                        String kategoriName = kSnapshot.child("kategori_adi").getValue(String.class);
                        kategoriesList.add(kategoriName);
                    }
                    spinner = (Spinner) findViewById(R.id.spinner1);
                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(PostUpload.this, android.R.layout.simple_spinner_item, kategoriesList);
                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(areasAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            secilen_kategori = spinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            kategoriEkle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomAlertDialog();
                }
            });
            databaseKullanici = FirebaseDatabase.getInstance().getReference("Uyeler").child(kul_id);
            databaseKullanici.keepSynced(true);//Ofline yükleme yapmayı sağlıyor.
            ValueEventListener kullanicEvent = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel profil = new UserModel();
                    profil = dataSnapshot.getValue(UserModel.class);
                    kul_adi = "" + profil.getIsim();
                    kul_soyadi=""+profil.getSoyisim();
                    kul_profil_resim = profil.getResimUrl();
                    incelem_s = Integer.parseInt(profil.getInceleme_sayisi());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseKullanici.addValueEventListener(kullanicEvent);
        } else {
            Toast.makeText(PostUpload.this,R.string.kullanicibilgisi_cekilemedi, Toast.LENGTH_LONG).show();
        }
        databaseReferenceYazilar = FirebaseDatabase.getInstance().getReference("Yazilar");
        pickupLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPlace();
                getLocation();
                bulunamayan_edit.setText("");
                bulunamayan_edit.setVisibility(View.GONE);
                foursquare_search.setVisibility(View.GONE);
                foursquare_delete.setVisibility(View.VISIBLE);
                konum_bul_basilma=1;
            }
        });
        foursquare_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foursquare_delete.setVisibility(View.GONE);
                foursquare_search.setVisibility(View.VISIBLE);
                pickupLocation.setText("");
                location="";
            }
        });
        yazi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resim_flag = 1;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity().setActivityTitle(""+getString(R.string.crop_baslik))
                        .setCropMenuCropButtonTitle(""+getString(R.string.crop_onayla))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostUpload.this);
            }
        });
        help_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (konum_bul_basilma==1){
                    bulunamayan_edit.setVisibility(View.VISIBLE);
                    if (bulunamayan_edit.getText().length()>0){
                        pickupLocation.setText(bulunamayan_edit.getText().toString());
                        location=bulunamayan_edit.getText().toString();
                    }
                }else{
                    Toast.makeText(PostUpload.this, R.string.konum_bulmayi_dene, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //textview scrool özelliği
        yazi_icerik.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        ek_resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/**/
                yazi_resmi2.setVisibility(View.VISIBLE);
                //ek_resim.setVisibility(View.GONE);
                resim_flag = 2;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity().setActivityTitle(""+getString(R.string.crop_baslik))
                        .setCropMenuCropButtonTitle(""+getString(R.string.crop_onayla))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostUpload.this);
            }
        });
        PlacePickerSdk.with(new PlacePickerSdk.Builder(this).consumer(FOURSQUARE_CLIENT_KEY, FOURSQUARE_CLIENT_SECRET)
                .imageLoader(new PlacePickerSdk.ImageLoader() {
                    @Override
                    public void loadImage(Context context, ImageView v, String url) {
                        Glide.with(context)
                                .load(url)
                                .placeholder(R.drawable.category_none)
                                .dontAnimate()
                                .into(v);
                    }
                }).build());
        yukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yazi_baslik.getText().toString().equals(null)){
                    yazi_baslik.setText("");
                }
                if (yazi_baslik.getText().length()>0&&yazi_icerik.getText().length()>0&&pickupLocation.getText().length()>0||bulunamayan_edit.getText().length()>0){
                    if (auth.getCurrentUser() != null) {
                        //2.resmin seçilip seçilmediği
                        if (FilePathUri2 != null) {
                            progressBar.setVisibility(View.VISIBLE);
                            StorageReference storageReference3nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + "jpg");
                            storageReference3nd.putFile(FilePathUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if (auth.getCurrentUser() != null) {
                                        resims2_snapshot_url = taskSnapshot.getDownloadUrl().toString();
                                        UploadImageFileToFirebaseStorage();

                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            UploadImageFileToFirebaseStorage();//İkinci resim seçilmedi ise direk upload
                        }
                    }
                }else{
                    Snackbar.make(view,R.string.snackbar_update, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }
        });
    }

    private void pickPlace() {
        Intent intent = new Intent(this, PlacePicker.class);
        startActivityForResult(intent, 9001);
//        if (ulke_b == null) {
////            Toast.makeText(PostUpload.this, "Lütfen konum bilgisini ve internetinizi kontrol ediniz", Toast.LENGTH_SHORT).show();
//        } else {
//            Intent intent = new Intent(this, PlacePicker.class);
//            startActivityForResult(intent, 9001);
//        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void UploadImageFileToFirebaseStorage() {// Creating UploadImageFileToFirebaseStorage method to upload image on storage.
        final String yazi_b = yazi_baslik.getText().toString();
        final String yazi_i = yazi_icerik.getText().toString();
        final String currentTime = sdf.format(cal.getTime());
        yazilar_id = databaseReferenceYazilar.push().getKey();//Yazilar için unique id olşturur.
        if (FilePathUri != null) {
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + "jpg");
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            DataClass dataUploadInfo = new DataClass("" + yazilar_id, "" + kul_id, "" + secilen_kategori,
                                    "" + kul_soyadi,""+kul_adi, "" +ulke_b,""+sehir_b, "" + currentTime, "" + 0,
                                    "" + 0, "" + 0, "" + yazi_b, "" + yazi_i, "" + taskSnapshot.getDownloadUrl().toString(),
                                    "" + resims2_snapshot_url, "" + kul_profil_resim, "" + enlem, "" + boylam, location+" " + "" + bulunamayan_edit.getText().toString());
                            if (auth.getCurrentUser() != null) {
                                databaseReferenceYazilar.child(yazilar_id).setValue(dataUploadInfo);// Adding image upload id s child element into databaseReference.
                                incelem_s = incelem_s + 1;
                                databaseKullanici.child("inceleme_sayisi").setValue("" + incelem_s);
                                uploadFlag = 1;
                                if (uploadFlag == 1) {
                                    Toast.makeText(getApplicationContext(),R.string.yaziniz_yuklendi, Toast.LENGTH_LONG).show();// Showing toast message after done uploading.
                                    progressBar.setVisibility(View.GONE);
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
                            //Toast.makeText(PostUpload.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this,R.string.ilkresim_bos, Toast.LENGTH_SHORT).show();
            uploadFlag = 0;
        }
    }

    public void CustomAlertDialog() {
        final String kat_id = databaseKategori.push().getKey();
        dialog = new Dialog(PostUpload.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle(R.string.dialog);
        final EditText getir = (EditText) dialog.findViewById(R.id.editText8);
        Button iptalb = (Button) dialog.findViewById(R.id.button3);
        Button olusturb = (Button) dialog.findViewById(R.id.button5);
        olusturb.setEnabled(true);
        iptalb.setEnabled(true);
        olusturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getir.getText().length() > 0) {
                    writeNewKategori(kat_id, getir.getText().toString());
                    dialog.cancel();
                } else {
                    Toast.makeText(PostUpload.this,R.string.dialog_kategori_bos, Toast.LENGTH_SHORT).show();
                    dialog.show();
                }
            }
        });
        iptalb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void writeNewKategori(String kategoriId, String kategoriName) {
        KategoriModel k = new KategoriModel(kategoriId, kategoriName);
        databaseKategori.child("Kategoriler").child(kategoriId).setValue(k);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PlacePicker.PLACE_PICKED_RESULT_CODE) {
            Venue place = data.getParcelableExtra(PlacePicker.EXTRA_PLACE);
            pickupLocation.setText(place.getName());
            //Buradada edittext teki bilgi geri döndürülür
            //Toast.makeText(this, place.getName(), Toast.LENGTH_LONG).show();
            location = place.getName();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (resim_flag == 1) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    FilePathUri = result.getUri();//Uri sonuc = result.getUri();
                    yazi_resmi.setPadding(0, 0, 0, 0);
                    yazi_resmi.setImageURI(FilePathUri);
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
                    yazi_resmi2.setPadding(0, 0, 0, 0);
                    yazi_resmi2.setImageURI(FilePathUri2);
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
            ulke_b = (addresses.get(0).getCountryName());
            sehir_b = (addresses.get(0).getAdminArea());
            enlem = String.valueOf(location.getLatitude());
            boylam = String.valueOf(location.getLongitude());
        } catch (Exception e) {
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(PostUpload.this,R.string.konum_servisi_kontrol, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();
        Toast.makeText(PostUpload.this,R.string.konum_bekleniyor, Toast.LENGTH_SHORT).show();
    }

}
