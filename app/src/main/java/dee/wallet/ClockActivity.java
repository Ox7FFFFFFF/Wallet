package dee.wallet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;


public class ClockActivity extends AppCompatActivity {
    //Database
    private DBHelper dbHelper = null;
    private SQLiteDatabase db;

    private TimePicker timePicker;
    private LinearLayout linearLayout;
    private TextView textWeekday;
    private int id;
    private boolean functionCode;
    private int editHour;
    private int editMinute;
    private String[] items = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    private boolean[] itemsChecked;
    private ArrayList<Integer> durationList;
    private boolean isMenuChange;
    private final int idDelete = 55123;
    private String actionDelete,actionSave,actionCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_clock);
        setSupportActionBar(toolbar);
        openDB();
        actionDelete = getString(R.string.action_delete);
        actionSave = getString(R.string.action_save);
        actionCancel = getString(R.string.action_cancel);

        Bundle bundle = getIntent().getExtras();
        functionCode = bundle.getBoolean("functionCode");
        if(functionCode){ //edit
            id = bundle.getInt("id");
            editHour = bundle.getInt("hour");
            editMinute = bundle.getInt("minute");
            durationList = new ArrayList<>();
            durationList.clear();
            durationList.addAll(bundle.getIntegerArrayList("duration"));
            isMenuChange = true;
        }
        else{//add
            editHour = 12;
            editMinute = 0;
            durationList = new ArrayList<>();
            for(int i=0;i<7;i++){
                durationList.add(1);
            }
            isMenuChange = false;
        }

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        linearLayout = (LinearLayout) findViewById(R.id.linearClock);
        textWeekday = (TextView) findViewById(R.id.textWeekday);

        timePicker.setCurrentHour(editHour);
        timePicker.setCurrentMinute(editMinute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                editHour = hourOfDay;
                editMinute = minute;
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemsChecked = new boolean[items.length];
                for(int i=0;i<items.length;i++){
                    itemsChecked[i] = (durationList.get(i)==1);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ClockActivity.this);
                builder.setTitle("Duration")
                        .setMultiChoiceItems(items,itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                String msg = "";
                                for (int index = 0; index < items.length; index++) {
                                    if (itemsChecked[index]){
                                        msg += items[index]+" ";
                                        durationList.set(index,1);
                                    }
                                    else{
                                        durationList.set(index,0);
                                    }
                                }
                                textWeekday.setText(msg);
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel",null).show();
            }
        });

        String msg = "";
        for (int index = 0; index < durationList.size(); index++) {
            if (durationList.get(index)==1){
                msg += items[index]+" ";
            }
        }
        textWeekday.setText(msg);

    }


    private void openDB(){
        dbHelper = new DBHelper(ClockActivity.this);
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_clock,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(isMenuChange){
            menu.add(0,idDelete,0,actionDelete).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(0,R.id.action_save,0,actionSave).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        else{
            menu.add(0,R.id.action_cancel,0,actionCancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(0,R.id.action_save,0,actionSave).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                if(functionCode){
                    updateDate();
                }
                else{
                    saveData();
                }
                return true;
            case idDelete:
                deleteData();
                return true;
            case R.id.action_cancel:
                setResult(MainActivity.requestCodeClock,getIntent());
                ClockActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveData(){
        String duration = "";
        for(int i=0;i<durationList.size();i++){
            duration += (durationList.get(i)==1)?'1':'0';
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("_hour",editHour);
        contentValues.put("_minute",editMinute);
        contentValues.put("_duration",duration);
        contentValues.put("_turn",1);
        long id =  db.insert(DBHelper.CLOCK_TABLE_NAME,null,contentValues);
        setAlarm((int)id,duration);
    }

    private void updateDate(){
        String duration = "";
        for(int i=0;i<durationList.size();i++){
            duration += (durationList.get(i)==1)?'1':'0';
        }
        String where = "_id = "+id;
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id",id);
        contentValues.put("_hour",editHour);
        contentValues.put("_minute",editMinute);
        contentValues.put("_duration",duration);
        db.update(DBHelper.CLOCK_TABLE_NAME,contentValues,where,null);
        setAlarm(id,duration);
    }

    private void setAlarm(int id,String duration){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,editHour);
        cal.set(Calendar.MINUTE, editMinute);
        cal.set(Calendar.SECOND,1);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("msg", "alarm");
        intent.putExtra("duration",duration);

        PendingIntent pi = PendingIntent.getBroadcast(this,id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);

        setResult(MainActivity.requestCodeClock,getIntent());
        ClockActivity.this.finish();
    }

    private void deleteData(){
        new AlertDialog.Builder(ClockActivity.this)
                .setTitle(R.string.alert_delete_clock)
                .setMessage(R.string.alert_delete_clock_info)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String where = "_id = "+id;
                        db.delete(DBHelper.CLOCK_TABLE_NAME,where,null);
                        Intent intent = new Intent(ClockActivity.this, AlarmReceiver.class);
                        PendingIntent pi = PendingIntent.getBroadcast(ClockActivity.this,id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        am.cancel(pi);
                        pi = null;
                        am = null;
                        setResult(MainActivity.requestCodeClock,getIntent());
                        ClockActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no,null)
                .show();
    }
    //TODO alarm management
}
