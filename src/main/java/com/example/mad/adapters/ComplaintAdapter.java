package com.example.mad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad.R;
import com.example.mad.models.Complaint;

import java.util.ArrayList;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Complaint c);
        void onItemLongClick(Complaint c);
    }

    private final List<Complaint> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public ComplaintAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Complaint> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Complaint c = items.get(pos);
        h.tvTitle.setText(c.title);
        h.tvCategory.setText(c.category);
        if (h.tvRoom != null) {
            h.tvRoom.setText(c.room != null ? "Room: " + c.room : "");
        }
        h.tvStatus.setText(c.status);
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(c); });
        h.itemView.setOnLongClickListener(v -> { if (listener != null) listener.onItemLongClick(c); return true; });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvStatus, tvRoom;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRoom = itemView.findViewById(R.id.tvRoom);
        }
    }
}
