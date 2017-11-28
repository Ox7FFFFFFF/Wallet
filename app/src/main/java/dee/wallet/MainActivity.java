package dee.wallet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.rackspira.kristiawan.rackmonthpicker.RackMonthPicker;
import com.rackspira.kristiawan.rackmonthpicker.listener.DateMonthDialogListener;
import com.rackspira.kristiawan.rackmonthpicker.listener.OnCancelMonthDialogListener;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Database
    DBHelper dbHelper = null;
    SQLiteDatabase db;

    public static int requestCodeClock = 1;
    public static int requestCodeRecord = 2;
    private WalletFragment currentFragment;
    private WalletViewPagerAdapter adapter;
    private AHBottomNavigationViewPager viewPager;
    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationAdapter navigationAdapter;
    private int[]tabColors;
    private int selectYear,selectMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        openDB();
        initDate();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void initDate(){
        final TextView textDate = (TextView) findViewById(R.id.text_date);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.toolbar_layout);
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        String monthString = new DateFormatSymbols(new Locale("en", "US")).getMonths()[mMonth];
        String month = mYear+" "+monthString;
        textDate.setText(month);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RackMonthPicker(MainActivity.this)
                        .setLocale(Locale.US)
                        .setColorTheme(R.color.colorForDateChoose)
                        .setPositiveButton(new DateMonthDialogListener() {
                            @Override
                            public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
//                                System.out.println(month);
//                                System.out.println(startDate);
//                                System.out.println(endDate);
//                                System.out.println(year);
//                                System.out.println(monthLabel);
                                selectYear = year;
                                selectMonth = month;
                                String monthString = new DateFormatSymbols(new Locale("en", "US")).getMonths()[month-1];
                                textDate.setText(String.valueOf(mYear+" "+monthString));
                            }
                        })
                        .setNegativeButton(new OnCancelMonthDialogListener() {
                            @Override
                            public void onCancel(AlertDialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }
    private void initUI(){
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

        tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setForceTint(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if(currentFragment == null){
                    currentFragment = adapter.getCurrentFragment();
                }

                if(wasSelected){
                    return true;
                }

                if(currentFragment!=null){
                    currentFragment.willBeHidden();
                }

                viewPager.setCurrentItem(position,false);

                if(currentFragment==null){
                    return true;
                }

                currentFragment = adapter.getCurrentFragment();
                currentFragment.willBeDisplayed();

                return true;
            }
        });

        viewPager.setOffscreenPageLimit(3);
        adapter = new WalletViewPagerAdapter(getSupportFragmentManager(),selectYear,selectMonth);
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == requestCodeClock){
            adapter.updateSettingFragment();
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(3);
        }
        else if(resultCode == requestCodeRecord){
            adapter.updateDetailFragment();
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
        }
    }

    private void openDB(){
        dbHelper = new DBHelper(MainActivity.this);
//        getContext().deleteDatabase(DBHelper.DATABASE_NAME);
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        dbHelper.close();
    }
}
