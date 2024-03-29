package dee.wallet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
/**
 * Created by Dee on 2017/11/8.
 *
 */

public class WalletFragment extends Fragment {
    private FrameLayout fragmentContainer;

    public static int selectYear,selectMonth;

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
    private LinearLayout tabLayout;
    private Button btnExpense;
    private Button btnIncome;
    private int offset;

    //FragmentSettingUI
    private static RecyclerView clockView;
    private ArrayList <RecordDetail> clockData;
    private WalletAdapter clockAdapter;

    public static WalletFragment newInstance(int index,int year,int month){
        WalletFragment fragment = new WalletFragment();
        Bundle b = new Bundle();
        b.putInt("index",index);
        selectYear = year;
        selectMonth = month;
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int index = getArguments().getInt("index",0);
        Calendar cal = Calendar.getInstance();
        Log.e("time",selectMonth+" "+selectYear);
        if(selectYear == 0){
            selectYear = cal.get(Calendar.YEAR);
        }
        if(selectMonth == 0){
            selectMonth = cal.get(Calendar.MONTH)+1;
        }
        openDB();
        if(index == 0){
            View view = inflater.inflate(R.layout.fragment_list,container,false);
            setHasOptionsMenu(true);
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
            setHasOptionsMenu(true);
            initWalletSetting(view);
            return view;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int index = getArguments().getInt("index",0);
        if(index == 3){
            inflater.inflate(R.menu.menu_setting, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_addClock){
            Intent intent = new Intent(getContext(),ClockActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("functionCode",false);
            intent.putExtras(bundle);
            getActivity().startActivityForResult(intent,MainActivity.requestCodeClock);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        String SQLHEAD = "SELECT "+DBHelper.CATEGORY_TABLE_NAME+"._type , SUM("+DBHelper.RECORD_TABLE_NAME+"._cost) FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.CATEGORY_TABLE_NAME+"._id = "+DBHelper.RECORD_TABLE_NAME+"._category AND strftime('%Y',"+DBHelper.RECORD_TABLE_NAME+"._date)='"+selectYear+"' AND strftime('%m',"+DBHelper.RECORD_TABLE_NAME+"._date) = '"+selectMonth+"' GROUP BY "+DBHelper.CATEGORY_TABLE_NAME+"._type";
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
        String SQL = "SELECT * FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.RECORD_TABLE_NAME+"._category="+DBHelper.CATEGORY_TABLE_NAME+"._id AND strftime('%Y',"+DBHelper.RECORD_TABLE_NAME+"._date)='"+selectYear+"' AND strftime('%m',"+DBHelper.RECORD_TABLE_NAME+"._date) = '"+selectMonth+"' ORDER BY "+DBHelper.RECORD_TABLE_NAME+"._date DESC,"+DBHelper.RECORD_TABLE_NAME+"._id DESC";
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

                RecordDetail recordDetail = new RecordDetail(id,name,cost,date,category,type,1,true);
                tmp.add(recordDetail);
            }
            RecordDetail mergeData = new RecordDetail(tmp,4);
            recordData.add(mergeData);
        }
        else{

        }
        cursor.close();
        WalletAdapter adapter = new WalletAdapter(recordData,getContext(),getActivity());
        recyclerView.setAdapter(adapter);
    }

    private void initWalletInput(View view){
        //Initial UI
        inputView = (RecyclerView) view.findViewById(R.id.fragment_input_list);
        inputView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getActivity());
        inputView.setLayoutManager(layoutManager);
        LoadWalletInputData();
    }

    private void LoadWalletInputData(){
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
                if(!inputDetails.get(0).getValue().equals("") && !inputDetails.get(1).getValue().equals("")){
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
    private String expenseCategory;
    private String incomeCategory;

    private void LoadSpinner(){
        String SQL = "SELECT * FROM " + DBHelper.CATEGORY_TABLE_NAME;
        expenseCategory = "";
        incomeCategory = "";
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
        tabLayout = (LinearLayout) view.findViewById(R.id.tab_layout);
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
        historyView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                offset += dy;
//                Log.e("offset",String.valueOf(offset));
                if(offset>70) {
                    tabLayout.animate().translationY(-125).start();
                }
                else if(offset<200) {
                    tabLayout.animate().translationY(0).start();
                }
            }
        });
        LoadWalletHistoryData(0);
    }

