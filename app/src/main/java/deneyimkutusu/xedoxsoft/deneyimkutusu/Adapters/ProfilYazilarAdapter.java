package deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonDeleteNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonEditNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.ProfilModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Profile.ProfileActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by Erdem Gençoğlu on 7.02.2018.
 */

public class ProfilYazilarAdapter extends RecyclerView.Adapter<ProfilYazilarAdapter.ViewHolder>{
    private ArrayList<ProfilModel> postListesi;
    private Context context;
    ButtonDeleteNotify buttonDeleteNotify;
    ButtonEditNotify buttonEditNotify;
    public ProfilYazilarAdapter(Context context, ArrayList<ProfilModel> postListesi) {
        this.context = context;
        this.postListesi = postListesi;

        try {
            buttonDeleteNotify=(ProfileActivity)context;
            buttonEditNotify =(ProfileActivity)context;
        }catch (Throwable e){
            //İnterface implemen edilemesse
        }
    }

    @Override
    public ProfilYazilarAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profil_row_layout, viewGroup, false);
        final ViewHolder mViewHolder=new ViewHolder(view);
        return mViewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.postTitle.setText(postListesi.get(position).getPostTitle());
        viewHolder.location.setText(postListesi.get(position).getPostLocation());
        Picasso.with(context).load(postListesi.get(position).getPostImage_url()).transform(new RoundedCornersTransformation(50,1)).fit().centerCrop().into(viewHolder.postPic);

        viewHolder.postDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonDeleteNotify.onButtonDeleteClick(position);
                }catch (Throwable e){
                    //interface can be null
                }
            }
        });
        viewHolder.postEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonEditNotify.onButtonEditClick(position);
                }catch (Throwable e){
                    //interface can be null
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int size= postListesi.size();
        return size;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postPic;
        TextView postTitle;
        TextView location;
        Button postEditButton;
        Button postDeleteButton;

        public ViewHolder(View view) {
            super(view);
            postPic=(ImageView)view.findViewById(R.id.imageView5);
            postTitle=(TextView)view.findViewById(R.id.textView26);
            location=(TextView)view.findViewById(R.id.textView30);
            postEditButton=(Button)view.findViewById(R.id.button7);
            postDeleteButton=(Button)view.findViewById(R.id.button8);
        }
    }
}
