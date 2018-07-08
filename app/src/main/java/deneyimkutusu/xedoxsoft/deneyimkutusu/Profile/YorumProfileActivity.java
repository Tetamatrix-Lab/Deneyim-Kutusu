package deneyimkutusu.xedoxsoft.deneyimkutusu.Profile;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.ItemClickSupport;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.YorumProfileAdapter;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Login.LoginScreen;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.ProfilModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.UserModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostUpload;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class YorumProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference databaseYazilarCurrentUser;
    DatabaseReference databaseread;
    Query mQueryUser;
    RecyclerView recyclerView;
    String kullanici_id;
    TextView isim;
    TextView ulke;
    TextView incelemS;
    TextView rutbe;
    ImageView profilRes;
    ImageView kapakRes;
    YorumProfileAdapter adapter;
    ArrayList<ProfilModel> yazilarGel;
    String pResimUrl;
    int yenidenOkuma = 0;//Pull reflesh için

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_profile);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isim = (TextView) findViewById(R.id.textView10);
        ulke = (TextView) findViewById(R.id.textView11);
        incelemS = (TextView) findViewById(R.id.textView4);
        rutbe = (TextView) findViewById(R.id.textView2);
        profilRes = (ImageView) findViewById(R.id.imageView3);
        kapakRes = (ImageView) findViewById(R.id.imageView2);

        auth = FirebaseAuth.getInstance();
        databaseYazilarCurrentUser = FirebaseDatabase.getInstance().getReference("Yazilar");
        kullanici_id = getIntent().getStringExtra("kullanıcı_key");
        databaseYazilarCurrentUser.keepSynced(true);//Offline yükleme yapmayı sağlıyor.
        if (auth.getCurrentUser() != null) {
            databaseread = FirebaseDatabase.getInstance().getReference().child("Uyeler").child(kullanici_id);
            databaseread.keepSynced(true);//Ofline yükleme yapmayı sağlıyor.
            ValueEventListener oku = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel profil = new UserModel();
                    profil = dataSnapshot.getValue(UserModel.class);
                    isim.setText(profil.getIsim() + " " + profil.getSoyisim());
                    ulke.setText(profil.getSehir() + "/" + profil.getUlke());
                    pResimUrl = profil.getResimUrl();
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
        adapter = new YorumProfileAdapter(YorumProfileActivity.this, yazilarGel);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent myIntent = new Intent(YorumProfileActivity.this, PostActivity.class);
                myIntent.putExtra("key", yazilarGel.get(position).getIcerik_id()); //Optional parameters
                YorumProfileActivity.this.startActivity(myIntent);
            }
        });
        initViews();//Recylerview set edilmesi
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
        final ArrayList<ProfilModel> yazilarArrayList = new ArrayList<ProfilModel>();
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
        super.onBackPressed();
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
