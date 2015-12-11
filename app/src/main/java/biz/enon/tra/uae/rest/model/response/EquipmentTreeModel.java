package biz.enon.tra.uae.rest.model.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static biz.enon.tra.uae.adapter.TreeViewBaseAdapter.TreeEntity;

/**
 * Created by ak-buffalo on 13.11.15.
 */
public class EquipmentTreeModel {
    public String validateAs;
    public boolean required;
    public String inputType;
    public String name;
    public int order;
    public String _id;
    public ArrayList<EquipmentTreeItem> dataSource;

    public class EquipmentTreeItem implements TreeEntity {

        public String value;
        public String EN;
        public String AR;
        public boolean checked;
        public ArrayList<EquipmentTreeItem> items = new ArrayList<>();

        @Override
        public boolean haveChild() {
            return items.size() > 0;
        }

        @Override
        public void add(TreeEntity _entity) {
            items.add((EquipmentTreeItem) _entity);
        }

        @Override
        public List<TreeEntity> getChildren() { return (ArrayList) items; }
    }

    public Map<String, String> displayName;
    public Map<String, String> placeHolder;
}