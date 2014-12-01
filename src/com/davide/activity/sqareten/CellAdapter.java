package com.davide.activity.sqareten;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by dcazzaniga on 03/07/14.
 */

public class CellAdapter  extends ArrayAdapter<Integer> {

    private Context context;
    private ArrayList<Integer> data = new ArrayList<Integer>(100) ;
    private int layoutResourceId;

    public CellAdapter(Context context, int layoutResourceId, ArrayList<Integer> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View cell = View.inflate(context, R.layout.gridcell, null);
        TextView tv = (TextView) cell.findViewById(R.id.cella);
        Integer item = data.get(position);
        tv.setId(item);
        return cell;

    }

}
