package com.example.mad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mad.R;
import com.example.mad.models.AttendanceRecord;

import java.util.List;

// If you added Glide, uncomment imports and Glide usage
// import com.bumptech.glide.Glide;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {

    private final Context ctx;
    private final List<AttendanceRecord> items;

    public StudentsAdapter(Context ctx, List<AttendanceRecord> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_student_attendance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecord rec = items.get(position);
        holder.tvName.setText(rec.getName());
        holder.tvTime.setText(rec.getTime());
        holder.tvRoomHostel.setText(rec.getRoomHostel());

        // load profile image: either local drawable or URL
        if (rec.getProfileUrl() != null && !rec.getProfileUrl().isEmpty()) {
            // if you added Glide, use this:
            // Glide.with(ctx).load(rec.getProfileUrl()).placeholder(R.drawable.ic_account_circle).circleCrop().into(holder.imgProfile);

            // fallback: you can keep placeholder for now
            holder.imgProfile.setImageResource(R.drawable.ic_account_circle);
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_account_circle);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView tvName, tvTime, tvRoomHostel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRoomHostel = itemView.findViewById(R.id.tvRoomHostel);
        }
    }
}
