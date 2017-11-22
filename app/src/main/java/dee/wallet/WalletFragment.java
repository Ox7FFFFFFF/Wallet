package dee.wallet;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
import java.util.ArrayList;
import java.util.Calendar;
/**
 * Created by Dee on 2017/11/8.
 *
 */

public class WalletFragment extends Fragment {
    private FrameLayout fragmentContainer;

    private int selectYear,selectMonth;

    //Database
    DBHelper dbHelper = null;
    SQLiteDatabase db;

    //FragmentDetailUI
    private static RecyclerView recyclerView;

    //FragmentInputUI
    private static RecyclerView inputView;
    private ArrayList<RecordDetail> inputDetails;
    private WalletAdapter inputAdapter;

    //FragmentHistoryUI
    private static RecyclerView historyView;
    private RecyclerView.LayoutManager historyLayoutManager;
    private Button btnExpense;
    private Button btnIncome;

    //FragmentSettingUI
    private static RecyclerView clockView;
    private ArrayList <RecordDetail> clockData;
    private WalletAdapter clockAdapter;

    public static WalletFragment newInstance(int index,int year,int month){
        WalletFragment fragment = new WalletFragment();
        Bundle b = new Bundle();
        b.putInt("index",index);
        b.putInt("year",year);
        b.putInt("month",month);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int index = getArguments().getInt("index",0);
        selectYear = getArguments().getInt("year",2017);
        selectMonth = getArguments().getInt("month",1);
        openDB();
        if(index == 0){
            View view = inflater.inflate(R.layout.fragment_list,container,false);
            initWalletList(view);
            return view;
        }
        else if(index==1){
            View view = inflater.inflate(R.layout.fragment_input,container,false);
            initWalletInput(view);
            return view;
        }
        else if(index==2){
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

    private void initWalletList(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        LoadWalletDetailData();
    }

    private void LoadWalletDetailData(){
        ArrayList <RecordDetail> recordData = new ArrayList<>();

        //Head
        String SQLHEAD = "SELECT "+DBHelper.CATEGORY_TABLE_NAME+"._type , SUM("+DBHelper.RECORD_TABLE_NAME+"._cost) FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.CATEGORY_TABLE_NAME+"._id = "+DBHelper.RECORD_TABLE_NAME+"._category GROUP BY "+DBHelper.CATEGORY_TABLE_NAME+"._type";
        Cursor cursorHead = db.rawQuery(SQLHEAD,null);
        int countHead = cursorHead.getCount();
        cursorHead.moveToFirst();
        int expense = 0;
        int income = 0;
        for(int i=0;i<countHead;i++){
            cursorHead.moveToPosition(i);
            int sum = cursorHead.getInt(1);
            if(i==0){
                expense = sum;
            }
            else {
                income = sum;
            }
        }
        recordData.add(new RecordDetail(expense,income,3));
        cursorHead.close();
        //Record
        String SQL = "SELECT * FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.RECORD_TABLE_NAME+"._category="+DBHelper.CATEGORY_TABLE_NAME+"._id ORDER BY DATETIME("+DBHelper.RECORD_TABLE_NAME+"._date) DESC,"+DBHelper.RECORD_TABLE_NAME+"._id DESC";
        Cursor cursor = db.rawQuery(SQL,null);
        int count = cursor.getCount();
        String newDate="";
        boolean isFirst = true;
        ArrayList<RecordDetail> tmp = new ArrayList<>();
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
                    if(isFirst){
                        isFirst = false;
                    }
                    else{
                        RecordDetail mergeData = new RecordDetail(tmp,4);
                        recordData.add(mergeData);
                    }
                    newDate = date;
                    tmp.clear();
                    RecordDetail dateDetail = new RecordDetail(-1,date,0);
                    tmp.add(dateDetail);
                }

                RecordDetail recordDetail = new RecordDetail(id,name,cost,date,category,type,1);
                tmp.add(recordDetail);
            }
            RecordDetail mergeData = new RecordDetail(tmp,4);
            recordData.add(mergeData);
        }
        else{

        }
        cursor.close();
        WalletAdapter adapter = new WalletAdapter(recordData,getContext());
        recyclerView.setAdapter(adapter);
    }

    private void initWalletInput(View view){
        //Initial UI
        inputView = (RecyclerView) view.findViewById(R.id.fragment_input_list);
        inputView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getActivity());
        inputView.setLayoutManager(layoutManager);

        //Initial data value
        final String[] titles = {"Name","Cost","Date","Type","Category","Submit"};
        final int[] layouts = {5,5,6,8,9,7};
        LoadSpinner();
        final String[] values = {"","","","0",expenseCategory,""};
        inputDetails = new ArrayList<>();
        for(int i=0;i<titles.length;i++){
            inputDetails.add(new RecordDetail(titles[i],values[i],layouts[i]));
        }
        inputAdapter = new WalletAdapter(inputDetails,getContext());
        inputAdapter.setOnTextChangeListener(new onTextChangeListener() {
            @Override
            public void onTextChanged(int pos,RecordDetail input,boolean isUpdate) {
                inputDetails.set(pos,input);
                if(input.getLayout()==8){
                    //update spinner
                    int type = Integer.valueOf(input.getValue());
                    if(type==0){
                        inputAdapter.updateItem(4,expenseCategory);
                    }
                    else{
                        inputAdapter.updateItem(4,incomeCategory);
                    }
//                    inputAdapter.notifyItemChanged(4);
                }
            }
        });
        inputAdapter.setOnButtonClickListener(new onButtonClickListener() {
            @Override
            public void onButtonClick() {
//                for(int i=0;i<inputDetails.size();i++){
//                    Log.e("value",inputDetails.get(i).getValue());
//                }
                String name = inputDetails.get(0).getValue();
                int cost = Integer.parseInt(inputDetails.get(1).getValue());
                String date = inputDetails.get(2).getValue();
                int type = Integer.valueOf(inputDetails.get(3).getValue());
                String type_name = inputDetails.get(4).getValue();
                int category = queryCategory(type,type_name);
                ContentValues contentValues = new ContentValues();
                contentValues.put("_name",name);
                contentValues.put("_cost",cost);
                contentValues.put("_date",date);
                contentValues.put("_category",category);
                db.insert(DBHelper.RECORD_TABLE_NAME,null,contentValues);
                Toast.makeText(getContext(),"Submit",Toast.LENGTH_SHORT).show();
                LoadWalletDetailData();
                LoadWalletHistoryData(0);
            }
        });
        inputView.setAdapter(inputAdapter);
        inputView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
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

    /***
     *
     * @param index 1=income 0=expense
     */
    private String expenseCategory ="";
    private String incomeCategory = "";

    private void LoadSpinner(){
        String SQL = "SELECT * FROM " + DBHelper.CATEGORY_TABLE_NAME;
        Cursor cursor = db.rawQuery(SQL, null);
        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int type = cursor.getInt(1);
                if (type == 0) {
                    expenseCategory += cursor.getString(2) + "-";
                } else {
                    incomeCategory += cursor.getString(2) + "-";
                }
            }
        }
        else {

        }
        cursor.close();
    }

    private void initWalletHistory(View view){
        historyView = (RecyclerView) view.findViewById(R.id.fragment_history_list);
        btnExpense = (Button) view.findViewById(R.id.btn_expense);
        btnIncome = (Button) view.findViewById(R.id.btn_income);

        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadWalletHistoryData(0);
            }
        });

        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadWalletHistoryData(1);
            }
        });

        historyView.setHasFixedSize(true);
        historyLayoutManager = new LinearLayoutManager(getActivity());
        historyView.setLayoutManager(historyLayoutManager);
        historyView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        LoadWalletHistoryData(0);
    }

    private void LoadWalletHistoryData(int index){
        String SQL = "SELECT "+DBHelper.RECORD_TABLE_NAME+"._id , SUM("+DBHelper.RECORD_TABLE_NAME+"._cost),"+DBHelper.CATEGORY_TABLE_NAME+"._name,"+DBHelper.CATEGORY_TABLE_NAME+"._type FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.CATEGORY_TABLE_NAME+"._type="+index+" AND "+DBHelper.CATEGORY_TABLE_NAME+"._id="+DBHelper.RECORD_TABLE_NAME+"._category GROUP BY "+DBHelper.RECORD_TABLE_NAME+"._category";

        Cursor cursor = db.rawQuery(SQL,null);
        ArrayList <RecordDetail> historyData = new ArrayList<>();
        historyData.add(new RecordDetail(0,"pie",0,index,2));
        int count = cursor.getCount();
        if(count>0){
            cursor.moveToFirst();
            for(int i = 0; i<count;i++){
                cursor.moveToPosition(i);
                int id = cursor.getInt(0);
                int cost = cursor.getInt(1);
                String name = cursor.getString(2);
                int type = cursor.getInt(3);
                RecordDetail recordDetail = new RecordDetail(id,name,cost,type,1);
                historyData.add(recordDetail);
            }
        }
        else{

        }
        cursor.close();
        WalletAdapter adapter = new WalletAdapter(historyData,getContext());
        historyView.setAdapter(adapter);
    }

    private void initWalletSetting(View view){
        clockView = (RecyclerView) view.findViewById(R.id.fragment_clock_list);
        clockView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getActivity());
        clockView.setLayoutManager(layoutManager);
        LoadWalletSettingData();
    }

    private void LoadWalletSettingData(){
        String SQL = "SELECT * FROM "+DBHelper.CLOCK_TABLE_NAME ;

        Cursor cursor = db.rawQuery(SQL,null);
        clockData = new ArrayList<>();
        int count = cursor.getCount();
        if(count>0){
            cursor.moveToFirst();
            for(int i = 0; i<count;i++){
                cursor.moveToPosition(i);
                int id = cursor.getInt(0);
                int hour = cursor.getInt(1);
                int minute = cursor.getInt(2);
                String duration = cursor.getString(3);
                ArrayList<Integer> durationList = new ArrayList<>();
                for(int j=0;j<duration.length();j++){
                    char c = duration.charAt(j);
                    if(c=='1'){
                        durationList.add(1);
                    }
                    else{
                        durationList.add(0);
                    }
                }
                int turn = cursor.getInt(4);
                RecordDetail recordDetail = new RecordDetail(id,hour,minute,durationList,turn,10);
                clockData.add(recordDetail);
            }
        }
        else{

        }
        cursor.close();
        clockAdapter = new WalletAdapter(clockData,getContext());
        clockAdapter.setOnTextChangeListener(new onTextChangeListener() {
            @Override
            public void onTextChanged(int pos, RecordDetail input,boolean isUpdate) {
                if(isUpdate){
                    clockData.set(pos,input);
                    clockAdapter.updateDuration(pos,input);
                    Log.e("duration",input.getDuration().toString());
//                clockAdapter.notifyItemChanged(pos);
                }
            }

        });
        clockView.setAdapter(clockAdapter);

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
