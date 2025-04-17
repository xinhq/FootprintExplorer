package com.example.footprintexplorer.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.data.entity.Challenge;
import com.google.android.material.card.MaterialCardView;

/**
 * 挑战任务适配器
 * 用于显示挑战任务列表
 */
public class ChallengeAdapter extends ListAdapter<Challenge, ChallengeAdapter.ChallengeViewHolder> {

    private final OnChallengeClickListener listener;

    public ChallengeAdapter(OnChallengeClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Challenge> DIFF_CALLBACK = new DiffUtil.ItemCallback<Challenge>() {
        @Override
        public boolean areItemsTheSame(@NonNull Challenge oldItem, @NonNull Challenge newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Challenge oldItem, @NonNull Challenge newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getProgress() == newItem.getProgress() &&
                    oldItem.isCompleted() == newItem.isCompleted();
        }
    };

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = getItem(position);
        holder.bind(challenge, listener);
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView progressTextView;
        private final ProgressBar progressBar;
        private final TextView rewardTextView;
        private final TextView difficultyTextView;
        private final TextView statusTextView;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_challenge);
            titleTextView = itemView.findViewById(R.id.text_challenge_title);
            descriptionTextView = itemView.findViewById(R.id.text_challenge_description);
            progressTextView = itemView.findViewById(R.id.text_challenge_progress);
            progressBar = itemView.findViewById(R.id.progress_challenge);
            rewardTextView = itemView.findViewById(R.id.text_challenge_reward);
            difficultyTextView = itemView.findViewById(R.id.text_challenge_difficulty);
            statusTextView = itemView.findViewById(R.id.text_challenge_status);
        }

        public void bind(Challenge challenge, OnChallengeClickListener listener) {
            titleTextView.setText(challenge.getTitle());
            descriptionTextView.setText(challenge.getDescription());
            
            // 设置进度
            int progressPercentage = challenge.getProgressPercentage();
            progressTextView.setText(String.format("%d/%d (%d%%)", 
                    challenge.getProgress(), challenge.getTarget(), progressPercentage));
            progressBar.setProgress(progressPercentage);
            
            // 设置奖励
            rewardTextView.setText(String.format("+%d XP", challenge.getXpReward()));
            
            // 设置难度
            StringBuilder difficultyBuilder = new StringBuilder();
            for (int i = 0; i < challenge.getDifficulty(); i++) {
                difficultyBuilder.append("★");
            }
            difficultyTextView.setText(difficultyBuilder.toString());
            
            // 设置状态
            if (challenge.isCompleted()) {
                statusTextView.setText("已完成");
                statusTextView.setTextColor(itemView.getContext().getResources().getColor(R.color.challenge_completed, null));
            } else {
                statusTextView.setText("进行中");
                statusTextView.setTextColor(itemView.getContext().getResources().getColor(R.color.challenge_in_progress, null));
            }
            
            // 设置点击事件
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChallengeClick(challenge);
                }
            });
        }
    }

    public interface OnChallengeClickListener {
        void onChallengeClick(Challenge challenge);
    }
}
