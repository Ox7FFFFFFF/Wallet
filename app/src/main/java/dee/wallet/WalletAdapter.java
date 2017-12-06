package dee.wallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbAccessory;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
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
    private onTextChangeListener mTextListener;
    private onButtonClickListener mButtonListener;
    private onButtonClickResultListener mButtonClickResultListener;
    private Context context;
    private ArrayList<Integer> duration = new ArrayList<>();
    private final Calendar c = Calendar.getInstance();
    private Activity activity;
    private int mYear,mMonth,mDay;

    public enum ITEM_TYPE {
        ITEM_TYPE_DATE,
        ITEM_TYPE_RECORD,
        ITEM_TYPE_PIECHART,
        ITEM_TYPE_HEAD,
        ITEM_TYPE_CARD,
        ITEM_TYPE_EDIT_TEXT,
        ITEM_TYPE_TEXT,
        ITEM_TYPE_BUTTON,
        ITEM_TYPE_RADIO,
        ITEM_TYPE_SPINNER,
        ITEM_TYPE_CLOCK
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
    public static class CardViewHolder extends RecyclerView.ViewHolder{
        public RecyclerView recyclerView;
        public CardView cardView;
        public RecyclerView.LayoutManager layoutManager;
        public CardViewHolder(View v){
            super(v);
            recyclerView = (RecyclerView) v.findViewById(R.id.recycleview_item);
            cardView = (CardView) v.findViewById(R.id.recycleview_cardview);
            layoutManager = new LinearLayoutManager(v.getContext());
            recyclerView.setLayoutManager(layoutManager);
        }
    }
    public static class EditTextViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle;
        public EditText editValue;
        public ConstraintLayout constraintLayout;
        public EditTextViewHolder(View v){
            super(v);
            textTitle = (TextView) v.findViewById(R.id.edit_recycleview_title);
            editValue = (EditText) v.findViewById(R.id.edit_recycleview_value);
            constraintLayout = (ConstraintLayout) v.findViewById(R.id.edit_recycleview_layout);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle;
        public TextView textValue;
        public ConstraintLayout textConstraintLayout;
        public TextViewHolder(View v){
            super(v);
            textTitle = (TextView) v.findViewById(R.id.edit_recycleview_text_title);
            textValue = (TextView) v.findViewById(R.id.edit_recycleview_text_value);
            textConstraintLayout = (ConstraintLayout) v.findViewById(R.id.edit_recycleview_text_layout);
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder{
        public Button btnSubmit;
        public ConstraintLayout buttonConstraintLayout;
        public ButtonViewHolder(View v){
            super(v);
            btnSubmit = (Button) v.findViewById(R.id.edit_recycleview_button_submit);
            buttonConstraintLayout = (ConstraintLayout) v.findViewById(R.id.edit_recycleview_button_layout);
        }
    }

    public static class RadioViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle;
        public RadioGroup radioGroupType;
        public RadioButton radioButtonExpense;
        public RadioButton radioButtonIncome;

        public RadioViewHolder(View v){
            super(v);
            textTitle = (TextView) v.findViewById(R.id.edit_recycleview_radio_title);
            radioGroupType = (RadioGroup) v.findViewById(R.id.radioGroup_type);
            radioButtonIncome = (RadioButton) v.findViewById(R.id.radioButton_income);
            radioButtonExpense = (RadioButton) v.findViewById(R.id.radioButton_expense);
        }
    }

    public static class SpinnerViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle;
        public Spinner spinnerCategory;
        public ConstraintLayout constraintLayout;
        public SpinnerViewHolder(View v){
            super(v);
            textTitle = (TextView) v.findViewById(R.id.edit_recycleview_spinner_title);
            spinnerCategory = (Spinner) v.findViewById(R.id.spinner_category);
            constraintLayout = (ConstraintLayout) v.findViewById(R.id.edit_recycleview_spinner_layout);
        }
    }

    public static class ClockViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public TextView textClock;
        public Switch aSwitch;
        public TextView textMon,textTue,textWed,textThu,textFri,textSat,textSun,textOk,textDelete;

        public ClockViewHolder(View v){
            super(v);
            cardView = (CardView) v.findViewById(R.id.clockCard);
            textClock = (TextView) v.findViewById(R.id.textClock);
            aSwitch = (Switch) v.findViewById(R.id.clock_switch);
            textMon = (TextView) v.findViewById(R.id.textMonday);
            textTue = (TextView) v.findViewById(R.id.textTuesday);
            textWed = (TextView) v.findViewById(R.id.textWednesday);
            textThu = (TextView) v.findViewById(R.id.textThursday);
            textFri = (TextView) v.findViewById(R.id.textFriday);
            textSat = (TextView) v.findViewById(R.id.textSaturday);
            textSun = (TextView) v.findViewById(R.id.textSunday);
            textOk = (TextView) v.findViewById(R.id.textOk);
            textDelete = (TextView) v.findViewById(R.id.textDelete);
        }
    }

    public WalletAdapter(ArrayList<RecordDetail> dataset,Context context) {
        mDataset.clear();
        mDataset.addAll(dataset);
        this.context = context;
    }


    public WalletAdapter(ArrayList<RecordDetail> dataset,Context context,Activity activity) {
        mDataset.clear();
        mDataset.addAll(dataset);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE.ITEM_TYPE_DATE.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_date, parent, false);
            return new DateViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_RECORD.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item, parent, false);
            return new RecordViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_HEAD.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_head, parent, false);
            return new HeadViewHolder(v);
        }
        else if (viewType == ITEM_TYPE.ITEM_TYPE_CARD.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_card, parent, false);
            return new CardViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_PIECHART.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_piechart, parent, false);
            return new PieChartViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_EDIT_TEXT.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_recycleview_text, parent, false);
            return new EditTextViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_TEXT.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_recycleview_textview, parent, false);
            return new TextViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_BUTTON.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_recycleview_button, parent, false);
            return new ButtonViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_RADIO.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_recycleview_radio, parent, false);
            return new RadioViewHolder(v);
        }
        else if(viewType == ITEM_TYPE.ITEM_TYPE_SPINNER.ordinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_recycleview_spinner, parent, false);
            return new SpinnerViewHolder(v);
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_clockcard, parent, false);
            return new ClockViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final RecordDetail recordDetail = mDataset.get(position);
        if(holder instanceof DateViewHolder){
            ((DateViewHolder)holder).textdate.setText(setDateRegularFormat(recordDetail.getName()));
        }
        else if(holder instanceof RecordViewHolder){
            ((RecordViewHolder)holder).textName.setText(recordDetail.getName());
            int type = recordDetail.getType();
            boolean isClick = recordDetail.isClick();
            String category = "";
            if(isClick){
                ((RecordViewHolder)holder).constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mButtonClickResultListener.onButtonClickResult(recordDetail);
                    }
                });
                category = recordDetail.getCategory();
            }
            else{
                category = recordDetail.getName();
            }

            if(category.equals("Activity")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.expense_activity));
            }
            else if(category.equals("School")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.expense_school));
            }
            else if(category.equals("Breakfast")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.expense_breakfase));
            }
            else if(category.equals("Lunch")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.expense_lunch));
            }
            else if(category.equals("Dinner")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.expense_dinner));
            }
            else if(category.equals("Drink")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.expense_drink));
            }
            else if(category.equals("Salary")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.income_salary));
            }
            else if(category.equals("Home")){
                ((RecordViewHolder)holder).imageView.setImageDrawable(context.getDrawable(R.drawable.incomt_home));
            }

            if(type==0){
                ((RecordViewHolder)holder).textDollar.setTextColor(Color.parseColor("#F8838B"));
                ((RecordViewHolder)holder).textDollar.setText(String.valueOf(-recordDetail.getCost()));
            }
            else{
                ((RecordViewHolder)holder).textDollar.setTextColor(Color.parseColor("#4CAF9B"));
                ((RecordViewHolder)holder).textDollar.setText(String.valueOf(recordDetail.getCost()));
            }
        }
        else if(holder instanceof PieChartViewHolder){
            String centerText = "";
            if(recordDetail.getType()==0){
                centerText += "Expense";
            }
            else{
                centerText += "Income";
            }

            int sum = 0;
            Map<String,Integer> pieValues = new HashMap<>();
            for(int j=0; j<mDataset.size();j++){
                if(mDataset.get(j).getName().equals("pie")){
                    continue;
                }
                sum+=mDataset.get(j).getCost();
                pieValues.put(mDataset.get(j).getName(),mDataset.get(j).getCost());
            }
            centerText += "\n"+String.valueOf(sum);
            ((PieChartViewHolder)holder).pieChart.setUsePercentValues(true);
            ((PieChartViewHolder)holder).pieChart.getDescription().setEnabled(false);
            ((PieChartViewHolder)holder).pieChart.setExtraOffsets(5, 10, 5, 5);
            ((PieChartViewHolder)holder).pieChart.setCenterText(centerText);
            ((PieChartViewHolder)holder).pieChart.setCenterTextSize(22f);
            ((PieChartViewHolder)holder).pieChart.setDrawCenterText(true);
            ((PieChartViewHolder)holder).pieChart.setRotationEnabled(false);
            ((PieChartViewHolder)holder).pieChart.setRotationAngle(0f);
            Legend legend = ((PieChartViewHolder)holder).pieChart.getLegend();
            legend.setEnabled(false);
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
        else if(holder instanceof CardViewHolder){

            ((CardViewHolder)holder).recyclerView.setHasFixedSize(true);

            ArrayList<RecordDetail> mergeData = new ArrayList<>();
            mergeData.clear();
            mergeData.addAll(recordDetail.getRecordDetails());
            WalletAdapter adapter = new WalletAdapter(mergeData,context,activity);
            adapter.setOnButtonClickResultListener(new onButtonClickResultListener() {
                @Override
                public void onButtonClickResult(RecordDetail recordDetail) {
                    Intent intent = new Intent(context,RecordActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id",recordDetail.getId());
                    bundle.putString("name",recordDetail.getName());
                    bundle.putInt("cost",recordDetail.getCost());
                    bundle.putString("date",recordDetail.getDate());
                    bundle.putInt("type",recordDetail.getType());
                    bundle.putString("category",recordDetail.getCategory());
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent,MainActivity.requestCodeRecord);
                }
            });
            ((CardViewHolder)holder).recyclerView.setAdapter(adapter);
            ((CardViewHolder)holder).recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        }
        else if(holder instanceof EditTextViewHolder){
            final String title = recordDetail.getTitle();
            final EditTextViewHolder editTextViewHolder = (EditTextViewHolder) holder;
            ((EditTextViewHolder)holder).textTitle.setText(recordDetail.getTitle());
            ((EditTextViewHolder)holder).editValue.setText(recordDetail.getValue());
            ((EditTextViewHolder)holder).editValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mTextListener.onTextChanged(position,new RecordDetail(title,editTextViewHolder.editValue.getText().toString(),5),true);
                }
            });
            ((EditTextViewHolder)holder).constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextViewHolder.editValue.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextViewHolder.editValue, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }
        else if(holder instanceof TextViewHolder){
            final String title = recordDetail.getTitle();
            final TextViewHolder textViewHolder = (TextViewHolder) holder;
            final Calendar c = Calendar.getInstance();
            boolean isClick = recordDetail.isClick();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            if(isClick){
                if(!recordDetail.getValue().equals("")){
                    String[] date = recordDetail.getValue().split("-");
                    mYear = Integer.valueOf(date[0]);
                    mMonth = Integer.valueOf(date[1])-1;
                    mDay = Integer.valueOf(date[2]);
                }

                String format = setDateFormat(mYear,mMonth,mDay);
                ((TextViewHolder)holder).textValue.setText(format);
                mTextListener.onTextChanged(position,new RecordDetail(title,format,6),true);

                ((TextViewHolder)holder).textConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                String format = setDateFormat(year,month,day);
                                textViewHolder.textValue.setText(format);
                                mTextListener.onTextChanged(position,new RecordDetail(title,format,6),true);
                            }
                        }, mYear,mMonth, mDay).show();
                    }
                });
            }
            else{
                ((TextViewHolder)holder).textValue.setText(recordDetail.getValue());
            }
            ((TextViewHolder)holder).textTitle.setText(recordDetail.getTitle());
        }
        else if(holder instanceof ButtonViewHolder){
            ((ButtonViewHolder)holder).btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mButtonListener.onButtonClick();
                    Log.e("click","Click");
                }
            });
        }
        else if(holder instanceof RadioViewHolder){
            final String title = recordDetail.getTitle();
            ((RadioViewHolder)holder).textTitle.setText(recordDetail.getTitle());
            if(recordDetail.getValue().equals("0")){
                ((RadioViewHolder)holder).radioButtonExpense.setChecked(true);
            }
            else{
                ((RadioViewHolder)holder).radioButtonIncome.setChecked(true);
            }
            ((RadioViewHolder)holder).radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.radioButton_expense:
                            mTextListener.onTextChanged(position,new RecordDetail(title,"0",8),true);
                            Log.d("type","expense");
                            break;
                        case R.id.radioButton_income:
                            mTextListener.onTextChanged(position,new RecordDetail(title,"1",8),true);
                            Log.d("type","income");
                            break;
                    }
                }
            });

        }
        else if(holder instanceof SpinnerViewHolder){
            final String title = recordDetail.getTitle();
            final int currentPosition = position;
            final SpinnerViewHolder spinnerViewHolder = (SpinnerViewHolder) holder;

            ((SpinnerViewHolder)holder).textTitle.setText(title);
            String[] categoryList = recordDetail.getValue().split("-");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,categoryList);
            ((SpinnerViewHolder)holder).spinnerCategory.setAdapter(adapter);
            ((SpinnerViewHolder)holder).spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mTextListener.onTextChanged(currentPosition,new RecordDetail(title,parent.getSelectedItem().toString(),9),true);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ((SpinnerViewHolder)holder).constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinnerViewHolder.spinnerCategory.performClick();
                }
            });
        }
        else if(holder instanceof ClockViewHolder){
            final ClockViewHolder clockViewHolder = (ClockViewHolder) holder;
            final int id = recordDetail.getId();
            final int hour = recordDetail.getHour();
            final int minute = recordDetail.getMinute();
            duration = recordDetail.getDuration();
            int turn = recordDetail.getTurn();

            ((ClockViewHolder)holder).textClock.setText(String.valueOf(new DecimalFormat("00").format(hour)+":"+new DecimalFormat("00").format(minute)));
            ((ClockViewHolder)holder).aSwitch.setChecked(turn==1);
            ((ClockViewHolder)holder).aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int turn = (isChecked)?1:0;
                    mTextListener.onTextChanged(position,new RecordDetail(id,hour,minute,duration,turn,10),false);
                }
            });

            ((ClockViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mButtonClickResultListener.onButtonClickResult(new RecordDetail(id,hour,minute,duration,0,10));
                }
            });
            for(int i=0;i<7;i++) {
                setDurationWeekday((ClockViewHolder)holder,i,duration.get(i));
            }
        }
    }

    private void setDurationWeekday(ClockViewHolder clockViewHolder,int weekday,int on){
        int color;
        if(on == 1){
            color = context.getResources().getColor(R.color.colorAccent);
        }
        else{
            color = context.getResources().getColor(R.color.defaultText);
        }
        switch (weekday){
            case 0:
                clockViewHolder.textSun.setTextColor(color);
                break;
            case 1:
                clockViewHolder.textMon.setTextColor(color);
                break;
            case 2:
                clockViewHolder.textTue.setTextColor(color);
                break;
            case 3:
                clockViewHolder.textWed.setTextColor(color);
                break;
            case 4:
                clockViewHolder.textThu.setTextColor(color);
                break;
            case 5:
                clockViewHolder.textFri.setTextColor(color);
                break;
            case 6:
                clockViewHolder.textSat.setTextColor(color);
                break;
        }
    }

    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        return String.valueOf(year) + "-"
                + new DecimalFormat("00").format(monthOfYear + 1)+ "-"
                + new DecimalFormat("00").format(dayOfMonth);
    }

    private String setDateRegularFormat(String date){
        String str = "";
        String[] tokens = date.split("-");
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        int year = Integer.valueOf(tokens[0]);
        int monthOfYear = Integer.valueOf(tokens[1]);
        int dayOfMonth = Integer.valueOf(tokens[2]);
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

//    private static final int[] PIE_COLORS = {
//            Color.rgb(129, 216, 200), Color.rgb(241, 214, 145),
//            Color.rgb(108, 176, 223), Color.rgb(195, 221, 155),
//            Color.rgb(251, 215, 191), Color.rgb(237, 189, 189),
//            Color.rgb(172, 217, 243)
//    };

    private static final int[] PIE_COLORS = {
            Color.rgb(227, 178, 178), Color.rgb(238, 216, 150),
            Color.rgb(151, 225, 179), Color.rgb(142, 192, 216),
            Color.rgb(207, 186, 234)
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
        piechart = 2
        head = 3
        card = 4
        edittext = 5
        textview = 6
        button = 7*/
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
        else if(layout==3){
            return ITEM_TYPE.ITEM_TYPE_HEAD.ordinal();
        }
        else if(layout==4){
            return ITEM_TYPE.ITEM_TYPE_CARD.ordinal();
        }
        else if(layout==5){
            return ITEM_TYPE.ITEM_TYPE_EDIT_TEXT.ordinal();
        }
        else if(layout==6){
            return ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
        }
        else if(layout==7){
            return ITEM_TYPE.ITEM_TYPE_BUTTON.ordinal();
        }
        else if(layout==8){
            return ITEM_TYPE.ITEM_TYPE_RADIO.ordinal();
        }
        else if(layout==9){
            return ITEM_TYPE.ITEM_TYPE_SPINNER.ordinal();
        }
        else{
            return ITEM_TYPE.ITEM_TYPE_CLOCK.ordinal();
        }

    }

    public void setOnTextChangeListener(onTextChangeListener onTextChangeListener){
        mTextListener = onTextChangeListener;
    }

    public void setOnButtonClickListener(onButtonClickListener onButtonClickListener){
        mButtonListener = onButtonClickListener;
    }

    public void setOnButtonClickResultListener(onButtonClickResultListener onButtonClickResultListener){
        mButtonClickResultListener = onButtonClickResultListener;
    }

    public void updateItem(int position,String value) {
        RecordDetail newRecordDetail = mDataset.get(position);
        newRecordDetail.setValue(value);
        mDataset.set(position,newRecordDetail);
        notifyItemChanged(position);
    }

    public void updateDuration(final int position,RecordDetail recordDetail){
        mDataset.set(position,recordDetail);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                notifyItemChanged(position);
            }
        };
        handler.post(r);
    }



}
