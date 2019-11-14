package com.example.aditya.result24;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private final Set<Pair<Integer, Integer>> mCheckedItems = new HashSet<Pair<Integer,Integer>>();

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    public String filterStringMaker() {
        String[] filters = new String[expandableListTitle.size()];
        for (int i = 0; i < expandableListTitle.size(); i++) filters[i] = "";
        for (Pair<Integer,Integer> tag : mCheckedItems) {
            String match = (String) getChild(tag.first,tag.second);
            if(filters[tag.first] == null || filters[tag.first].length() != 0) filters[tag.first] += "," + match.split("<-->")[1];
            else filters[tag.first] = match.split("<-->")[1];
        }

        String filterParams = "";
        for (int i = 0; i < filters.length; i++) {
            if (i != filters.length - 1) filterParams += expandableListTitle.get(i) + "=" + filters[i] + "&";
            else filterParams += expandableListTitle.get(i) + "=" + filters[i];
        }

        return filterParams;
    }


    public Set<Pair<Integer, Integer>> getCheckedItems() {
        return mCheckedItems;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText.split("<-->")[0]);

        TextView matchTextView = (TextView) convertView.findViewById(R.id.match);
        matchTextView.setText(expandedListText.split("<-->")[1]);

        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkbox);
        final Pair<Integer, Integer> tag = new Pair<Integer, Integer>(listPosition, expandedListPosition);
        cb.setTag(tag);
        cb.setChecked(mCheckedItems.contains(tag));
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CheckBox cb = (CheckBox) v;
                final Pair<Integer, Integer> tag = (Pair<Integer, Integer>) v.getTag();
                if (cb.isChecked()) {
                    mCheckedItems.add(tag);
                } else {
                    mCheckedItems.remove(tag);
                }

                JobFragment.param= filterStringMaker();

            }
        });

        return convertView;

    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
