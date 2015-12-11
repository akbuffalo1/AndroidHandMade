package biz.enon.tra.uae.entities.treview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.rest.model.response.EquipmentTreeModel;

/**
 * Created by ak-buffalo on 13.11.15.
 */
public class TreeHeaderHolder extends TreeNode.BaseNodeViewHolder<EquipmentTreeModel.EquipmentTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;
    private static final int LEVEL_PADDING = 30;

    public TreeHeaderHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, EquipmentTreeModel.EquipmentTreeItem value) {
        final View view = LayoutInflater.from(context).inflate(R.layout.layout_tree_header, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.value);

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);
        if (node.isLeaf()) {
            arrowView.setVisibility(View.INVISIBLE);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(LEVEL_PADDING * (mNode.getLevel() - 1), 0, 0, 0);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_up : R.string.ic_keyboard_arrow_down));
    }
}