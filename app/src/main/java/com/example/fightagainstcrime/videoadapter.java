package com.example.fightagainstcrime;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class videoadapter  extends  FirebaseRecyclerAdapter<videomodel,videoadapter.myviewholder> {
    Context context;
    private ImageView sound_dis;
    DatabaseReference likereference;
    Boolean testclick=false;

    public videoadapter(@NonNull FirebaseRecyclerOptions<videomodel> options) {
        super(options);


    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull videomodel model) {
        //Glide.with(context).load(R.drawable.disc1).into( holder.sound_dis);
        holder.setdata(model);
        likereference=FirebaseDatabase.getInstance().getReference("likes");
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String userid=firebaseUser.getUid();
        final String postkey=getRef(position).getKey();
        holder.getlikebuttonstatus(postkey,userid);

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testclick=true;

                likereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(testclick==true)
                        {
                            if(snapshot.child(postkey).hasChild(userid))
                            {
                                likereference.child(postkey).child(userid).removeValue();
                                testclick=false;
                            }
                            else
                            {
                                likereference.child(postkey).child(userid).setValue(true);
                                testclick=false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
               // Toast.makeText(, "error", Toast.LENGTH_SHORT).show();
//                Intent intent=new Intent(getApplicationContext(),commentpanel.class);
//                intent.putExtra("postkey",postkey);
//                startActivity(intent);
            }
        });


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row,parent,false);
        return new myviewholder(view);

    }


    class myviewholder extends RecyclerView.ViewHolder
    {

        VideoView videoView;
        TextView title,desc;
        ProgressBar pbar;
        //14/2-22.55//////////////////////////////////////////////
        ImageView like_btn;
        ImageView comment_btn;
        TextView like_text;
        DatabaseReference likereference;
        //////////////////////////////////////////////////////
        private ImageView sound_dis;

        public myviewholder(@NonNull View itemView)
        {

            super(itemView);

            videoView=(VideoView)itemView.findViewById(R.id.videoView);
            title=(TextView)itemView.findViewById(R.id.textVideoTitle);
            desc=(TextView)itemView.findViewById(R.id.textVideoDescription);
            pbar=(ProgressBar)itemView.findViewById(R.id.videoProgressBar);
            sound_dis = (ImageView) itemView.findViewById(R.id.imageView3);
            ////////////////////////////////////
            like_btn=(ImageView)itemView.findViewById(R.id.like_btn);
            like_text=(TextView)itemView.findViewById(R.id.like_text);
            comment_btn=(ImageView)itemView.findViewById(R.id.comment_btn);
            //////////////////////////////////////
        }

        //////////////////////////////////
        public void getlikebuttonstatus(final String postkey, final String userid)
        {
            likereference= FirebaseDatabase.getInstance().getReference("likes");
            likereference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(postkey).hasChild(userid))
                    {
                        int likecount=(int)snapshot.child(postkey).getChildrenCount();
                        like_text.setText(likecount+" likes");
                        like_btn.setImageResource(R.drawable.ic_baseline_favorite_24);
                    }
                    else
                    {
                        int likecount=(int)snapshot.child(postkey).getChildrenCount();
                        like_text.setText(likecount+" likes");
                        like_btn.setImageResource(R.drawable.like);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //////////////////////////////////

        void setdata(videomodel obj)
        {
            videoView.setVideoPath(obj.getUrl());
            title.setText(obj.getTitle());
            desc.setText(obj.getDesc());



            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    pbar.setVisibility(View.GONE);
                    mediaPlayer.start();
                    

                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
    }


}
