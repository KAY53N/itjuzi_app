package common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class SpecialAdapter extends SimpleAdapter
{
    public SpecialAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
    {
        super(context, data, resource, from, to);
    }


    @Override

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(0xFFFFFFFF);
        return view;
    }
}