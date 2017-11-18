package dee.wallet;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

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
    private RecyclerView recyclerView;
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

        ArrayList<String> itemsData = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            itemsData.add("Fragment " + getArguments().getInt("index", -1) + " / Item " + i);
        }

        WalletAdapter adapter = new WalletAdapter(itemsData);
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

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        radioGroupType.setOnCheckedChangeListener(onCheckedChangeListener);
        radioButtonIncome.setChecked(true);
        LoadSpinner(0);

    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.radioButton_income:
                    Log.d("type","income");
                    LoadSpinner(0);
                    break;
                case R.id.radioButton_expense:
                    LoadSpinner(1);
                    Log.d("type","expense");
                    break;
            }
        }
    };

    /***
     *
     * @param index 0=income 1=expense
     */
    private void LoadSpinner(int index){
        String[] categoryList = new String[] {"Activity","School","Lunch","Breakfast","Dinner"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,categoryList);
        spinnerCategory.setAdapter(adapter);
    }

    private void initWalletHistory(View view){

    }

    private void initWalletSetting(View view){

    }

    private void openDB(){
        dbHelper = new DBHelper(getContext());
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
