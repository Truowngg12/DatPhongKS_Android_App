package com.example.datphongks.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datphongks.R;
import com.example.datphongks.data.models.Category;
import com.example.datphongks.databinding.ItemCategoryBinding;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getName().equals(newItem.getName()) && oldItem.isSelected() == newItem.isSelected();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(
                ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category, OnCategoryClickListener listener) {
            binding.tvCategoryName.setText(category.getName());
            if (category.isSelected()) {
                binding.cardCategory.setCardBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.navy_deep));
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                binding.cardCategory.setStrokeWidth(0);
            } else {
                binding.cardCategory.setCardBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.slate_grey));
                binding.cardCategory.setStrokeWidth(1);
            }

            binding.getRoot().setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }
}
