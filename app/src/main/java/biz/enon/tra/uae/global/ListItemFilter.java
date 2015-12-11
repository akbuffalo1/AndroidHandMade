package biz.enon.tra.uae.global;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ak-buffalo on 19.08.15.
 */
public abstract class ListItemFilter<T,K> extends Filter {
    protected T mAdapter;

    private final List<K> originalList;

    private final List<K> filteredList;

    protected ListItemFilter(/*T adapter, */List<K> originalList) {
        super();
        this.originalList = new LinkedList<>(originalList);
        this.filteredList = new ArrayList<>();
    }

    public void initFromAdapter(T _adapter){
        this.mAdapter = _adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();

            for (final K listItem : originalList) {
                if (listItem.toString().toLowerCase().trim().contains(filterPattern)) {
                    filteredList.add(listItem);
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected abstract void publishResults(CharSequence constraint, FilterResults results);
}