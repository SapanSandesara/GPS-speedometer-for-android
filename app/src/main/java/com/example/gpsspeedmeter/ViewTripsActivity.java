package com.example.gpsspeedmeter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ViewTripsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_trips);

        LinearLayout ll = findViewById(R.id.linear);
        File file = new File(this.getExternalFilesDir(null), "temp5.txt");



        try {

             InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(isr);
            ArrayList<String> strs = new ArrayList<String>();
            while(true){
                String str = br.readLine();
                if(str == null){
                    break;
                }
                else{
//                    TextView tv = new TextView(this);
//                    tv.setVisibility(View.VISIBLE);
//                    tv.setText(str);
//                    tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT));
//                    ll.addView(tv);
                    strs.add(str);
                }
            }
            ListView list = findViewById(R.id.list);
            ArrayAdapter<String> arrad;
            arrad
                    = new ArrayAdapter<String>(
                    this,
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    strs);

            list.setAdapter(arrad);

            br.close();
            isr.close();





        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
