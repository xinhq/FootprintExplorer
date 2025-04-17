package com.example.footprintexplorer.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.footprintexplorer.R;
import com.example.footprintexplorer.data.entity.Badge;

/**
 * 徽章适配器
 * 用于显示徽章列表
 */
public class BadgeAdapter extends ListAdapter<Badge, BadgeAdapter.BadgeViewHolder> {

    private final OnBadgeClickListener listener;

    public BadgeAdapter(OnBadgeClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Badge> DIFF_CALLBACK = new DiffUtil.ItemCallback<Badge>() {
        @Override
        public boolean areItemsTheSame(@NonNull Badge oldItem, @NonNull Badge newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Badge oldItem, @NonNull Badge newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getCategory().equals(newItem.getCategory()) &&
                   oldItem.isUnlocked() == newItem.isUnlocked();
        }
    };

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_badge, parent, false);
        return new BadgeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        Badge badge = getItem(position);
        holder.bind(badge, listener);
    }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textName;
        private final TextView textCategory;
        private final View lockOverlay;

        BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_badge);
            textName = itemView.findViewById(R.id.text_badge_name);
            textCategory = itemView.findViewById(R.id.text_badge_category);
            lockOverlay = itemView.findViewById(R.id.lock_overlay);
        }

        void bind(Badge badge, OnBadgeClickListener listener) {
            textName.setText(badge.getName());
            textCategory.setText(badge.getCategory());
            
            // 设置徽章图片
            if (badge.getImageUrl() != null && !badge.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(badge.getImageUrl())
                        .placeholder(R.drawable.badge_placeholder)
                        .error(R.drawable.badge_placeholder)
                        .into(imageView);
            } else {
                // 根据类别设置默认图片
                int resourceId;
                switch (badge.getCategory()) {
                    case "美食":
                        resourceId = R.drawable.badge_food;
                        break;
                    case "文物":
                        resourceId = R.drawable.badge_culture;
                        break;
                    case "动物":
                        resourceId = R.drawable.badge_animal;
                        break;
                    default:
                        resourceId = R.drawable.badge_placeholder;
                        break;
                }
                imageView.setImageResource(resourceId);
            }
            
            // 设置锁定状态
            lockOverlay.setVisibility(badge.isUnlocked() ? View.GONE : View.VISIBLE);
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null && badge.isUnlocked()) {
                    listener.onBadgeClick(badge);
                }
            });
        }
    }

    public interface OnBadgeClickListener {
        void onBadgeClick(Badge badge);
    }
}
