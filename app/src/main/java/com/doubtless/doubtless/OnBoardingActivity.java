package com.doubtless.doubtless;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OnBoardingActivity extends AppCompatActivity {

    String[] items =  {"1st","2nd","3rd","4th"};
    String[] branch =  {"BBA","BCA","B.Tech"};
    String[] department =  {"CSE","IT","ECE","EEE"};
    String[] purpose =  {"I am Junior, came here to ask Doubt","I am a senior, came here to answer Doubt"};
    AutoCompleteTextView autoCompleteTxtYear;
    AutoCompleteTextView autoCompleteTxtBranch;
    AutoCompleteTextView autoCompleteTxtDepartment;
    AutoCompleteTextView autoCompleteTxtPurpose;
    ArrayAdapter<String> adapterItemsYear;
    ArrayAdapter<String> adapterItemsBranch;
    ArrayAdapter<String> adapterItemsDepartment;
    ArrayAdapter<String> adapterItemsPurpose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        autoCompleteTxtYear = findViewById(R.id.auto_complete_txt_year);

        adapterItemsYear = new ArrayAdapter<String>(this,R.layout.list_item,items);
        autoCompleteTxtYear.setAdapter(adapterItemsYear);

        autoCompleteTxtYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item, Toast.LENGTH_SHORT).show();

            }

        });
        autoCompleteTxtBranch = findViewById(R.id.auto_complete_txt_branch);

        adapterItemsBranch = new ArrayAdapter<String>(this,R.layout.list_item,branch);
        autoCompleteTxtBranch.setAdapter(adapterItemsBranch);

        autoCompleteTxtBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item, Toast.LENGTH_SHORT).show();

            }

        });
        autoCompleteTxtDepartment = findViewById(R.id.auto_complete_txt_department);

        adapterItemsDepartment = new ArrayAdapter<String>(this,R.layout.list_item,department);
        autoCompleteTxtDepartment.setAdapter(adapterItemsDepartment);

        autoCompleteTxtDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item, Toast.LENGTH_SHORT).show();

            }

        });

        autoCompleteTxtPurpose = findViewById(R.id.auto_complete_txt_purpose);

        adapterItemsPurpose = new ArrayAdapter<String>(this,R.layout.list_item,purpose);
        autoCompleteTxtPurpose.setAdapter(adapterItemsPurpose);

        autoCompleteTxtPurpose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item, Toast.LENGTH_SHORT).show();

            }

        });

    }
}
