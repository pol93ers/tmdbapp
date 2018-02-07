package querol.pol.tmdbapp.base.adapter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import querol.pol.tmdbapp.util.Component;
import querol.pol.tmdbapp.util.LogUtils;

/**
 * <p>Base class for all RecyclerView Adapters. Extends support.v7.widget.RecyclerView.Adapter</p>
 * <p>Supports all sort of ArrayList operations and click events</p>
 * <p>Created by Pol Querol on 05/02/2018.</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AdapterRecyclerBase<I, VH extends AdapterRecyclerBase.BindableViewHolder<I>>
        extends RecyclerView.Adapter<VH>
        implements Component
{
    private final ArrayList<OnItemClickListener<I>> listeners;
    public interface OnItemClickListener<I> {
        void onItemClick(I item, int position, View rowView, int viewType);
        void onItemLongClick(I item, int position, View rowView, int viewType);
    }

    public static class BindableViewHolder<I> extends RecyclerView.ViewHolder {
        public View itemView;
        public I item;

        public BindableViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        @Override
        public String toString() {
            return "BindableViewHolder{" +
                    "itemView=" + String.valueOf(itemView) +
                    ", item=" + String.valueOf(item) +
                    '}';
        }
    }

    protected final String TAG = getComponent().id();
    protected final ArrayList<I> items;

    public AdapterRecyclerBase() {
        items = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    @Override
    public final @NonNull
    VH onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return onCreateViewHolder(context, inflater, parent, viewType);
    }

    protected abstract @NonNull VH onCreateViewHolder(Context context, LayoutInflater inflater, ViewGroup parent, int viewType);

    @CallSuper
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.item = items.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listeners.isEmpty()) {
                    I item = holder.item;
                    int position = holder.getAdapterPosition();
                    int viewType = holder.getItemViewType();
                    View itemView = holder.itemView;
                    LogUtils.d(TAG, "Clicked " + String.valueOf(holder.item)
                            + " at position " + String.valueOf(position)
                            + " in view " + String.valueOf(itemView)
                            + " of type " + String.valueOf(viewType)
                    );
                    for (OnItemClickListener<I> listener : listeners) {
                        listener.onItemClick(item, position, itemView, viewType);
                    }
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!listeners.isEmpty()) {
                    I item = holder.item;
                    int position = holder.getAdapterPosition();
                    int viewType = holder.getItemViewType();
                    View itemView = holder.itemView;
                    LogUtils.d(TAG, "Long Clicked " + String.valueOf(holder.item)
                            + " at position " + String.valueOf(position)
                            + " in view " + String.valueOf(itemView)
                            + " of type " + String.valueOf(viewType)
                    );
                    for (OnItemClickListener<I> listener : listeners) {
                        listener.onItemLongClick(item, position, itemView, viewType);
                    }
                }
                return !listeners.isEmpty();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public @NonNull I getItem(int position) {
        return items.get(position);
    }

    public @NonNull ArrayList<I> getItems() {
        return items;
    }

    public void addItem(@NonNull I item) {
        addItem(item, items.size());
    }

    public void addItem(@NonNull I item, int position) {
        this.items.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(@NonNull List<I> items) {
        addItems(items, this.items.size());
    }

    public void addItems(@NonNull List<I> items, int position) {
        if (!items.isEmpty()) {
            this.items.addAll(items);
            notifyItemRangeInserted(position, items.size());
        } else {
            LogUtils.e(TAG, "Empty List!");
        }
    }

    public void removeItem(@NonNull I item) {
        removeItem(items.indexOf(item));
    }

    public void removeItem(int position) {
        try {
            items.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            LogUtils.e(TAG, "Item not found!", e);
        }
    }

    public void setItems(@NonNull List<I> items) {
        if (!items.isEmpty()) {
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        } else {
            LogUtils.e(TAG, "Empty List!");
        }
    }

    public void clearItems() {
        this.items.clear();
        notifyDataSetChanged();
    }


    public void addOnItemClickListener(OnItemClickListener<I> listener) {
        this.listeners.add(listener);
    }

    public boolean removeOnItemClickListener(OnItemClickListener<I> listener) {
        return this.listeners.remove(listener);
    }
}

