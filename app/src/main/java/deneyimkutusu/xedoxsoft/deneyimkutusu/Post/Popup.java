package deneyimkutusu.xedoxsoft.deneyimkutusu.Post;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class Popup extends AppCompatActivity {
    DatabaseReference yorumlar;
    DatabaseReference yazilar;
    String yorum_id;
    String post_id;
    int anlik_comment_sayisi=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_popup);
        Button delete=(Button)findViewById(R.id.button11);
        Button duzenle=(Button)findViewById(R.id.button10);
        final ImageView profil=(ImageView) findViewById(R.id.imageView11_yrm);
        final EditText yorum=(EditText)findViewById(R.id.editText28_yrm);
        final TextView isim=(TextView)findViewById(R.id.textView_yrm);
        yorumlar = FirebaseDatabase.getInstance().getReference().child("Kisi_yorumlari");
        yorumlar.keepSynced(true);
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        yorum_id = getIntent().getStringExtra("yorum_id");
        post_id = getIntent().getStringExtra("post_id");
        yazilar = FirebaseDatabase.getInstance().getReference().child("Yazilar").child(post_id);
        yazilar.keepSynced(true);
        yazilar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataClass dt = new DataClass();
                dt = dataSnapshot.getValue(DataClass.class);
                anlik_comment_sayisi=Integer.parseInt(dt.getCommentNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        yorumlar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url=dataSnapshot.child(yorum_id).child("kisi_resim_url").getValue().toString();
                String kisi_adi=dataSnapshot.child(yorum_id).child("uye_adi").getValue().toString();
                isim.setText(kisi_adi);
                yorum.setText(dataSnapshot.child(yorum_id).child("yorum").getValue().toString());
                Picasso.with(getApplicationContext()).load(url).transform(new RoundedCornersTransformation(100,1)).fit().centerCrop().into(profil);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.9),(int)(height*.4));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yorumlar.child(yorum_id).removeValue();
                anlik_comment_sayisi=anlik_comment_sayisi-1;
                yazilar.child("commentNumber").setValue(""+anlik_comment_sayisi);
                finish();
            }
        });
        duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yorumlar.child(yorum_id).child("yorum").setValue(yorum.getText().toString());
                finish();
            }
        });
    }
}
