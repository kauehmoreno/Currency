package br.com.rkj.currency.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import br.com.rkj.currency.Constante;
import br.com.rkj.currency.R;

/**
 * Created by kauerodrigues on 6/6/16.
 */
public class CurrencyAdapter extends BaseAdapter {
    private Context mContext;

    public CurrencyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return Constante.CURRENCY_SIZE;
    }

    @Override
    public Object getItem(int position) {
        return Constante.CURRENCY_CODE[position];
    }

    /*
        Not using, reaming returning value zero
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.currency_item, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.currency_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(Constante.CURRENCY_NAMES[position] + "("
                + Constante.CURRENCY_CODE[position] + ")");

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }
}