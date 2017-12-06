package dee.wallet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {
    //Database
    private DBHelper dbHelper = null;
    private SQLiteDatabase db;

    private int id;
    private boolean isMenuChange = false;
    private RecyclerView inputView;
    private ArrayList<RecordDetail> inputDetails;
    private WalletAdapter inputAdapter;

    private String actionDelete,actionEdit,actionFinish,actionCancel;
    private final int idFinish = 784, idCancel = 856;
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
        id = bundle.getInt("id");
        initialData(true);

        actionDelete = getString(R.string.action_delete);
        actionCancel = getString(R.string.action_cancel);
        actionEdit = getString(R.string.action_edit);
        actionFinish = getString(R.string.action_finish);

    }

    /**
     *
     * @param index 0=show 1=edit
     */

    private void initialData(boolean index){
        String SQL = "SELECT * FROM "+DBHelper.RECORD_TABLE_NAME+","+DBHelper.CATEGORY_TABLE_NAME+" WHERE "+DBHelper.RECORD_TABLE_NAME+"._category="+DBHelper.CATEGORY_TABLE_NAME+"._id AND "+DBHelper.RECORD_TABLE_NAME+"._id="+id+" ORDER BY DATETIME("+DBHelper.RECORD_TABLE_NAME+"._date) DESC,"+DBHelper.RECORD_TABLE_NAME+"._id DESC";
        Cursor cursor = db.rawQuery(SQL,null);
        int count = cursor.getCount();
        if(count>0) {
            cursor.moveToFirst();
            String name = cursor.getString(1);
            int cost = cursor.getInt(2);
            String date = cursor.getString(3);
            String category = cursor.getString(7);
            int type = cursor.getInt(6);

            if(index){
                String[] titles = {"Name","Cost","Date","Type","Category"};
                int[] layouts = {6,6,6,6,6};
                final String[] values = {name,String.valueOf(cost),date,((type==0)?"Expense":"Income"),category};
                inputDetails = new ArrayList<>();
                for(int i=0;i<titles.length;i++){
                    inputDetails.add(new RecordDetail(titles[i],values[i],layouts[i],false));
                }
                inputAdapter = new WalletAdapter(inputDetails,RecordActivity.this);
            }
            else{
                int[] layouts = {5,5,6,8,9};
                LoadSpinner();
                String[] titles = {"Name","Cost","Date","Type","Category"};
                String[] values = {name,String.valueOf(cost),date,String.valueOf(type),((type==0)?expenseCategory:incomeCategory)};
                inputDetails = new ArrayList<>();
                for(int i=0;i<titles.length;i++){
                    inputDetails.add(new RecordDetail(titles[i],values[i],layouts[i]));
                }
                inputAdapter = new WalletAdapter(inputDetails,this);
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
                        }
                    }
                });
                inputAdapter.setOnButtonClickListener(new onButtonClickListener() {
                    @Override
                    public void onButtonClick() {
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
                        Toast.makeText(RecordActivity.this,"Submit",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            inputView.setAdapter(inputAdapter);
            inputView.addItemDecoration(new DividerItemDecoration(RecordActivity.this,DividerItemDecoration.VERTICAL));

        }
        cursor.close();

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


    private void openDB(){
        dbHelper = new DBHelper(RecordActivity.this);
//        getContext().deleteDatabase(DBHelper.DATABASE_NAME);
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        dbHelper.close();
    }

    @Override
    public void onBackPressed() {
        if(isMenuChange){
            initialData(true);
            isMenuChange = false;
        }
        else{
            setResult(MainActivity.requestCodeRecord,getIntent());
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_record,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(isMenuChange){
            menu.add(0,idCancel,0,actionCancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(0,idFinish,0,actionFinish).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        else{
            menu.add(0,R.id.action_edit,0,actionEdit).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(0,R.id.action_delete,0,actionDelete).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                initialData(false);
                isMenuChange = true;
                invalidateOptionsMenu();
                return true;
            case R.id.action_delete:
                deleteData();
                return true;
            case idCancel:
                initialData(true);
                isMenuChange = false;
                invalidateOptionsMenu();
                return true;
            case idFinish:
                updateData();
                initialData(true);
                isMenuChange = false;
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateData(){
        String where = "_id = "+id;
        String name = inputDetails.get(0).getValue();
        int cost = Integer.parseInt(inputDetails.get(1).getValue());
        String date = inputDetails.get(2).getValue();
        int type = Integer.valueOf(inputDetails.get(3).getValue());
        int category = queryCategory(type,inputDetails.get(4).getValue());

        ContentValues contentValues = new ContentValues();
        contentValues.put("_name",name);
        contentValues.put("_cost",cost);
        contentValues.put("_date",date);
        contentValues.put("_category",category);
        db.update(DBHelper.RECORD_TABLE_NAME,contentValues,where,null);
        Toast.makeText(RecordActivity.this,"Update",Toast.LENGTH_SHORT).show();
    }

    private void deleteData(){
        new AlertDialog.Builder(RecordActivity.this)
                .setTitle(R.string.alert_delete_record)
                .setMessage(R.string.alert_delete_record_info)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String where = "_id = "+id;
                        db.delete(DBHelper.RECORD_TABLE_NAME,where,null);
                        setResult(MainActivity.requestCodeRecord,getIntent());
                        finish();
                    }
                })
                .setNegativeButton(R.string.no,null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }
}
