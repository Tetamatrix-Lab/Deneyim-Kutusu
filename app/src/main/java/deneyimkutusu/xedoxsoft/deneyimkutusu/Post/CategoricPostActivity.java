package deneyimkutusu.xedoxsoft.deneyimkutusu.Post;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.CategoriAdapter;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonCommentNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonLikeNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonShareNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;


public class CategoricPostActivity extends AppCompatActivity implements ButtonLikeNotify, ButtonShareNotify, ButtonCommentNotify {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    DatabaseReference databaseProfilRead;
    DatabaseReference databaseYazilarRead;
    DatabaseReference databaseLike;
    Query mQueryKategori;
    ProgressBar progressBarmain;
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    PullRefreshLayout pullRefreshLayout;
    String kullaniciId;
    CategoriAdapter adapter;
    ArrayList<DataClass> yazilarGel;
    String username;
    private boolean mProcessClickLike = false;
    int count = 0;
    String kategori;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoric_post);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view_kategoric);
        kategori=getIntent().getStringExtra("kategori");
        username=getIntent().getStringExtra("username");
        auth = FirebaseAuth.getInstance();
        kullaniciId = auth.getUid();
        databaseYazilarRead = FirebaseDatabase.getInstance().getReference("Yazilar");
        databaseProfilRead = FirebaseDatabase.getInstance().getReference().child("Uyeler").child(kullaniciId);
        databaseLike = FirebaseDatabase.getInstance().getReference().child("Begeniler");
        databaseYazilarRead.keepSynced(true);
        databaseProfilRead.keepSynced(true);
        databaseLike.keepSynced(true);
        yazilarGel = prepareData();
        adapter = new CategoriAdapter(CategoricPostActivity.this, yazilarGel);
        recyclerView.setAdapter(adapter);
//        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                Intent myIntent = new Intent(CategoricPostActivity.this, PostActivity.class);
//                myIntent.putExtra("key", yazilarGel.get(position).getIcerik_id()); //Optional parameters
//                CategoricPostActivity.this.startActivity(myIntent);
//            }
//        });
        initViews();
    }
    private void initViews() {
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
    }
    private ArrayList prepareData() {
        mQueryKategori = databaseYazilarRead.orderByChild("kategori_name").equalTo(kategori);
        final ArrayList<DataClass> yazilarArrayList = new ArrayList<DataClass>();
        ValueEventListener oku = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yazilarArrayList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    count = count + 1;
                    if (count >= dataSnapshot.getChildrenCount()) {
                    }
                    DataClass post = postSnapshot.getValue(DataClass.class);
                    yazilarArrayList.add(0, post);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (kategori.equals("Ulke")){
            databaseYazilarRead.addValueEventListener(oku);
        }
        else
            mQueryKategori.addValueEventListener(oku);
        return yazilarArrayList;
    }
    @Override
    public void onButtonLikeClick(int position) {
        mProcessClickLike = true;
        final String btnd_gelen_post_id = yazilarGel.get(position).getIcerik_id();
        final int anlik_like_sayisi = Integer.parseInt(yazilarGel.get(position).getUpNumber());
        databaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessClickLike) { //Kullanıcı daha önce beğendi ise
                    if (dataSnapshot.child(btnd_gelen_post_id).hasChild(kullaniciId)) {
                        dataSnapshot.getRef().child(btnd_gelen_post_id).child(kullaniciId).removeValue();
                        databaseYazilarRead.child(btnd_gelen_post_id).child("upNumber").setValue("" + String.valueOf(anlik_like_sayisi - 1));
                        mProcessClickLike = false;
                    } else {
                        databaseLike.child(btnd_gelen_post_id).child(kullaniciId).setValue(username);
                        databaseYazilarRead.child(btnd_gelen_post_id).child("upNumber").setValue("" + String.valueOf(anlik_like_sayisi + 1));
                        mProcessClickLike = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onButtonShareClick(int position) {
    }

    @Override
    public void onButtonCommentClick(int position) {
        final String btnd_gelen_post_id = yazilarGel.get(position).getIcerik_id();
        Intent myIntent = new Intent(CategoricPostActivity.this, PostActivity.class);
        myIntent.putExtra("key", btnd_gelen_post_id); //Optional parameters
        CategoricPostActivity.this.startActivity(myIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
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
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }
}
