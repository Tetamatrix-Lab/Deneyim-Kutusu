package deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonCommentProfile;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.SpinnerTextNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.YorumModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class YorumAdapter extends RecyclerView.Adapter<YorumAdapter.ViewHolder>{
    private ArrayList<YorumModel> yorumListesi;
    private Context context;
    ButtonCommentProfile buttonCommentProfile;
    SpinnerTextNotify spinnerTextNotify;
    public YorumAdapter(Context context,ArrayList<YorumModel> yorumListesi) {
        this.context = context;
        this.yorumListesi = yorumListesi;
        try {
            buttonCommentProfile=(PostActivity)context;
            spinnerTextNotify=(PostActivity)context;
        }catch (Throwable e){
            //İnterface implemen edilemesse
        }
    }

    @Override
    public YorumAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.yorum_row, viewGroup, false);
        final ViewHolder mViewHolder=new ViewHolder(view);
        return mViewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.username.setText(yorumListesi.get(position).getUye_adi());
        viewHolder.comment.setText(Html.fromHtml("<b>"+yorumListesi.get(position).getUye_adi()+": </b>"+yorumListesi.get(position).getYorum()));
        Picasso.with(context).load(yorumListesi.get(position).getKisi_resim_url()).transform(new RoundedCornersTransformation(100,1)).fit().centerCrop().into(viewHolder.profilPic);
        //Yorumlar kısmında profil resme tıklama
        viewHolder.profilPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonCommentProfile.onButtonCommentProfileClick(position);
                }catch (Throwable e){
                    //interface can be null
                }
            }
        });
        if (viewHolder.kul_id.equals(yorumListesi.get(position).getUye_id())){
            viewHolder.yorum_spinner_text.setVisibility(View.VISIBLE);
        }else{
            viewHolder.yorum_spinner_text.setVisibility(View.GONE);
        }
        viewHolder.yorum_spinner_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    spinnerTextNotify.onButtonSpinnerClick(position);
                }catch (Throwable e){
                    //interface can be null
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return yorumListesi.size();
    }


public static class ViewHolder extends RecyclerView.ViewHolder {
    FirebaseAuth mAuth;
    ImageView profilPic;
    TextView username;
    TextView comment;
    TextView yorum_spinner_text;
    String kul_id;
    public ViewHolder(View view) {
        super(view);
        mAuth=FirebaseAuth.getInstance();
        profilPic=(ImageView)view.findViewById(R.id.imageView11);
        username=(TextView)view.findViewById(R.id.textView27);
        comment=(TextView)view.findViewById(R.id.textView28);
        yorum_spinner_text=(TextView) view.findViewById(R.id.yorum_spinner_text);
        kul_id=mAuth.getCurrentUser().getUid();
    }
}
}
