package dee.wallet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {
    //Database
    DBHelper dbHelper = null;
    SQLiteDatabase db;
    private RecyclerView inputView;
    private ArrayList<RecordDetail> inputDetails;
    private WalletAdapter inputAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        openDB();
        inputView = (RecyclerView) findViewById(R.id.record_activity_list);
        inputView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(RecordActivity.this);
        inputView.setLayoutManager(layoutManager);

        //Initial data value
        Bundle bundle = getIntent().getExtras();
        final int id = bundle.getInt("id");
        final String name = bundle.getString("name");
        final int cost = bundle.getInt("cost");
        final String date = bundle.getString("date");
        final int type = bundle.getInt("type");
        final String category = bundle.getString("category");
        final String[] titles = {"Name","Cost","Date","Type","Category"};
        final int[] layouts = {6,6,6,6,6};
        final String[] values = {name,String.valueOf(cost),date,((type==0)?"Expense":"Income"),category};
        inputDetails = new ArrayList<>();
        for(int i=0;i<titles.length;i++){
            inputDetails.add(new RecordDetail(titles[i],values[i],layouts[i],false));
        }
        inputAdapter = new WalletAdapter(inputDetails,RecordActivity.this);
//        inputAdapter.setOnTextChangeListener(new onTextChangeListener() {
//            @Override
//            public void onTextChanged(int pos,RecordDetail input,boolean isUpdate) {
//                inputDetails.set(pos,input);
//                if(input.getLayout()==8){
//                    //update spinner
//                    int type = Integer.valueOf(input.getValue());
//                    if(type==0){
//                        inputAdapter.updateItem(4,expenseCategory);
//                    }
//                    else{
//                        inputAdapter.updateItem(4,incomeCategory);
//                    }
////                    inputAdapter.notifyItemChanged(4);
//                }
//            }
//        });
//        inputAdapter.setOnButtonClickListener(new onButtonClickListener() {
//            @Override
//            public void onButtonClick() {
////                for(int i=0;i<inputDetails.size();i++){
////                    Log.e("value",inputDetails.get(i).getValue());
////                }
//                String name = inputDetails.get(0).getValue();
//                int cost = Integer.parseInt(inputDetails.get(1).getValue());
//                String date = inputDetails.get(2).getValue();
//                int type = Integer.valueOf(inputDetails.get(3).getValue());
//                String type_name = inputDetails.get(4).getValue();
//                int category = queryCategory(type,type_name);
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("_name",name);
//                contentValues.put("_cost",cost);
//                contentValues.put("_date",date);
//                contentValues.put("_category",category);
//                db.insert(DBHelper.RECORD_TABLE_NAME,null,contentValues);
//                Toast.makeText(RecordActivity.this,"Submit",Toast.LENGTH_SHORT).show();
//
//            }
//        });
        inputView.setAdapter(inputAdapter);
        inputView.addItemDecoration(new DividerItemDecoration(RecordActivity.this,DividerItemDecoration.VERTICAL));

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

    //TODO create option menu(edit) & edit function

    private void openDB(){
        dbHelper = new DBHelper(RecordActivity.this);
//        getContext().deleteDatabase(DBHelper.DATABASE_NAME);
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        dbHelper.close();
    }
}
