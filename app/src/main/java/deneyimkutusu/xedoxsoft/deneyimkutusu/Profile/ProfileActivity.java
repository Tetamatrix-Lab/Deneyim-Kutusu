package deneyimkutusu.xedoxsoft.deneyimkutusu.Profile;

import android.content.Intent;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonDeleteNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonEditNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.ItemClickSupport;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.ProfilYazilarAdapter;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Login.LoginScreen;
import deneyimkutusu.xedoxsoft.deneyimkutusu.MainActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.ProfilModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.UserModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.CategoricPostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostUpadate;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostUpload;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ButtonEditNotify, ButtonDeleteNotify {
    TextView isim;
    TextView ulke;
    TextView isim_drawer;
    TextView rutbe_drawer;
    TextView incelemS;
    TextView rutbe;
    TextView konum;
    ImageView profilRes;
    ImageView kapakRes;
    RecyclerView recyclerView;
    ImageView drawerProfile;
    ImageView drawerKapak;
    FloatingActionButton fab;
    NestedScrollView scrollView;
    FirebaseAuth auth;
    DatabaseReference databaseYazilarCurrentUser;
    DatabaseReference databaseread;
    Query mQueryUser;
    ProfilYazilarAdapter adapter;
    ArrayList<ProfilModel> yazilarGel;
    ArrayList<ProfilModel> yazilarArrayList;
    int yenidenOkuma = 0;//Pull reflesh için
    int inceleme_sayisi;
    String kullanici_id;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        isim = (TextView) findViewById(R.id.textView10);
        ulke = (TextView) findViewById(R.id.textView11);
        konum = (TextView) findViewById(R.id.textView30);
        incelemS = (TextView) findViewById(R.id.textView4);
        rutbe = (TextView) findViewById(R.id.textView2);
        profilRes = (ImageView) findViewById(R.id.imageView3);
        kapakRes = (ImageView) findViewById(R.id.imageView2);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view_profile);
        scrollView=(NestedScrollView)findViewById(R.id.profile_nested_scrool);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        kullanici_id = auth.getUid();//o anki kullanıcı id sini getirir.
        databaseread = FirebaseDatabase.getInstance().getReference().child("Uyeler").child(kullanici_id);
        databaseread.keepSynced(true);//Ofline yükleme yapmayı sağlıyor.
        databaseYazilarCurrentUser = FirebaseDatabase.getInstance().getReference("Yazilar");
        databaseYazilarCurrentUser.keepSynced(true);//Offline yükleme yapmayı sağlıyor.
        if (auth.getCurrentUser() != null) {
            ValueEventListener oku = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel profil = new UserModel();
                    profil = dataSnapshot.getValue(UserModel.class);
                    username = ("" + profil.getIsim() + " " + profil.getSoyisim());
                    isim.setText(profil.getIsim() + " " + profil.getSoyisim());
                    ulke.setText(profil.getSehir() + "/" + profil.getUlke());
                    rutbe.setText(profil.getRutbe());
                    incelemS.setText(profil.getInceleme_sayisi());
                    inceleme_sayisi = Integer.parseInt(profil.getInceleme_sayisi());
                    if (Integer.parseInt(profil.getInceleme_sayisi()) < 20) {
                        databaseread.child("rutbe").setValue("Acemi");
                    }
                    if (Integer.parseInt(profil.getInceleme_sayisi()) >= 20 && Integer.parseInt(profil.getInceleme_sayisi()) < 50) {
                        databaseread.child("rutbe").setValue("Bilgin");
                    }
                    if (Integer.parseInt(profil.getInceleme_sayisi()) >= 50) {
                        databaseread.child("rutbe").setValue("Bilmiş");
                    }
                    String pResimUrl = profil.getResimUrl();
                    String kResimUrl = profil.getKapakUrl();
                    Picasso.with(getApplicationContext()).load(pResimUrl).transform(new CropCircleTransformation()).fit().centerCrop().into(profilRes);
                    Picasso.with(getApplicationContext()).load(kResimUrl).fit().centerCrop().into(kapakRes);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseread.addValueEventListener(oku);//databaseread te verdiğimiz referansa göre oku value listenirimız sayesende okuma yapar
        } else {
        }
        //Recyclerview verilerinin hazırlanması
        yazilarGel = prepareData();
        adapter = new ProfilYazilarAdapter(ProfileActivity.this, yazilarGel);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent myIntent = new Intent(ProfileActivity.this, PostActivity.class);
                myIntent.putExtra("key", yazilarGel.get(position).getIcerik_id()); //Optional parameters
                ProfileActivity.this.startActivity(myIntent);

            }
        });
        initViews();//Recylerview set edilmesi
        fab = (FloatingActionButton) findViewById(R.id.fab_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ProfileActivity.this, PostUpload.class);
                ProfileActivity.this.startActivity(myIntent);
            }
        });
        //Fab butonun recyler scrool olduğunda gizlenmesi
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        //NavigationDrawer Kodları
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_profile);
        View headerView = navigationView.getHeaderView(0); //Navigation daki nesneleri için headerView nesnesi oluşturuldu;
        isim_drawer = (TextView) headerView.findViewById(R.id.textViewDrawer_isim_profile);
        rutbe_drawer = (TextView) headerView.findViewById(R.id.textViewDrawer_rutbe_profile);
        drawerProfile = (ImageView) headerView.findViewById(R.id.imageViewDrawer_profile_profile);
        drawerKapak = (ImageView) headerView.findViewById(R.id.imageViewDrawer_kapak_profile);
        drawerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                ProfileActivity.this.startActivity(myIntent);
            }
        });
        ValueEventListener oku2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel profil = new UserModel();
                profil = dataSnapshot.getValue(UserModel.class);
                username = ("" + profil.getIsim() + " " + profil.getSoyisim());
                isim_drawer.setText("" + profil.getIsim() + " " + profil.getSoyisim());
                rutbe_drawer.setText(""+profil.getRutbe());
                String pResimUrl = profil.getResimUrl();
                String kResimUrl = profil.getKapakUrl();
                Picasso.with(getApplicationContext()).load(pResimUrl).transform(new CropCircleTransformation()).fit().centerCrop().into(drawerProfile);
                Picasso.with(getApplicationContext()).load(kResimUrl).fit().centerCrop().into(drawerKapak);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseread.addValueEventListener(oku2);//databaseread te verdiğimiz referansa göre oku value listenirimız sayesende okuma yapar

        navigationView.setNavigationItemSelectedListener(this);;
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

    //Verilein arraylist için hazırlanması
    private ArrayList prepareData() {
        mQueryUser = databaseYazilarCurrentUser.orderByChild("uye_id").equalTo(kullanici_id);
        //final ArrayList<ProfilModel> yazilarArrayList=new ArrayList<ProfilModel>();
        yazilarArrayList = new ArrayList<ProfilModel>();
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yazilarArrayList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProfilModel post = postSnapshot.getValue(ProfilModel.class);
                    yazilarArrayList.add(0, post);
                    yenidenOkuma = 1;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mQueryUser.addValueEventListener(oku);
        return yazilarArrayList;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(ProfileActivity.this, ProfilUpdate.class);
            ProfileActivity.this.startActivity(myIntent);
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
            Intent myIntent = new Intent(ProfileActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Eğitim"); //Optional parameters
            myIntent.putExtra("username",username);
            ProfileActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_job) {
            Intent myIntent = new Intent(ProfileActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "İş Olanakları");
            myIntent.putExtra("username",username);
            ProfileActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_travel) {
            Intent myIntent = new Intent(ProfileActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Turistik");
            myIntent.putExtra("username",username);
            ProfileActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_food) {
            Intent myIntent = new Intent(ProfileActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Yemek");
            myIntent.putExtra("username",username);
            ProfileActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_country) {
            Intent myIntent = new Intent(ProfileActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Ulke");
            myIntent.putExtra("username",username);
            ProfileActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_exit) {
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onButtonEditClick(int position) {
        final String btnd_gelen_post_id = yazilarGel.get(position).getIcerik_id();
        Intent myIntent = new Intent(ProfileActivity.this, PostUpadate.class);
        myIntent.putExtra("post_id", btnd_gelen_post_id); //Optional parameters
        ProfileActivity.this.startActivity(myIntent);
    }

    @Override
    public void onButtonDeleteClick(int position) {
        final String btnd_gelen_post_id = yazilarGel.get(position).getIcerik_id();
        databaseYazilarCurrentUser.child(btnd_gelen_post_id).removeValue();
        inceleme_sayisi = inceleme_sayisi - 1;
        databaseread.child("inceleme_sayisi").setValue("" + inceleme_sayisi);

        //yazilarArrayList.remove(yazilarArrayList.get(position));
        yazilarArrayList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, yazilarArrayList.size());
        adapter.notifyDataSetChanged();
    }
    public void signOut() {
        auth.signOut();
    }
}
