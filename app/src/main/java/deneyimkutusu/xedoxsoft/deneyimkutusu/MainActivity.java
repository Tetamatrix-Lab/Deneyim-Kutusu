package deneyimkutusu.xedoxsoft.deneyimkutusu;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.DataAdapter;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonCommentNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonShareNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonLikeNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.LikeTextNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.Profile_postClickNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Login.LoginScreen;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.LikeModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.UserModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.CategoricPostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostUpload;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.ProfilUpdate;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.ProfileActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.YorumProfileActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ButtonLikeNotify, ButtonShareNotify, ButtonCommentNotify,LikeTextNotify,Profile_postClickNotify {

    boolean doubleBackToExitPressedOnce = false; //geri butonu için
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    DatabaseReference databaseProfilRead;
    DatabaseReference databaseYazilarRead;
    DatabaseReference databaseLike;
    DatabaseReference Likes;
    ProgressBar progressBarmain;
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    PullRefreshLayout pullRefreshLayout;
    String kullaniciId;
    FloatingActionButton fab;
    TextView isim;
    TextView rutbe;
    ImageView drawerProfile;
    ImageView drawerKapak;
    Dialog dialog;
    DataAdapter adapter;
    ArrayList<DataClass> yazilarGel;
    ArrayList<LikeModel> likeModels;
    String username;
    private boolean mProcessClickLike = false;
    boolean userScrolled = false;
    int count = 0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        progressBarmain = (ProgressBar) findViewById(R.id.progressBarMain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();


        auth = FirebaseAuth.getInstance(); //get firebase auth instance 2 kere yazma 2 kere load oluyo ekran
        kullaniciId = auth.getUid();//şu anki kullanıcı id sini getirir.
        databaseYazilarRead = FirebaseDatabase.getInstance().getReference("Yazilar");
        databaseProfilRead = FirebaseDatabase.getInstance().getReference().child("Uyeler").child(kullaniciId);
        databaseLike = FirebaseDatabase.getInstance().getReference().child("Begeniler");
        databaseYazilarRead.keepSynced(true);//Ofline yükleme yapmayı sağlıyor.
        databaseProfilRead.keepSynced(true);
        databaseLike.keepSynced(true);
        progressBarmain.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(this));
        progressBarmain.setVisibility(View.VISIBLE);
        authListener = new FirebaseAuth.AuthStateListener() {//kullanıcı oturumundaki değişiklikleri dinler
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginScreen.class));
                    finish();
                }
            }
        };

        //Recyclerview verilerinin hazırlanması
        yazilarGel = prepareData();
        adapter = new DataAdapter(MainActivity.this, yazilarGel);
        recyclerView.setAdapter(adapter);
        //ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
        //    @Override
        //    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        //       Intent myIntent = new Intent(MainActivity.this, PostActivity.class);
        //        myIntent.putExtra("key", yazilarGel.get(position).getIcerik_id()); //Optional parameters
        //        MainActivity.this.startActivity(myIntent);
        //        //Toast.makeText(MainActivity.this, ""+yazilarGel.get(position).getIcerik_id(), Toast.LENGTH_SHORT).show();
        //    }
        //});
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                        initViews();
                    }
                }, 2000);
            }
        });
        //Fab butonun recyler scrool olduğunda gizlenmesi
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
        //Floating button
        fab = (FloatingActionButton) findViewById(R.id.fabmain); //Floating action button kodları
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, PostUpload.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        //NavigationDrawer Kodları
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);
        View headerView = navigationView.getHeaderView(0); //Navigation daki nesneleri için headerView nesnesi oluşturuldu;
        isim = (TextView) headerView.findViewById(R.id.textViewDrawer_isim);
        rutbe = (TextView) headerView.findViewById(R.id.textViewDrawer_rutbe);
        drawerProfile = (ImageView) headerView.findViewById(R.id.imageViewDrawer_profile);
        drawerKapak = (ImageView) headerView.findViewById(R.id.imageViewDrawer_kapak);
        drawerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ProfileActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
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
        databaseProfilRead.addValueEventListener(oku2);//databaseread te verdiğimiz referansa göre oku value listenirimız sayesende okuma yapar
        initViews();//Recylerview set edilmesi
        implementScrollListener();


    }

    //Recyclerview kullanımı ve adaptere inflate edilmesi
    private void initViews() {
        int resId = R.anim.layout_animation_fall_down;//item ve layout animasyonu recyclerView için
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setHasFixedSize(true);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
    }

    // Implement scroll listener scrool olduğu surece yükleme yapar
    public void implementScrollListener() {
        recyclerView
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView,
                                                     int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        // If scroll state is touch scroll then set userScrolled
                        // true
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            userScrolled = true;

                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx,
                                           int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        // Here get the child count, item count and visibleitems
                        // from layout manager
//                        if(dy > 0) //check for scroll down
//                        {
//                            visibleItemCount = mLayoutManager.getChildCount();
//                            totalItemCount = mLayoutManager.getItemCount();
//                            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
//
//                            if (userScrolled)
//                            {
//                                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
//                                {
//                                    userScrolled = false;
//                                    //Log.v("...", "Last Item Wow !");
//                                    //Do pagination.. i.e. fetch new data
//                                }
//                            }
//                        }
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                        // Now check if userScrolled is true and also check if
                        // the item is end then update recycler view and set
                        // userScrolled to false
                        if (userScrolled
                                && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                            userScrolled = false;
                            //Toast.makeText(MainActivity.this, "Scrolling", Toast.LENGTH_SHORT).show();
                            prepareData();
                        }
                    }
                });
    }

    //Verilein arraylist için hazırlanması
    private ArrayList prepareData() {
        final ArrayList<DataClass> yazilarArrayList = new ArrayList<DataClass>();
        //final DataClass dGuncelle=new DataClass();
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yazilarArrayList.clear();//Bunu yazmassan Duplicate ediyor her veritabaninda değişiklik olduğunda
//               Toast.makeText(MainActivity.this, ""+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    count = count + 1;
                    if (count >= dataSnapshot.getChildrenCount()) {
                        //stop progress bar here
                        progressBarmain.setVisibility(View.GONE);
                    }
                    DataClass post = postSnapshot.getValue(DataClass.class);
                    yazilarArrayList.add(0, post);//Başa ekleme sıkıntı çıkarsa reverse sort ara
//                   dGuncelle.setIcerik_id(post.getIcerik_id());
//                   dGuncelle.setUye_id(post.getUye_id());
//                   dGuncelle.setKategori_name(post.getKategori_name());
//                   dGuncelle.setNameSurname(post.getNameSurname());
//                   dGuncelle.setCountry(post.getCountry());
//                   dGuncelle.setPostDate(post.getPostDate());
//                   dGuncelle.setUpNumber(post.getUpNumber());
//                   dGuncelle.setDownNumber(post.getDownNumber());
//                   dGuncelle.setCommentNumber(post.getCommentNumber());
//                   dGuncelle.setPostTitle(post.getPostTitle());
//                   dGuncelle.setPostExperince(post.getPostExperince());
//                   dGuncelle.setProfileImage(post.getProfileImage());
//                   dGuncelle.setPostImage_url(post.getPostImage_url());
//                   dGuncelle.setPostImage_url2(post.getPostImage_url2());
//                   dGuncelle.setPostLatitude(post.getPostLatitude());
//                   dGuncelle.setPostLongitude(post.getPostLongitude());
//                   dGuncelle.setPostLocation(post.getPostLocation());
                    adapter.notifyDataSetChanged();
                }
