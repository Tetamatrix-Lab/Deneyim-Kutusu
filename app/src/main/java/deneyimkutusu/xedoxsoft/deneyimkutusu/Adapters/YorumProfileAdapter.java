package deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.ProfilModel;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class YorumProfileAdapter extends RecyclerView.Adapter<YorumProfileAdapter.ViewHolder> {
    private ArrayList<ProfilModel> postListesi;
    private Context context;

    public YorumProfileAdapter(Context context, ArrayList<ProfilModel> postListesi) {
        this.context = context;
        this.postListesi = postListesi;
    }

    @Override
    public YorumProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.yorum_profil_row, viewGroup, false);
        final ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.postTitle.setText(postListesi.get(position).getPostTitle());
        viewHolder.location.setText(postListesi.get(position).getPostLocation());
        Picasso.with(context).load(postListesi.get(position).getPostImage_url()).transform(new RoundedCornersTransformation(50, 1)).fit().centerCrop().into(viewHolder.postPic);
    }

    @Override
    public int getItemCount() {
        return postListesi.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postPic;
        TextView postTitle;
        TextView location;

        public ViewHolder(View view) {
            super(view);
            postPic = (ImageView) view.findViewById(R.id.imageView5);
            postTitle = (TextView) view.findViewById(R.id.textView26);
            location = (TextView) view.findViewById(R.id.textView30);

        }
    }
}
