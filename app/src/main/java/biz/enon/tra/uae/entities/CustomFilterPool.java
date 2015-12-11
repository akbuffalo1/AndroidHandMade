package biz.enon.tra.uae.entities;

import java.util.ArrayList;

/**
 * Created by ak-buffalo on 14.08.15.
 *
 * Class CustomFilterPool is verifying of compliance of the custom data model
 * More then one filter can be added to check model
 *
 * @implements Filter
 * */
public class CustomFilterPool<T> implements Filter<T> {
    private ArrayList<Filter<T>> filters = new ArrayList<>();

    public void addFilter(Filter<T> _filter){
        filters.add(_filter);
    }

    @Override
    public boolean check(T _data) {
        for (Filter<T> filter : filters){
            if(filter.check(_data)){
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
}


