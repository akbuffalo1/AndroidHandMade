package com.uae.tra_smart_services.entities.treview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.rest.model.response.EquipmentModel;

/**
 * Created by ak-buffalo on 13.11.15.
 */
public class TreeSelectableItemHolder extends TreeNode.BaseNodeViewHolder<EquipmentModel.EquipmentTreeSelectableItem> implements TreeNode.TreeNodeClickListener{
    private TextView tvValue;
    private CheckBox nodeSelector;
    private static final int LEVEL_PADDING = 30;

    public TreeSelectableItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, EquipmentModel.EquipmentTreeSelectableItem value) {
        final View view = LayoutInflater.from(context).inflate(R.layout.layout_selectable_item, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNode.setSelected(isChecked);
            }
        });
        node.setSelected(value.checked);
        nodeSelector.setChecked(value.checked);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(LEVEL_PADDING * (mNode.getLevel() - 1), 0, 0, 0);
        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }

    @Override
    public void toggle(boolean active) {
        super.toggle(active);
        mNode.setSelected(active);
        nodeSelector.setChecked(active);
    }

    @Override
    public void onClick(TreeNode node, Object value) {
    }
}