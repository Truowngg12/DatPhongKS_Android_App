package com.example.datphongks.simplebooking;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datphongks.R;

import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị danh sách Room lên ListView.
 * Hiển thị real-time trạng thái "PHÒNG TRỐNG" (màu xanh) / "ĐÃ ĐẶT" (màu đỏ)
 * và hình ảnh động nạp bằng Glide.
 */
public class RoomAdapter extends BaseAdapter {

    private final Context context;
    private final List<Room> roomList;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return roomList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_room_simple, parent, false);

            holder = new ViewHolder();
            holder.imgRoom = convertView.findViewById(R.id.imgRoom);
            holder.txtRoomName = convertView.findViewById(R.id.txtRoomName);
            holder.txtRoomPrice = convertView.findViewById(R.id.txtRoomPrice);
            holder.txtRoomStatus = convertView.findViewById(R.id.txtRoomStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Room room = roomList.get(position);

        holder.txtRoomName.setText(room.getName());
        holder.txtRoomPrice.setText(
                String.format(Locale.getDefault(), "%,.0f đ / đêm", room.getPrice())
        );

        String status = room.getStatus();
        if ("Đã đặt".equalsIgnoreCase(status)) {
            holder.txtRoomStatus.setText("ĐÃ ĐẶT (CÓ KHÁCH)");
            holder.txtRoomStatus.setBackgroundColor(Color.parseColor("#DC2626")); // Red
        } else {
            holder.txtRoomStatus.setText("PHÒNG TRỐNG");
            holder.txtRoomStatus.setBackgroundColor(Color.parseColor("#16A34A")); // Green
        }

        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(room.getImageUrl())
                    .centerCrop()
                    .into(holder.imgRoom);
        } else if (room.getImageRes() != 0) {
            holder.imgRoom.setImageResource(room.getImageRes());
        } else {
            holder.imgRoom.setImageResource(R.drawable.ic_hotel_logo);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imgRoom;
        TextView txtRoomName;
        TextView txtRoomPrice;
        TextView txtRoomStatus;
    }
}
