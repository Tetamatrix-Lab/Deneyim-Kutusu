package deneyimkutusu.xedoxsoft.deneyimkutusu.Post;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonCommentProfile;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.SpinnerTextNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.YorumAdapter;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Login.LoginScreen;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.UserModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.YorumModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.ProfilUpdate;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.ProfileActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.YorumProfileActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener, ButtonCommentProfile, SpinnerTextNotify {
    RecyclerView recyclerView;
    Button yolTarifi;
    Button send;
    EditText yorum;
    TextView icerik;
    TextView baslik;
    TextView kategori;
    TextView ekresim;
    ImageView presim1;
    ImageView presim2;
    TextView isim;
    TextView rutbe;
    ImageView drawerProfile;
    ImageView drawerKapak;
    NestedScrollView nestedScrollView;
    private FirebaseAuth auth;
    LocationManager locationManager;
    FloatingActionButton fab;
    Query mQueryYorum;
    DatabaseReference yorumlar_database;
    DatabaseReference yazilar_database;
    DatabaseReference databaseread;
    DatabaseReference databaseProfilRead;

    YorumAdapter adapter;
    ArrayList<YorumModel> yorumlar_gel;
    ArrayList<YorumModel> yorumlarArrayList;
    String enlem, boylam;
    String post_id;
    LinearLayout linearLayout;
    String kullaniciId;
    String yorumId;
    String kisi_adi;
    String kisi_resim_url;
    String username;
    int flag = 0;
    int anlik_comment_sayisi=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        post_id = getIntent().getStringExtra("key");
        send = (Button) findViewById(R.id.button9);
        baslik = (TextView) findViewById(R.id.textView5);
        kategori = (TextView) findViewById(R.id.textView6);
        icerik = (TextView) findViewById(R.id.textView12);
        yorum = (EditText) findViewById(R.id.editText9);
        ekresim = (TextView) findViewById(R.id.textView13);
        presim1 = (ImageView) findViewById(R.id.imageView6);
        presim2 = (ImageView) findViewById(R.id.imageView7);
        yolTarifi = (Button) findViewById(R.id.button2);
        linearLayout = (LinearLayout) findViewById(R.id.content_post_linear);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view_post);
        nestedScrollView=(NestedScrollView)findViewById(R.id.nested_content_post);
        yorumlar_database = FirebaseDatabase.getInstance().getReference("Kisi_yorumlari");
        yazilar_database = FirebaseDatabase.getInstance().getReference().child("Yazilar").child(post_id);
        yazilar_database.keepSynced(true);
        yorumlar_database.keepSynced(true);
        linearLayout.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//Activity çalıştığında otomatik klavye açılmasını önleme
        if (auth.getCurrentUser() != null) {
            kullaniciId = auth.getUid();//şu anki kullanıcı id sini getirir.
            databaseread = FirebaseDatabase.getInstance().getReference().child("Uyeler").child(kullaniciId);
            databaseread.keepSynced(true);//Ofline yükleme yapmayı sağlıyor.
            ValueEventListener oku = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel profil = new UserModel();
                    profil = dataSnapshot.getValue(UserModel.class);
                    kisi_adi = "" + profil.getIsim() + " " + profil.getSoyisim();
                    kisi_resim_url = profil.getResimUrl();
                    //Picasso.with(getApplicationContext()).load(pResimUrl).transform(new CropCircleTransformation()).fit().centerCrop().into(profilRes);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseread.addValueEventListener(oku);//databaseread te verdiğimiz referansa göre oku value listenirimız sayesende okuma yapar
        } else {
        }
        yazilar_database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataClass dt = new DataClass();
                dt = dataSnapshot.getValue(DataClass.class);
                baslik.setText(dt.getPostTitle());
                kategori.setText(dt.getKategori_name());
                icerik.setText(dt.getPostExperince());
                enlem = dt.getPostLatitude();
                boylam = dt.getPostLongitude();
                anlik_comment_sayisi=Integer.parseInt(dt.getCommentNumber());
                String resim1 = dt.getPostImage_url();
                String resim2 = dt.getPostImage_url2();
                Picasso.with(getApplicationContext()).load(resim1).fit().centerCrop().into(presim1);
                if (resim2.equals("null")) {
                    presim2.setVisibility(View.GONE);
                    ekresim.setVisibility(View.GONE);
                    //imageview have no image
                } else {
                    Picasso.with(getApplicationContext()).load(resim2).fit().centerCrop().into(presim2);
                    //imageview have image
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Gps acikmi onun kontrolü
        //if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        //}
        yolTarifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                //Toast.makeText(PostActivity.this, ""+enlem+""+boylam+"", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + enlem + "," + "" + boylam));
                startActivity(intent);

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yorum.getText().length()>0){
                    yorumId = yorumlar_database.push().getKey();//Yazilar için unique id olşturur.
                    yorumlar_database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            YorumModel ym = new YorumModel();
                            ym.setYorum_id(yorumId);
                            ym.setYorum(yorum.getText().toString());
                            ym.setUye_id(kullaniciId);
                            ym.setPost_id(post_id);
                            ym.setKisi_resim_url(kisi_resim_url);
                            ym.setUye_adi(kisi_adi);
                            yorumlar_database.child(yorumId).setValue(ym);
                            anlik_comment_sayisi=anlik_comment_sayisi+1;
                            yazilar_database.child("commentNumber").setValue(""+anlik_comment_sayisi);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    YoYo.with(Techniques.SlideOutDown)
                            .duration(700)
                            .playOn(findViewById(R.id.content_post_linear));
                    linearLayout.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);

                }
                else{
                    Snackbar.make(view,R.string.yorum_bos ,Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fabpost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                YoYo.with(Techniques.SlideInUp)
                        .duration(500)
                        .playOn(findViewById(R.id.content_post_linear));
                linearLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                yorum.setText("");
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        //Fab butonun recyler scrool olduğunda gizlenmesi
//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY > oldScrollY) {
//                    fab.hide();
//                } else {
//                    fab.show();
//                }
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_post);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_post);
        View headerView = navigationView.getHeaderView(0); //Navigation daki nesneleri için headerView nesnesi oluşturuldu;
        isim = (TextView) headerView.findViewById(R.id.textViewDrawer_isim_post);
        rutbe = (TextView) headerView.findViewById(R.id.textViewDrawer_rutbe_post);
        drawerProfile = (ImageView) headerView.findViewById(R.id.postDrawerProfile);
        drawerKapak = (ImageView) headerView.findViewById(R.id.imageViewDrawer_kapak_post);
        drawerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PostActivity.this, ProfileActivity.class);
                PostActivity.this.startActivity(myIntent);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        databaseProfilRead = FirebaseDatabase.getInstance().getReference().child("Uyeler").child(kullaniciId);
        databaseProfilRead.keepSynced(true);
        ValueEventListener oku2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel profil = new UserModel();
                profil = dataSnapshot.getValue(UserModel.class);
                username = ("" + profil.getIsim() + " " + profil.getSoyisim());
                isim.setText("" + profil.getIsim() + " " + profil.getSoyisim());
                rutbe.setText(""+profil.getRutbe());
                String pResimUrl = profil.getResimUrl();
                String kResimUrl = profil.getKapakUrl();
                Picasso.with(getApplicationContext()).load(pResimUrl).transform(new CropCircleTransformation()).fit().centerCrop().into(drawerProfile);
                Picasso.with(getApplicationContext()).load(kResimUrl).fit().centerCrop().into(drawerKapak);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseProfilRead.addValueEventListener(oku2);

        yorumlar_gel = prepareData();//Recyclerview verilerinin hazırlanması
        adapter = new YorumAdapter(PostActivity.this, yorumlar_gel);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        initViews();

    }

    //Recyclerview kullanımı ve adaptere inflate edilmesi
    private void initViews() {
        int resId = R.anim.layout_animation_slide_right;//item ve layout animasyonu recyclerView için
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private ArrayList prepareData() {
        mQueryYorum = yorumlar_database.orderByChild("post_id").equalTo(post_id);
        yorumlarArrayList = new ArrayList<YorumModel>();
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yorumlarArrayList.clear();//Bunu yazmassan Duplicate ediyor her veritabaninda değişiklik olduğunda
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    YorumModel yorum = postSnapshot.getValue(YorumModel.class);
                    yorumlarArrayList.add(yorum);//Başa ekleme sıkıntı çıkarsa reverse sort ara
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mQueryYorum.addValueEventListener(oku);
        return yorumlarArrayList;
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_post);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (flag == 1) {
                flag = 0;
            }
            if (flag == 0) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(PostActivity.this, ProfilUpdate.class);
            PostActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_education) {
            Intent myIntent = new Intent(PostActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Eğitim"); //Optional parameters
            myIntent.putExtra("username",username);
            PostActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_job) {
            Intent myIntent = new Intent(PostActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "İş Olanakları");
            myIntent.putExtra("username",username);
            PostActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_travel) {
            Intent myIntent = new Intent(PostActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Turistik");
            myIntent.putExtra("username",username);
            PostActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_food) {
            Intent myIntent = new Intent(PostActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Yemek");
            myIntent.putExtra("username",username);
            PostActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_country) {
            Intent myIntent = new Intent(PostActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Ulke");
            myIntent.putExtra("username",username);
            PostActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_exit) {
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_post);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //sign out method
    public void signOut() {
        auth.signOut();
        Intent myIntent = new Intent(PostActivity.this, LoginScreen.class);
        PostActivity.this.startActivity(myIntent);
    }
    @Override
    public void onLocationChanged(Location location) {
        //enlem=String.valueOf(location.getLatitude());
        //boylam=String.valueOf(location.getLongitude());
        //Toast.makeText(this, "enlem:"+enlem+" boylam"+boylam, Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (Exception e) {

        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
        //Toast.makeText(PostActivity.this, "Lütfen internetinizi ve gps iniz açınız", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonCommentProfileClick(int position) {
        //final String btnd_gelen_yorum_id=yorumlar_gel.get(position).getYorum_id();
        final String btnd_gelen_kul_id = yorumlar_gel.get(position).getUye_id();
        if (kullaniciId.equals(btnd_gelen_kul_id)) {
            Intent myIntent = new Intent(PostActivity.this, ProfileActivity.class);
            PostActivity.this.startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(PostActivity.this, YorumProfileActivity.class);
            myIntent.putExtra("kullanıcı_key", btnd_gelen_kul_id); //Optional parameters
            PostActivity.this.startActivity(myIntent);
            //Toast.makeText(this, ""+btnd_gelen_kul_id, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onButtonSpinnerClick(int position) {
        final String btnd_gelen_yorum_id = yorumlar_gel.get(position).getYorum_id();
        final String btnd_gelen_post_id = yorumlar_gel.get(position).getPost_id();
        Intent myIntent = new Intent(PostActivity.this, Popup.class);
        myIntent.putExtra("yorum_id", btnd_gelen_yorum_id); //Optional parameters
        myIntent.putExtra("post_id", btnd_gelen_post_id); //Optional parameters
        PostActivity.this.startActivity(myIntent);
        //mQueryPost=databaseYazilarCurrentUser.orderByChild("icerik_id").equalTo(btnd_gelen_post_id);
        //yorumlar_database.child(btnd_gelen_yorum_id).removeValue();
        //yorumlarArrayList.remove(position);
        //adapter.notifyItemRemoved(position);
        //adapter.notifyItemRangeChanged(position, yorumlarArrayList.size());
        //adapter.notifyDataSetChanged();
    }


}
