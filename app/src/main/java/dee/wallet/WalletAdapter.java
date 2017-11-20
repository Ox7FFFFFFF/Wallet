package dee.wallet;

import android.graphics.Color;
import android.hardware.usb.UsbAccessory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dee on 2017/11/8.
 *
 */

public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecordDetail> mDataset = new ArrayList<>();
    public enum ITEM_TYPE {
        ITEM_TYPE_DATE,
        ITEM_TYPE_RECORD,
        ITEM_TYPE_PIECHART,
        ITEM_TYPE_HEAD
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
        public ConstraintLayout constraintLayout;
        public RecordViewHolder(View v) {
            super(v);
            textName = (TextView) v.findViewById(R.id.item_name);
            textDollar = (TextView) v.findViewById(R.id.item_dollar);
            imageView = (ImageView) v.findViewById(R.id.item_icon);
            constraintLayout = (ConstraintLayout) v.findViewById(R.id.layout_item);
        }
    }
    public static class PieChartViewHolder extends RecyclerView.ViewHolder{
        public PieChart pieChart;
        public PieChartViewHolder(View v){
            super(v);
            pieChart = (PieChart) v.findViewById(R.id.pie_chart);
        }
    }
    public static class HeadViewHolder extends RecyclerView.ViewHolder{
        public TextView textExpense;
        public TextView textIncome;
        public TextView textBalance;
        public HeadViewHolder(View v){
            super(v);
            textIncome = (TextView) v.findViewById(R.id.textIncome);
            textExpense = (TextView) v.findViewById(R.id.textExpense);
            textBalance = (TextView) v.findViewById(R.id.textBalance);
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
        else if(viewType == ITEM_TYPE.ITEM_TYPE_RECORD.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item, parent, false);
            RecordViewHolder vh = new RecordViewHolder(v);
            return vh;
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_HEAD.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_head, parent, false);
            HeadViewHolder vh = new HeadViewHolder(v);
            return vh;
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_piechart, parent, false);
            PieChartViewHolder vh = new PieChartViewHolder(v);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecordDetail recordDetail = mDataset.get(position);
        if(holder instanceof DateViewHolder){
            ((DateViewHolder)holder).textdate.setText(setDateRegularFormat(recordDetail.getName()));
        }
        else if(holder instanceof RecordViewHolder){
            ((RecordViewHolder)holder).textName.setText(recordDetail.getName());
            int type = recordDetail.getType();
            if(type==0){
                ((RecordViewHolder)holder).textDollar.setTextColor(Color.parseColor("#F8838B"));
                ((RecordViewHolder)holder).textDollar.setText(String.valueOf(-recordDetail.getCost()));
            }
            else{
                ((RecordViewHolder)holder).textDollar.setTextColor(Color.parseColor("#4CAF9B"));
                ((RecordViewHolder)holder).textDollar.setText(String.valueOf(recordDetail.getCost()));
            }
            ((RecordViewHolder)holder).constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        else if(holder instanceof PieChartViewHolder){
            ((PieChartViewHolder)holder).pieChart.setUsePercentValues(true);
            ((PieChartViewHolder)holder).pieChart.getDescription().setEnabled(false);
            ((PieChartViewHolder)holder).pieChart.setExtraOffsets(5, 10, 5, 5);
            ((PieChartViewHolder)holder).pieChart.setCenterText("expense");
            ((PieChartViewHolder)holder).pieChart.setCenterTextSize(22f);
            ((PieChartViewHolder)holder).pieChart.setDrawCenterText(true);
            ((PieChartViewHolder)holder).pieChart.setRotationEnabled(false);
            ((PieChartViewHolder)holder).pieChart.setRotationAngle(0f);
            Legend legend = ((PieChartViewHolder)holder).pieChart.getLegend();
            legend.setEnabled(false);

            Map<String,Integer> pieValues = new HashMap<>();
            for(int j=0; j<mDataset.size();j++){
                if(mDataset.get(j).getName().equals("pie")){
                    continue;
                }
                pieValues.put(mDataset.get(j).getName(),mDataset.get(j).getCost());
            }
            setPieChartData(((PieChartViewHolder)holder).pieChart, pieValues);
            ((PieChartViewHolder)holder).pieChart.animateX(1500, Easing.EasingOption.EaseInOutQuad);
        }
        else if(holder instanceof HeadViewHolder){
            int income = recordDetail.getType();
            int expense = recordDetail.getCost();
            int balance = income-expense;
            ((HeadViewHolder)holder).textIncome.setText(String.valueOf(income));
            ((HeadViewHolder)holder).textExpense.setText(String.valueOf(expense));
            ((HeadViewHolder)holder).textBalance.setText(String.valueOf(balance));
            if(balance>0){
                ((HeadViewHolder)holder).textBalance.setTextColor(Color.parseColor("#4CAF9B"));
            }
            else {
                ((HeadViewHolder)holder).textBalance.setTextColor(Color.parseColor("#F8838B"));
            }
        }

    }

    private String setDateRegularFormat(String date){
        String[] tokens = date.split("-");
        int year = Integer.valueOf(tokens[0]);
        int monthOfYear = Integer.valueOf(tokens[1]);
        int dayOfMonth = Integer.valueOf(tokens[2]);
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        String str = "";
        if(mYear == year && mMonth==monthOfYear-1 && mDay== dayOfMonth){
            str += "Today , ";
        }
        Locale locale = new Locale("en", "US");
        String monthString = new DateFormatSymbols(locale).getMonths()[monthOfYear-1];
        str += dayOfMonth+getDayNumberSuffix(dayOfMonth)+" "+monthString;
        return str;
    }
    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static final int[] PIE_COLORS = {
            Color.rgb(181, 194, 202), Color.rgb(129, 216, 200), Color.rgb(241, 214, 145),
            Color.rgb(108, 176, 223), Color.rgb(195, 221, 155), Color.rgb(251, 215, 191),
            Color.rgb(237, 189, 189), Color.rgb(172, 217, 243)
    };
    private void setPieChartData(PieChart pieChart, Map<String,Integer> pieValues){
        ArrayList<PieEntry> entries = new ArrayList<>();
        Set set = pieValues.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            entries.add(new PieEntry(Float.valueOf(entry.getValue().toString()), entry.getKey().toString()));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(PIE_COLORS);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.DKGRAY);
        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*date = 0
        record = 1
        piechart = 2*/
        int layout = mDataset.get(position).getLayout();
        if(layout==0){
            return ITEM_TYPE.ITEM_TYPE_DATE.ordinal();
        }
        else if(layout==1){
            return ITEM_TYPE.ITEM_TYPE_RECORD.ordinal();
        }
        else if(layout==2){
            return ITEM_TYPE.ITEM_TYPE_PIECHART.ordinal();
        }
        else{
            return ITEM_TYPE.ITEM_TYPE_HEAD.ordinal();
        }

    }

}
