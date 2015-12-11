package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.enon.tra.uae.R;
import com.github.johnkil.print.PrintView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import biz.enon.tra.uae.rest.model.response.WorkQueueDataModel;

/**
 * Created by ak-buffalo on 19.11.15.
 */
public class WorkQueueExpandableAdapter extends ExpandableRecyclerAdapter<WorkQueueExpandableAdapter.WorkQueueParentViewHolder, WorkQueueExpandableAdapter.WorkQueueChildViewHolder>
                                                    implements Spinner.OnItemSelectedListener{
    private final String mLocale;
    public WorkQueueDataModel datamodel;
    private LayoutInflater mInflater;
    private static WorkQueueExpandableAdapter.NetModelToExpRecyclerViewModelAdapter adapter;

    public WorkQueueExpandableAdapter(Context context, WorkQueueDataModel _datamodel) {
        super(context, (adapter = new WorkQueueExpandableAdapter.NetModelToExpRecyclerViewModelAdapter(_datamodel)).initData());
        mInflater = LayoutInflater.from(context);
        datamodel = _datamodel;
        mLocale = Locale.getDefault().getLanguage().toUpperCase();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View _view, int position, long id) {
        adapter.sortBy(datamodel.dataSource.get(position).get("value"));
        notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public WorkQueueParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.layout_tree_header, viewGroup, false);
        return new WorkQueueParentViewHolder(view);
    }

    @Override
    public WorkQueueChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        LinearLayout view = new LinearLayout(mContext);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOrientation(LinearLayout.VERTICAL);
        return new WorkQueueChildViewHolder(view, datamodel, mLocale);
    }

    @Override
    public void onBindParentViewHolder(WorkQueueParentViewHolder crimeParentViewHolder, int i, Object parentListItem) {
        crimeParentViewHolder.mWorkQueueHeaderTitleTextView.setText(((Header) parentListItem).getTile());
    }

    @Override
    public void onBindChildViewHolder(WorkQueueChildViewHolder crimeChildViewHolder, int i, Object childListItem) {
        for(Map.Entry<String, TextView> valueView : crimeChildViewHolder.memberViews.entrySet()){
            valueView.getValue().setText(((Map<String, String>)childListItem).get(valueView.getKey()));
        }
    }

    public class WorkQueueParentViewHolder extends ParentViewHolder {
        public TextView mWorkQueueHeaderTitleTextView;
        public PrintView arrowView;

        public WorkQueueParentViewHolder(View itemView) {
            super(itemView);
            mWorkQueueHeaderTitleTextView = (TextView) itemView.findViewById(R.id.node_value);
            arrowView = (PrintView) itemView.findViewById(R.id.arrow_icon);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            arrowView.setIconText(mContext.getResources().getString(isExpanded() ? R.string.ic_keyboard_arrow_up : R.string.ic_keyboard_arrow_down));
        }
    }

    public class WorkQueueChildViewHolder extends ChildViewHolder {
        Map<String, TextView> memberViews = new HashMap<>();
        LinearLayout underLine;

        public WorkQueueChildViewHolder(View itemView, WorkQueueDataModel _dataModel, String _locale) {
            super(itemView);
            LinearLayout container = (LinearLayout) itemView;
            for (Map<String,String> localized : _dataModel.dataSource){
                LinearLayout memberView = (LinearLayout) mInflater.inflate(R.layout.layout_work_queue_list_item_child, null, false);
                TextView title = (TextView) memberView.findViewById(R.id.tvTitle_LWQLICH);
                title.setText(localized.get(_locale));
                memberViews.put(localized.get("value"), (TextView) memberView.findViewById(R.id.tvValue_LWQLICH));
                container.addView(memberView);
            }

            underLine = new LinearLayout(mContext);
            underLine.setBackgroundColor(Color.DKGRAY);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
            layoutParams.setMargins(30,0,30,0);
            underLine.setLayoutParams(layoutParams);
            container.addView(underLine);
        }
    }

    public static class NetModelToExpRecyclerViewModelAdapter implements Comparator<String> {
        public List<ParentObject> mParentObjects;
        public WorkQueueDataModel datamodel;
        Map<String, List<Map<String, String>>> unique = new HashMap<>();

        public NetModelToExpRecyclerViewModelAdapter(WorkQueueDataModel _datamodel){
            datamodel = _datamodel;
        }

        public void prepareData(){
            header: for(final Map<String, String> content : datamodel.dataContent){
                Set<String> keys = unique.keySet();
                String valueToFind = content.get(datamodel.additional.get("sectorField"));
                if(unique.isEmpty()){
                    unique.put(valueToFind, new ArrayList<Map<String, String>>(){{add(content);}});
                    continue;
                }
                for (String key : keys){
                    if(compare(valueToFind, key) > 0){
                        unique.get(key).add(content);
                        continue header;
                    }
                }
                unique.put(valueToFind, new ArrayList<Map<String, String>>(){{add(content);}});
            }
        }

        @Override
        public int compare(String lhs, String rhs) {
            return (lhs.hashCode() == rhs.hashCode()) ? 1 : -1;
        }

        public List<ParentObject> initData(){
            prepareData();
            createParentObjects();
            return mParentObjects;
        }

        public void createParentObjects(){
            mParentObjects = new ArrayList<ParentObject>(){
                {
                    for(final Map.Entry<String, List<Map<String, String>>> item : unique.entrySet()){
                        add(new Header() {
                            @Override
                            public List<Object> getChildObjectList() {
                                return new ArrayList<Object>(item.getValue());
                            }
                            @Override
                            public void setChildObjectList(List<Object> list) { /*No need to implement*/ }
                            @Override
                            public String getTile(){
                                return item.getKey();
                            }
                        });
                    }
                }
            };
        }

        public void sortBy(String _key){
            for (final List<Map<String,String>> parent : adapter.unique.values()){
                Collections.sort(parent, new MapComparator(_key));
            }
            createParentObjects();
        }

        public class MapComparator implements Comparator<Object> {
            private final String key;

            public MapComparator(String key) {
                this.key = key;
            }

            public int compare(Object first, Object second) {
                String firstValue = ((Map<String, String>) first).get(key);
                String secondValue = ((Map<String, String>) second).get(key);
                return firstValue.compareTo(secondValue);
            }
        }
    }

    public interface Header extends ParentObject {
        String getTile();
    }
}