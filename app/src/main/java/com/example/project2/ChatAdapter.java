package com.example.project2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 이 데이터들을 가지고 각 뷰 홀더에 들어갈 텍스트 뷰에 연결할 것
    private ArrayList<ChatItem> myDataList = null;

    // 생성자
    ChatAdapter(ArrayList<ChatItem> dataList)
    {
        myDataList = dataList;
    }

    // 어댑터 클래스 상속시 구현해야할 함수 3가지 : onCreateViewHolder, onBindViewHolder, getItemCount
    // 리사이클러뷰에 들어갈 뷰 홀더를 할당하는 함수,
    // 뷰 홀더는 실제 레이아웃 파일과 매핑되어야하며, extends의 Adater<>에서 <>안에들어가는 타입을 따른다.

    // ViewHolder를 새로 만들어야 할 때마다 이 메서드를 호출합니다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewType.CENTER_JOIN)
        {
            view = inflater.inflate(R.layout.center_join, parent, false);
            return new CenterViewHolder(view);
        }
        else if(viewType == ViewType.LEFT_CHAT)
        {
            view = inflater.inflate(R.layout.left_chat, parent, false);
            return new LeftViewHolder(view);
        }
        else
        {
            view = inflater.inflate(R.layout.right_chat, parent, false);
            return new RightViewHolder(view);
        }
    }

    // ViewHolder를 데이터와 연결할 때 이 메서드를 호출합니다.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if(viewHolder instanceof CenterViewHolder)
        {
            ((CenterViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
        }
        else if(viewHolder instanceof LeftViewHolder)
        {
            ((LeftViewHolder) viewHolder).name.setText(myDataList.get(position).getName());
            ((LeftViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((LeftViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }
        else
        {
            ((RightViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((RightViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }
    }

    // 데이터 세트 크기를 가져올 때 이 메서드를 호출합니다.
    @Override
    public int getItemCount()
    {
        return myDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return myDataList.get(position).getViewType();
    }

    // 중앙 채팅 : ~~님이 입장하셨습니다.
    public class CenterViewHolder extends RecyclerView.ViewHolder{
        TextView content;

        CenterViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
        }
    }

    // 왼쪽 채팅 : 상대방 채팅박스
    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView name;
        TextView time;

        LeftViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
        }
    }

    // 오른쪽 채팅 : 이용자 채팅박스
    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView time;

        RightViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }

}