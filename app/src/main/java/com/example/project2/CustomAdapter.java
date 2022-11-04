package com.example.project2;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifBitmapProvider;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<BoardInfo> arrayList;
    private Context context;

    // viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성.
    public CustomAdapter(ArrayList<BoardInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_name.setText(arrayList.get(position).getName());
        holder.tv_date.setText(arrayList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_date;
        TextView tv_name;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_date = itemView.findViewById(R.id.tv_date);
            this.tv_name = itemView.findViewById(R.id.tv_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition();      //현재 선택한 리스트 아이템 위치
                    BoardInfo itemList = arrayList.get(curPos);
                    Intent intent =  new Intent(context, DetailActivity.class);   //화면 넘겨주기
                    intent.putExtra("idToken", itemList.getIdToken());
                    intent.putExtra("name", itemList.getName());
                    intent.putExtra("content", itemList.getContent());
                    intent.putExtra("title", itemList.getTitle());
                    intent.putExtra("date", itemList.getDate());
                    intent.putExtra("itemList", String.valueOf(itemList));
                    Log.d("위치 확인", String.valueOf(itemList));
                    Log.d("위치 확인", String.valueOf(curPos));
                    context.startActivity(intent);
                }
            });
        }

    }

}
