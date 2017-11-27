package dee.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;


//TODO change layout
public class ClockActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private LinearLayout linearLayout;
    private TextView textWeekday;
    private Button btnAddClock;
    private int id;
    private boolean functionCode;
    private int editHour;
    private int editMinute;
    private String[] items = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    private boolean[] itemsChecked;
    private ArrayList<Integer> durationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Bundle bundle = getIntent().getExtras();
        functionCode = bundle.getBoolean("functionCode");
        if(functionCode){ //edit
            id = bundle.getInt("id");
            editHour = bundle.getInt("hour");
            editMinute = bundle.getInt("minute");
            durationList = new ArrayList<>();
            durationList.clear();
            durationList.addAll(bundle.getIntegerArrayList("duration"));
        }
        else{//add
            editHour = 12;
            editMinute = 0;
            durationList = new ArrayList<>();
            for(int i=0;i<7;i++){
                durationList.add(1);
            }
        }

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        linearLayout = (LinearLayout) findViewById(R.id.linearClock);
        textWeekday = (TextView) findViewById(R.id.textWeekday);
        btnAddClock = (Button) findViewById(R.id.btnAddClock);

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

        btnAddClock.setText("Finish");
        btnAddClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("time",String.valueOf(editHour+":"+editMinute));
                String duration = "";
                for(int i=0;i<durationList.size();i++){
                    duration += (durationList.get(i)==1)?'1':'0';
                }
                //return value
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                if(functionCode){
                    bundle.putInt("id",id);
                }
                bundle.putBoolean("functionCode",functionCode);
                bundle.putInt("hour",editHour);
                bundle.putInt("minute",editMinute);
                bundle.putString("duration",duration);
                intent.putExtras(bundle);
                setResult(MainActivity.requestCodeClock,intent);
                ClockActivity.this.finish();
            }
        });


    }
}
