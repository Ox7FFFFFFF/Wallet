package dee.wallet;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dee on 2017/11/8.
 *
 */

public class WalletFragment extends Fragment {
    private FrameLayout fragmentContainer;

    //Database
    DBHelper dbHelper = null;
    SQLiteDatabase db;

    //FragmentDetailUI
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //FragmentInputUI
    EditText editName;
    EditText editDollar;
    TextView editDate;
    RadioGroup radioGroupType;
    RadioButton radioButtonIncome;
    RadioButton radioButtonExpense;
    Spinner spinnerCategory;
    Button buttonSubmit;

    //FragmentHistoryUI
    PieChart pieChart;
    private static RecyclerView historyView;
    private RecyclerView.LayoutManager historyLayoutManager;


    public static WalletFragment newInstance(int index){
        WalletFragment fragment = new WalletFragment();
        Bundle b = new Bundle();
        b.putInt("index",index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int index = getArguments().getInt("index",0);
        openDB();
        if(index == 0){
            View view = inflater.inflate(R.layout.fragment_home,container,false);
            initWalletHome(view);
            return view;
        }
        else if(index==1){
            View view = inflater.inflate(R.layout.fragment_list,container,false);
            initWalletList(view);
            return view;
        }
        else if(index==2){
            View view = inflater.inflate(R.layout.fragment_input,container,false);
            initWalletInput(view);
            return view;
        }
        else if(index==3){
            View view = inflater.inflate(R.layout.fragment_history,container,false);
            initWalletHistory(view);
            return view;
        }
        else {
            View view = inflater.inflate(R.layout.fragment_setting,container,false);
            initWalletSetting(view);
            return view;
        }
    }

    private void initWalletHome(View view){
        MainActivity mainActivity = (MainActivity) getActivity();
        TextView textView = view.findViewById(R.id.text_balance);
        //~~~~~~~~~~
    }

    private void initWalletList(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        LoadWalletDetailData();
    }

    private void LoadWalletDetailData(){
        String SQL = "SELECT * FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.RECORD_TABLE_NAME+"._category="+DBHelper.CATEGORY_TABLE_NAME+"._id ORDER BY "+DBHelper.RECORD_TABLE_NAME+"._date DESC,"+DBHelper.RECORD_TABLE_NAME+"._id DESC";
        Cursor cursor = db.rawQuery(SQL,null);
        ArrayList <RecordDetail> recordData = new ArrayList<>();
        int count = cursor.getCount();
        String newDate="";
        if(count>0){
            cursor.moveToFirst();
            for(int i = 0; i<count;i++){
                cursor.moveToPosition(i);
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int cost = cursor.getInt(2);
                String date = cursor.getString(3);
                String category = cursor.getString(7);
                int type = cursor.getInt(6);

                if(!date.equals(newDate)){
                    newDate = date;
                    RecordDetail dateDetail = new RecordDetail(-1,date);
                    recordData.add(dateDetail);
                }

                RecordDetail recordDetail = new RecordDetail(id,name,cost,date,category,type);
                recordData.add(recordDetail);
            }
        }
        else{

        }
        cursor.close();
        WalletAdapter adapter = new WalletAdapter(recordData);
        recyclerView.setAdapter(adapter);
    }

    private void initWalletInput(View view){
        editName = (EditText) view.findViewById(R.id.edit_name);
        editDollar = (EditText) view.findViewById(R.id.edit_dollar);
        editDate = (TextView) view.findViewById(R.id.edit_date);
        radioGroupType = (RadioGroup) view.findViewById(R.id.radioGroup_type);
        radioButtonIncome = (RadioButton) view.findViewById(R.id.radioButton_income);
        radioButtonExpense = (RadioButton) view.findViewById(R.id.radioButton_expense);
        spinnerCategory = (Spinner) view.findViewById(R.id.spinner_category);
        buttonSubmit = (Button) view.findViewById(R.id.btn_submit);

        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        editDate.setText(setDateFormat(mYear,mMonth,mDay));
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDateFormat(year,month,day);
                        editDate.setText(format);
                    }
                }, mYear,mMonth, mDay).show();
            }
        });
        radioGroupType.setOnCheckedChangeListener(onCheckedChangeListener);
        radioButtonExpense.setChecked(true);
        LoadSpinner(0);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                int cost = Integer.parseInt(editDollar.getText().toString());
                String date = editDate.getText().toString();
                int type = (radioButtonExpense.isChecked())?0:1;
