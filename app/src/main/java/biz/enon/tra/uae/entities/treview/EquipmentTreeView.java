package biz.enon.tra.uae.entities.treview;

import android.content.Context;

import biz.enon.tra.uae.adapter.TreeViewBaseAdapter;

/**
 * Created by ak-buffalo on 13.11.15.
 */
public class EquipmentTreeView extends AndroidTreeView {
    public TreeViewBaseAdapter<?> mTreeViewBaseAdapter;
    public EquipmentTreeView(Context context) {
        super(context, null);
    }

    public void setTreeViewAdapter(TreeViewBaseAdapter<?> _adapter){
        mTreeViewBaseAdapter = _adapter;
        mRoot = mTreeViewBaseAdapter.getRoot();
    }
}