    private void LoadWalletHistoryData(int index){
        String SQL = "SELECT "+DBHelper.RECORD_TABLE_NAME+"._id , SUM("+DBHelper.RECORD_TABLE_NAME+"._cost),"+DBHelper.CATEGORY_TABLE_NAME+"._name,"+DBHelper.CATEGORY_TABLE_NAME+"._type FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.CATEGORY_TABLE_NAME+"._type="+index+" AND "+DBHelper.CATEGORY_TABLE_NAME+"._id="+DBHelper.RECORD_TABLE_NAME+"._category  AND strftime('%Y',"+DBHelper.RECORD_TABLE_NAME+"._date)='"+selectYear+"' AND strftime('%m',"+DBHelper.RECORD_TABLE_NAME+"._date) = '"+selectMonth+"' GROUP BY "+DBHelper.RECORD_TABLE_NAME+"._category";

        Cursor cursor = db.rawQuery(SQL,null);
        ArrayList <RecordDetail> historyData = new ArrayList<>();
        historyData.add(new RecordDetail(0,"pie",0,index,2,false));
        int count = cursor.getCount();
        if(count>0){
            cursor.moveToFirst();
            for(int i = 0; i<count;i++){
                cursor.moveToPosition(i);
                int id = cursor.getInt(0);
                int cost = cursor.getInt(1);
                String name = cursor.getString(2);
                int type = cursor.getInt(3);
                RecordDetail recordDetail = new RecordDetail(id,name,cost,type,1,false);
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
                clockData.set(pos,input);
                int id = input.getId();
                int hour = input.getHour();
                int minute = input.getMinute();
                int turn = input.getTurn();
                ArrayList<Integer> durationList = new ArrayList<>();
                durationList.addAll(input.getDuration());
                String duration = "";
                for(int i=0;i<durationList.size();i++){
                    duration += (durationList.get(i)==1)?'1':'0';
                }

                String where = "_id = "+id;
                ContentValues contentValues = new ContentValues();
                contentValues.put("_turn",turn);
                db.update(DBHelper.CLOCK_TABLE_NAME,contentValues,where,null);
                if(turn == 1){
                    setAlarm(id,duration,hour,minute);
                }
                else{
                    cancelAlarm(id);
                }
            }
        });
        clockAdapter.setOnButtonClickResultListener(new onButtonClickResultListener() {
            @Override
            public void onButtonClickResult(RecordDetail recordDetail) {
                Intent intent = new Intent(getContext(),ClockActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("functionCode",true);
                bundle.putInt("id",recordDetail.getId());
                bundle.putInt("hour",recordDetail.getHour());
                bundle.putInt("minute",recordDetail.getMinute());
                bundle.putIntegerArrayList("duration",recordDetail.getDuration());
                intent.putExtras(bundle);
                getActivity().startActivityForResult(intent,MainActivity.requestCodeClock);
            }
        });
        clockView.setAdapter(clockAdapter);

    }

    private void setAlarm(int id,String duration,int editHour,int editMinute){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,editHour);
        cal.set(Calendar.MINUTE, editMinute);
        cal.set(Calendar.SECOND,1);

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("msg", "alarm");
        intent.putExtra("duration",duration);

        PendingIntent pi = PendingIntent.getBroadcast(getContext(),id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);
    }

    private void cancelAlarm(int id){
        Log.e("cancel","cancel");
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getContext(),id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        pi = null;
        am = null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDB();
    }
}
