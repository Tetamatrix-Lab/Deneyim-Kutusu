package deneyimkutusu.xedoxsoft.deneyimkutusu.Post;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.foursquare.api.types.Venue;
import com.foursquare.placepicker.PlacePicker;
import com.foursquare.placepicker.PlacePickerSdk;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;

public class PostUpadate extends AppCompatActivity implements LocationListener{
    RelativeLayout relativeLayout;
    ImageView resim1;
    EditText baslik;
    TextView pickup_location;
    EditText bulunamayan_edittext;
    ImageView help_pick;
    ImageView foursquare_delete;
    ImageView foursquare_search;
    Spinner kategori_spinner;
    Button kategori_ekle;
    EditText deneyim;
    ImageView resim2;
    Button guncelle;
    ProgressBar gnc_progress;
    TextView upld_ek_icerk;
    TextView post_kategori_text;
    Button ekresim;

    public static final String FOURSQUARE_CLIENT_KEY = "I4KGN51TLDRF1PES3KJPY3JGMQREHZA4JGNE3WSH3PS1ZGQE";
    public static final String FOURSQUARE_CLIENT_SECRET = "EYL5PMDNSUG2F5UKBFYF0QCZ0KOGRLAYVJVNVQOZEZ2SXOXL";
    LocationManager locationManager;
    DatabaseReference yazilar;
    DatabaseReference kategoriler;
    FirebaseAuth auth;
    StorageReference storageReference;
    Uri FilePathUri; // Creating URI.
    Uri FilePathUri2;
    String Storage_Path = "Yazi_Resimleri/";
    String resims2_snapshot_url = "null";
    String resim1_snapshot_url = "null";
    String post_id;
    String secilen_kategori;
    String ulke_b;
    String sehir_b;
    String enlem;
    String boylam;
    String location;
    String rsm1;
    String rsm2;
    Calendar cal = Calendar.getInstance();
    DateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
    int resim_flag = 0; //2.resim boş bırakılabilir bayrak değişkeni
    int resim_tiklandi=0;
    int resim2_tiklandi=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upadate);
        resim1=(ImageView)findViewById(R.id.imageView_ilk_resim);
        resim2=(ImageView)findViewById(R.id.upld_ikinci_resim);
        foursquare_delete = (ImageView) findViewById(R.id.upld_imageView_clear);
        foursquare_search = (ImageView) findViewById(R.id.upld_imageView);
        help_pick=(ImageView)findViewById(R.id.upld_help_image);
        baslik=(EditText)findViewById(R.id.upld_baslik);
        deneyim=(EditText)findViewById(R.id.upld_deneyim);
        bulunamayan_edittext=(EditText)findViewById(R.id.bulunamayanEditText_update);
        pickup_location=(TextView)findViewById(R.id.upld_pickUpTextView);
        kategori_ekle=(Button)findViewById(R.id.upld_button14);
        guncelle=(Button)findViewById(R.id.upld_button4);
        kategori_spinner=(Spinner)findViewById(R.id.upld_spinner1);
        gnc_progress=(ProgressBar)findViewById(R.id.upld_UploadProgressBar);
        upld_ek_icerk=(TextView)findViewById(R.id.upld_ek_icerik);
        post_kategori_text=(TextView)findViewById(R.id.upld_p_kategori);
        ekresim=(Button)findViewById(R.id.upld_button6);
        relativeLayout=(RelativeLayout)findViewById(R.id.uploadButtonReltv);
        post_id=getIntent().getStringExtra("post_id");
        auth = FirebaseAuth.getInstance();
        yazilar= FirebaseDatabase.getInstance().getReference("Yazilar").child(post_id);
        kategoriler = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        yazilar.keepSynced(true);

        kategoriler.getRoot().child("Kategoriler").addValueEventListener(new ValueEventListener() { //Kategorileri spinnera yükler
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> kategoriesList = new ArrayList<String>();
                for (DataSnapshot kSnapshot : dataSnapshot.getChildren()) {
                    String kategoriName = kSnapshot.child("kategori_adi").getValue(String.class);
                    kategoriesList.add(kategoriName);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(PostUpadate.this, android.R.layout.simple_spinner_item, kategoriesList);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                kategori_spinner.setAdapter(areasAdapter);
                kategori_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        secilen_kategori = kategori_spinner.getSelectedItem().toString();
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
        yazilar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataClass post = dataSnapshot.getValue(DataClass.class);
                baslik.setText(post.getPostTitle());
                pickup_location.setText(post.getPostLocation());
                deneyim.setText(post.getPostExperince());
                post_kategori_text.setText(post.getKategori_name());
                enlem=post.getPostLatitude();
                boylam=post.getPostLongitude();
                location=post.getPostLocation();
                rsm1=post.getPostImage_url();
                rsm2=post.getPostImage_url2();
                Picasso.with(getApplicationContext()).load(rsm1).fit().centerCrop().into(resim1);
                if (rsm2.equals("null")){
                    resim2.setVisibility(View.GONE);
                    ekresim.setVisibility(View.VISIBLE);
                }
                else{
                    Picasso.with(getApplicationContext()).load(rsm2).fit().centerCrop().into(resim2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gnc_progress.setVisibility(View.VISIBLE);
                UpdateFirebase();
                if (resim_tiklandi==1){
                    if (FilePathUri != null) {
                        gnc_progress.setVisibility(View.VISIBLE);
                        StorageReference storageReference3nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + "jpg");
                        storageReference3nd.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (auth.getCurrentUser() != null) {
                                    resim1_snapshot_url=taskSnapshot.getDownloadUrl().toString();
                                    yazilar.child("postImage_url").setValue(resim1_snapshot_url);
                                    gnc_progress.setVisibility(View.GONE);
                                    finish();
                                }
                            }
                        });
                    }
                }
                if (resim2_tiklandi==1){
                    if (FilePathUri2 != null) {
                        gnc_progress.setVisibility(View.VISIBLE);
                        StorageReference storageReference3nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + "jpg");
                        storageReference3nd.putFile(FilePathUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (auth.getCurrentUser() != null) {
                                    resims2_snapshot_url=taskSnapshot.getDownloadUrl().toString();
                                    yazilar.child("postImage_url2").setValue(resims2_snapshot_url);
                                    gnc_progress.setVisibility(View.GONE);
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
        resim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resim_tiklandi=1;
                resim_flag = 1;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity().setActivityTitle(""+getString(R.string.crop_baslik))
                        .setCropMenuCropButtonTitle(""+getString(R.string.crop_onayla))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostUpadate.this);
            }
        });
        ekresim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resim2_tiklandi=1;
                resim2.setVisibility(View.VISIBLE);
                relativeLayout.setPadding(0,20,0,0);
                resim_flag = 2;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity().setActivityTitle(""+getString(R.string.crop_baslik))
                        .setCropMenuCropButtonTitle(""+getString(R.string.crop_onayla))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostUpadate.this);
            }
        });
        help_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bulunamayan_edittext.setVisibility(View.VISIBLE);
                if (bulunamayan_edittext.getText().length()>0){
                    pickup_location.setText(bulunamayan_edittext.getText().toString());
                    location=bulunamayan_edittext.getText().toString();
                }
            }
        });
        //textview scrool özelliği
        deneyim.setOnTouchListener(new View.OnTouchListener() {
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
        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bulunamayan_edittext.setText("");
                bulunamayan_edittext.setVisibility(View.GONE);
                foursquare_search.setVisibility(View.GONE);
                foursquare_delete.setVisibility(View.VISIBLE);
                pickPlace();
                getLocation();
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
        foursquare_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foursquare_delete.setVisibility(View.GONE);
                foursquare_search.setVisibility(View.VISIBLE);
                pickup_location.setText("");
                location="";
            }
        });

    }

    private void pickPlace() {
        if (ulke_b == null) {
            Toast.makeText(this,R.string.konum_bekleniyor, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, PlacePicker.class);
            startActivityForResult(intent, 9001);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PlacePicker.PLACE_PICKED_RESULT_CODE) {
            Venue place = data.getParcelableExtra(PlacePicker.EXTRA_PLACE);
            pickup_location.setText(place.getName());
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
                    resim1.setPadding(0, 0, 0, 0);
                    resim1.setImageURI(FilePathUri);
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
                    resim2.setPadding(0, 0, 0, 0);
                    resim2.setImageURI(FilePathUri2);
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
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
        Toast.makeText(PostUpadate.this,R.string.konum_bilgisi_alınıyor, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(PostUpadate.this,R.string.konum_servisi_kontrol, Toast.LENGTH_SHORT).show();

    }
    public void UpdateFirebase() {// Creating UploadImageFileToFirebaseStorage method to upload image on storage.
        final String currentTime = sdf.format(cal.getTime());
        yazilar.child("kategori_name").setValue(post_kategori_text.getText().toString());
        yazilar.child("postDate").setValue(currentTime);
        yazilar.child("postExperince").setValue(deneyim.getText().toString());
        yazilar.child("postLatitude").setValue(enlem);
        yazilar.child("postLongitude").setValue(boylam);
        yazilar.child("postLocation").setValue(pickup_location.getText().toString()+""+bulunamayan_edittext.getText().toString());
        yazilar.child("postTitle").setValue(baslik.getText().toString());
        gnc_progress.setVisibility(View.GONE);
    }

}