//                cost = type==0 ? -cost:cost;
                String type_name = spinnerCategory.getSelectedItem().toString();
                int category = queryCategory(type,type_name);
                ContentValues contentValues = new ContentValues();
                contentValues.put("_name",name);
                contentValues.put("_cost",cost);
                contentValues.put("_date",date);
                contentValues.put("_category",category);
                db.insert(DBHelper.RECORD_TABLE_NAME,null,contentValues);
                Toast.makeText(getContext(),"Submit",Toast.LENGTH_SHORT).show();
                LoadWalletDetailData();

                editName.setText("");
                editDollar.setText("");
                editDate.setText(setDateFormat(mYear,mMonth,mDay));
                radioButtonExpense.setChecked(true);
                LoadSpinner(0);
            }
        });

    }

    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
    }

    private int queryCategory(int type,String name){
        String SQL = "SELECT * FROM "+DBHelper.CATEGORY_TABLE_NAME+" WHERE _type="+type+" AND _name='"+name+"'";
        Cursor cursor = db.rawQuery(SQL,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        else{
            return -1;
        }
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.radioButton_expense:
                    LoadSpinner(0);
                    Log.d("type","expense");
                    break;
                case R.id.radioButton_income:
                    Log.d("type","income");
                    LoadSpinner(1);
                    break;
            }
        }
    };

    /***
     *
     * @param index 1=income 0=expense
     */
    private void LoadSpinner(int index){
        String SQL = "SELECT * FROM "+DBHelper.CATEGORY_TABLE_NAME+" WHERE _type="+index;
        Cursor cursor = db.rawQuery(SQL,null);
        int count = cursor.getCount();
        if(count>0){
            String[] categoryList = new String[count];
            cursor.moveToFirst();
            for(int i=0;i<count;i++){
                cursor.moveToPosition(i);
                categoryList[i] = cursor.getString(2);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,categoryList);
            spinnerCategory.setAdapter(adapter);
        }
        else{

        }
    }

    private void initWalletHistory(View view){
        pieChart = (PieChart) view.findViewById(R.id.pie_chart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setCenterText("expense");
        pieChart.setCenterTextSize(22f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationEnabled(false);
        pieChart.setRotationAngle(0f);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        historyView = (RecyclerView) view.findViewById(R.id.fragment_history_list);
        historyView.setHasFixedSize(true);
        historyLayoutManager = new LinearLayoutManager(getActivity());
        historyView.setLayoutManager(historyLayoutManager);
        LoadWalletHistoryData(0);
    }

    private void LoadWalletHistoryData(int index){
        String SQL = "SELECT "+DBHelper.RECORD_TABLE_NAME+"._id , SUM("+DBHelper.RECORD_TABLE_NAME+"._cost),"+DBHelper.CATEGORY_TABLE_NAME+"._name FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.CATEGORY_TABLE_NAME+"._type="+index+" AND "+DBHelper.CATEGORY_TABLE_NAME+"._id="+DBHelper.RECORD_TABLE_NAME+"._category GROUP BY "+DBHelper.RECORD_TABLE_NAME+"._category" ;
        Cursor cursor = db.rawQuery(SQL,null);
        ArrayList <RecordDetail> historyData = new ArrayList<>();
        int count = cursor.getCount();
        if(count>0){
            cursor.moveToFirst();
            for(int i = 0; i<count;i++){
                cursor.moveToPosition(i);
                int id = cursor.getInt(0);
                int cost = cursor.getInt(1);
                String name = cursor.getString(2);
                RecordDetail recordDetail = new RecordDetail(id,name,cost);
                historyData.add(recordDetail);
            }
        }
        else{

        }
        cursor.close();
        //TODO
        WalletAdapter adapter = new WalletAdapter(historyData);
        historyView.setAdapter(adapter);

        Map<String,Integer> pieValues = new HashMap<>();
        for(int j=0; j<historyData.size();j++){
            pieValues.put(historyData.get(j).getName(),historyData.get(j).getCost());
        }
        setPieChartData(pieChart, pieValues);
        pieChart.animateX(1500, Easing.EasingOption.EaseInOutQuad);

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


    private void initWalletSetting(View view){

    }

    private void openDB(){
        dbHelper = new DBHelper(getContext());
//        getContext().deleteDatabase(DBHelper.DATABASE_NAME);
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        dbHelper.close();
    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        // Do what you want here, for example animate the content
        if (fragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }
    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }
}
