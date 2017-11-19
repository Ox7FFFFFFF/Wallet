package dee.wallet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dee on 2017/11/8.
 *
 */

public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecordDetail> mDataset = new ArrayList<>();
    public enum ITEM_TYPE {
        ITEM_TYPE_DATE,
        ITEM_TYPE_RECORD
    }
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        public TextView textdate;
        public DateViewHolder(View v) {
            super(v);
            textdate = (TextView) v.findViewById(R.id.recycleview_date);
        }
    }
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public ImageView imageView;
        public TextView textDollar;
        public RecordViewHolder(View v) {
            super(v);
            textName = (TextView) v.findViewById(R.id.item_name);
            textDollar = (TextView) v.findViewById(R.id.item_dollar);
            imageView = (ImageView) v.findViewById(R.id.item_icon);
        }
    }

    public WalletAdapter(ArrayList<RecordDetail> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE.ITEM_TYPE_DATE.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_date, parent, false);
            DateViewHolder vh = new DateViewHolder(v);
            return vh;
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item, parent, false);
            RecordViewHolder vh = new RecordViewHolder(v);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecordDetail recordDetail = mDataset.get(position);
        if(holder instanceof DateViewHolder){
            ((DateViewHolder)holder).textdate.setText(recordDetail.getName());
        }
        else if(holder instanceof RecordViewHolder){
            ((RecordViewHolder)holder).textName.setText(recordDetail.getName());
            ((RecordViewHolder)holder).textDollar.setText(recordDetail.getCost());
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position).getId() < 0 ? ITEM_TYPE.ITEM_TYPE_DATE.ordinal() : ITEM_TYPE.ITEM_TYPE_RECORD.ordinal();
    }

}