//                if (!dataSnapshot.hasChildren() && refleshMain==0){
//                    initViews();
//                    refleshMain=1;
//                    //Toast.makeText(MainActivity.this, "veri tabanında yazı yok", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseYazilarRead.addValueEventListener(oku);
        return yazilarArrayList;
    }
    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(MainActivity.this, ProfilUpdate.class);
            MainActivity.this.startActivity(myIntent);
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
            Intent myIntent = new Intent(MainActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Eğitim"); //Optional parameters
            myIntent.putExtra("username",username);
            MainActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_job) {
            Intent myIntent = new Intent(MainActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "İş Olanakları");
            myIntent.putExtra("username",username);
            MainActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_travel) {
            Intent myIntent = new Intent(MainActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Turistik");
            myIntent.putExtra("username",username);
            MainActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_food) {
            Intent myIntent = new Intent(MainActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Yemek");
            myIntent.putExtra("username",username);
            MainActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_country) {
            Intent myIntent = new Intent(MainActivity.this, CategoricPostActivity.class);
            myIntent.putExtra("kategori", "Ulke");
            myIntent.putExtra("username",username);
            MainActivity.this.startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_exit) {
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Uygulamadan çıkmak için birkez daha basınız", Toast.LENGTH_SHORT).show();
            Toast.makeText(this,R.string.basmak_gerek, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Arama");
        search(searchView);
        return true;
    }
    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onButtonLikeClick(int position) {
        mProcessClickLike = true;
        final String btnd_gelen_post_id = yazilarGel.get(position).getIcerik_id();
        final int anlik_like_sayisi = Integer.parseInt(yazilarGel.get(position).getUpNumber());
        databaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LikeModel like = new LikeModel();
                like.setAd_soyad(username);
                like.setPost_id(btnd_gelen_post_id);
                like.setKul_id(kullaniciId);
                if (mProcessClickLike) { //Kullanıcı daha önce beğendi ise
                    if (dataSnapshot.child(btnd_gelen_post_id).hasChild(kullaniciId)) {
                        dataSnapshot.getRef().child(btnd_gelen_post_id).child(kullaniciId).removeValue();
                        databaseYazilarRead.child(btnd_gelen_post_id).child("upNumber").setValue("" + String.valueOf(anlik_like_sayisi - 1));
                        mProcessClickLike = false;
                    } else {
                        databaseLike.child(btnd_gelen_post_id).child(kullaniciId).setValue(like);
                        databaseYazilarRead.child(btnd_gelen_post_id).child("upNumber").setValue("" + String.valueOf(anlik_like_sayisi + 1));
                        mProcessClickLike = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //Toast.makeText(this, "adapterViewden gelen ekrana bass"+yazilarGel.get(position).getPostTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonShareClick(int position) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "deneyim://kutusu/deeblink";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void onButtonCommentClick(int position) {
        final String btnd_gelen_post_id = yazilarGel.get(position).getIcerik_id();
        Intent myIntent = new Intent(MainActivity.this, PostActivity.class);
        myIntent.putExtra("key", btnd_gelen_post_id); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    public void onButtonTextLikeClick(final int position) {
        final String btnd_gelen_post_id = yazilarGel.get(position).getIcerik_id();
        CustomAlertDialog(btnd_gelen_post_id);
    }
    public void CustomAlertDialog(String post_id) {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_likes);
        //dialog.setTitle("Beğenen kişiler");
        Button iptalb = (Button) dialog.findViewById(R.id.button12);
        ListView list=(ListView)dialog.findViewById(R.id.dialog_listview_likes);
        iptalb.setEnabled(true);
        iptalb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();

        final ArrayList<String> liste=new ArrayList<>();
        final ArrayAdapter<String> adapter_list=new ArrayAdapter<String>(this,R.layout.like_listview_item,liste);
        list.setAdapter(adapter_list);
        Likes = FirebaseDatabase.getInstance().getReference().child("Begeniler").child(post_id);
        Likes.keepSynced(true);//Ofline yükleme yapmayı sağlıyor.
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                liste.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    LikeModel likes = postSnapshot.getValue(LikeModel.class);
                    liste.add(likes.getAd_soyad());
                    adapter_list.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Likes.addValueEventListener(oku);
    }

    @Override
    public void onButtonPicClick(int position) {
        final String btnd_gelen_kul_id = yazilarGel.get(position).getUye_id();
        if (kullaniciId.equals(btnd_gelen_kul_id)) {
            Intent myIntent = new Intent(MainActivity.this, ProfileActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(MainActivity.this, YorumProfileActivity.class);
            myIntent.putExtra("kullanıcı_key", btnd_gelen_kul_id); //Optional parameters
            MainActivity.this.startActivity(myIntent);
        }

    }
}
