package com.example.cashflowscheduler.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashflowscheduler.R;
import com.example.cashflowscheduler.logic.Timeline;

import java.util.ArrayList;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    public interface OnDetailClickListener {
        void onDetail(Timeline.DayEntry entry);
    }

    private List<Timeline.DayEntry> items = new ArrayList<>();
    private OnDetailClickListener listener;

    public void setItems(List<Timeline.DayEntry> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnDetailClickListener(OnDetailClickListener l) { this.listener = l; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Timeline.DayEntry entry = items.get(position);

        h.tvDate.setText(entry.date);

        // 구분 텍스트
        String typeLabel;
        if (entry.isIncome)     typeLabel = "[수입] ";
        else if (entry.isFixed) typeLabel = "[고정지출] ";
        else                    typeLabel = "[지출] ";

        h.tvCategory.setText(typeLabel + entry.category);

        if (entry.isIncome) {
            h.tvAmount.setText("+" + String.format("%,d", entry.amount));
            h.tvAmount.setTextColor(Color.parseColor("#43A047"));
        } else {
            h.tvAmount.setText("-" + String.format("%,d", entry.amount));
            h.tvAmount.setTextColor(Color.parseColor("#E53935"));
        }

        h.tvBalance.setText(String.format("%,d원", entry.balanceAfter));

        if (entry.isDanger) {
            h.itemView.setBackgroundColor(Color.parseColor("#FFEBEE"));
            h.tvBalance.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            h.itemView.setBackgroundColor(Color.WHITE);
            h.tvBalance.setTextColor(Color.parseColor("#1A73E8"));
        }

        h.btnDetail.setOnClickListener(v -> {
            if (listener != null) listener.onDetail(entry);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvCategory, tvAmount, tvBalance, btnDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate     = itemView.findViewById(R.id.tv_item_date);
            tvCategory = itemView.findViewById(R.id.tv_item_category);
            tvAmount   = itemView.findViewById(R.id.tv_item_amount);
            tvBalance  = itemView.findViewById(R.id.tv_item_balance);
            btnDetail  = itemView.findViewById(R.id.btn_item_detail);
        }
    }
}