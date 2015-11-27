package com.uae.tra_smart_services.entities;

import android.content.Context;
import android.text.SpannableStringBuilder;

import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.global.SpannableWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak-buffalo on 09.09.15.
 */
public class SearchResult {

    private List<SearchResultItem> searchResultItems;

    public SearchResult(){
        searchResultItems = new ArrayList<>();
    }

    public void initFromServicesList(List<Service> _services, Context _context){
        for(Service service : _services){
            addItem(new SearchResultItem(service, service.getTitle(_context)));
        }
    }

    public List<SearchResultItem> getSearchResultItems(){
        return searchResultItems;
    }

    public class SearchResultItem extends SpannableWrapper {
        private Service service;

        public SearchResultItem(Service _service, String _text){
            super(_text);
            service = _service;
        }

        public Service getBindedService(){
            return service;
        }
    }

    public int getCount(){
        return searchResultItems.size();
    }

    public void clearAll(){
        searchResultItems.clear();
    }

    public void addItem(SearchResult.SearchResultItem results){
        searchResultItems.add(results);
    }
}
