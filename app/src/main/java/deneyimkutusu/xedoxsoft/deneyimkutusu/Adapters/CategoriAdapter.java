package deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonCommentNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonLikeNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Adapters.Interfaceler.ButtonShareNotify;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Model.DataClass;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.CategoricPostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.Post.PostActivity;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CategoriAdapter extends RecyclerView.Adapter<CategoriAdapter.ViewHolder> implements Filterable{
    private ArrayList<DataClass> postListesi;
    private ArrayList<DataClass> postfilteredListesi;
    private Context context;
    ButtonLikeNotify buttonLikeNotify;
    ButtonShareNotify buttonShareNotify;
    ButtonCommentNotify buttonCommentNotify;
    public CategoriAdapter(Context context,ArrayList<DataClass> postListesi) {
        this.context = context;
        this.postListesi = postListesi;
        postfilteredListesi=postListesi;

        try {
            buttonLikeNotify=(CategoricPostActivity)context;
            buttonShareNotify =(CategoricPostActivity)context;
            buttonCommentNotify=(CategoricPostActivity)context;
        }catch (Throwable e){
            //İnterface implemen edilemesse
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_row_layout, viewGroup, false);
        final ViewHolder mViewHolder=new ViewHolder(view);
        return mViewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.nameSurname.setText(postfilteredListesi.get(position).getNameSurname());
        viewHolder.surname.setText(postfilteredListesi.get(position).getSurname());
        viewHolder.country.setText(postfilteredListesi.get(position).getCountry());
        viewHolder.city.setText(postfilteredListesi.get(position).getCity());
        viewHolder.postDate.setText(postfilteredListesi.get(position).getPostDate());
        viewHolder.postTitle.setText(postfilteredListesi.get(position).getPostTitle());
        viewHolder.postUpNumber.setText(postfilteredListesi.get(position).getUpNumber());
        //viewHolder.postDownNumber.setText(postListesi.get(position).getDownNumber());
        viewHolder.postCommentNumber.setText(postfilteredListesi.get(position).getCommentNumber());
        //Picasso.with(context).load(postListesi.get(i).getProfileImage()).into(viewHolder.profilePic);
        Picasso.with(context).load(postfilteredListesi.get(position).getPostImage_url()).transform(new RoundedCornersTransformation(5,1)).fit().centerCrop().into(viewHolder.postPic);
        Picasso.with(context).load(postfilteredListesi.get(position).getProfileImage()).transform(new RoundedCornersTransformation(50,1)).fit().centerCrop().into(viewHolder.profilePic);

        //Like,Dislike,Comment butonlarına custom adapter içinde tıklanma olayı
        viewHolder.setLikeButton(postfilteredListesi.get(position).getIcerik_id());//Beğeni tıklanma olayı için anlık postun metodunu tanımlama
        //viewHolder.setdisLikeButton(postListesi.get(position).getIcerik_id());
        viewHolder.postUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonLikeNotify.onButtonLikeClick(position);
                }catch (Throwable e){
                    //interface can be null
                }
            }
        });
        viewHolder.postDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonShareNotify.onButtonShareClick(position);
                }catch (Throwable e){
                    //interface can be null
                }
            }
        });
        viewHolder.postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonCommentNotify.onButtonCommentClick(position);
                }catch (Throwable e){
                    //interface can be null
                }
            }
        });
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("key", postfilteredListesi.get(position).getIcerik_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postfilteredListesi.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    postfilteredListesi = postListesi;
                } else {
                    ArrayList<DataClass> filteredList = new ArrayList<>();
                    for (DataClass androidVersion : postListesi) {
                        //
                        if (androidVersion.getPostTitle().toLowerCase().contains(charString) || androidVersion.getCountry().toLowerCase().contains(charString)
                                || androidVersion.getKategori_name().toLowerCase().contains(charString)||androidVersion.getCity().toLowerCase().contains(charString)
                                ||androidVersion.getNameSurname().toLowerCase().contains(charString)) {
                            filteredList.add(androidVersion);
                        }
                    }
                    postfilteredListesi = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = postfilteredListesi;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                postfilteredListesi = (ArrayList<DataClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        DatabaseReference mDatabaseReference;
        FirebaseAuth mAuth;
        RelativeLayout parentLayout;
        TextView nameSurname;
        TextView surname;
        TextView country;
        TextView city;
        TextView postDate;
        ImageView profilePic;
        ImageView postPic;
        TextView postTitle;
        TextView postUpNumber;
        //TextView postDownNumber;
        TextView postCommentNumber;
        Button postUpButton;
        Button postDownButton;
        Button postCommentButton;
        public ViewHolder(View view) {
            super(view);
            mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Begeniler");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseReference.keepSynced(true);
            //Listview de bulunan nesnelerin tanımlanması
            nameSurname=(TextView)view.findViewById(R.id.textView15);
            surname=(TextView)view.findViewById(R.id.textView_surname);
            country=(TextView)view.findViewById(R.id.textView18);
            city=(TextView)view.findViewById(R.id.textView_city);
            postDate=(TextView)view.findViewById(R.id.textView19);
            profilePic=(ImageView)view.findViewById(R.id.imageView9);
            postPic=(ImageView)view.findViewById(R.id.imageView8);
            postTitle=(TextView)view.findViewById(R.id.textView35);
            postUpNumber=(TextView)view.findViewById(R.id.textView32);
            //postDownNumber=(TextView)view.findViewById(R.id.textView34);
            postCommentNumber=(TextView)view.findViewById(R.id.textView33);
            postUpButton=(Button)view.findViewById(R.id.button15);
            postDownButton=(Button)view.findViewById(R.id.button16);
            postCommentButton=(Button)view.findViewById(R.id.button34);
            parentLayout=(RelativeLayout)view.findViewById(R.id.item_relative);

        }
        //Beğeni butonu resmi değiştirir firebaseden gelen post idnin beğendi bilgisine göre
        public void setLikeButton(final String post_key){

            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        postUpButton.setBackgroundResource(R.drawable.like_dolu);

                    }else{
                        postUpButton.setBackgroundResource(R.drawable.like);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
