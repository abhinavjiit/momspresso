package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;

public class Recycle extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context c;
    String[] str = {"Home", "Videos", "100 words Story", "Support Groups","" ,"Bookmarks","" ,"Settings"};

    public Recycle(Context context) {
        this.c = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1: {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_drawer, parent, false);
                programmingviewholder rowone = new programmingviewholder(view1);
                return rowone;
            }
            case 2: {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.divider, parent, false);
                programmingviewholder1 rowtwo = new programmingviewholder1(view2);
                return rowtwo;
            }

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 4 || position == 6) {
            return 2;

        } else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof programmingviewholder) {
            ((programmingviewholder) holder).textView.setText(str[position]);
//            ((programmingviewholder) holder).imageView1.setImageResource(img[position + 1]);
        }

    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class programmingviewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public programmingviewholder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.drawertext);


//            imageView1 = (ImageView) itemView.findViewById(R.id.t2);
//            imageView = (ImageView) itemView.findViewById(R.id.t1);

//            c = itemView.getContext();
//
//            imageView1 = (ImageView) itemView.findViewById(R.id.t2);
//            imageView = (ImageView) itemView.findViewById(R.id.t1);
            //           imageView.setImageResource(img[getAdapterPosition()]);
            //          imageView1.setImageResource(img[getAdapterPosition()] + 1);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    switch (getAdapterPosition()) {
//                        case 0: {
//                        }
//                        case 1: {
//                        }
//                        case 2: {
//                        }
//                        case 3: {
//                        }
//                    }
//                }
//
//            });
//            imageView1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    switch (getAdapterPosition()) {
//                        case 0: {
//                            Intent intent = new Intent(c, Firstclass.class);
//                            c.startActivity(intent);
//                        }
//                        case 1: {
//                        }
//                        case 2: {
//                        }
//                        case 3: {
//                        }
//
//                    }
//                }
//            });
//
//
        }
    }


    public class programmingviewholder1 extends RecyclerView.ViewHolder {
        // TextView textView;

        //ImageView imageView, imageView1;

        public programmingviewholder1(View itemView) {
            super(itemView);
            //  textView = (TextView) itemView.findViewById(R.id.as);

            //  textView.setOnClickListener(new View.OnClickListener() {
            //      @Override
            //      public void onClick(View view) {

            //    }
            //  });
        }
    }



}