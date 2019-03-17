package com.francisdeh.comdepapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by FrancisDeh on 10/27/2017.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    List<Member> mMembers;


    public MemberAdapter(List<Member> members){
        this.mMembers = members;

    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_list, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        Member member = mMembers.get(position);
        holder.setViewName(member.getName());
        holder.setViewLevel(member.getLevel());
         holder.setViewImage(member.getImage());
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder{

        View myView;
        public MemberViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setViewName(String Name){
            TextView nameView = (TextView)myView.findViewById(R.id.name_text_view);
            nameView.setText(Name);
        }

        public void setViewLevel(String Level){
            TextView levelView = (TextView)myView.findViewById(R.id.level_text_view);
            levelView.setText(Level);
        }

        public void setViewImage(String Image){
            ImageView imageView = (ImageView)myView.findViewById(R.id.profile_image);
            Picasso.with(myView.getContext()).load(Image).into(imageView);

        }
    }
}
