package com.example.memoryslide;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatteryReduceActivity extends AppCompatActivity {//implements ListViewAdapter.ListBtnClickListener {


    Handler setHandler = new Handler();
    TextView rdText;
    private LineChart lineChart;




    public boolean loadItemsFromDB(ArrayList<ListViewItem> list) {
        ListViewItem item ;
        int i ;

        if (list == null) {
            list = new ArrayList<ListViewItem>() ;
        }

        // 아이템 생성.
        item = new ListViewItem() ;
        item.setIcon(ContextCompat.getDrawable(this, R.drawable.toggle_on)) ;
        item.setText("시스템 화면 밝기 조정");
        list.add(item) ;

        item = new ListViewItem() ;
        item.setIcon(ContextCompat.getDrawable(this, R.drawable.toggle_on)) ;
        item.setText("Wifi 기능 관리") ; //  sdk29부터 불가능...
        list.add(item) ;

        item = new ListViewItem() ;
        item.setIcon(ContextCompat.getDrawable(this, R.drawable.toggle_on)) ;
        item.setText("GPS 기능 관리") ;
        list.add(item) ;

        return true ;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_reduce);

        ListView listview ;
        ListViewAdapter adapter;
        ArrayList<ListViewItem> items = new ArrayList<ListViewItem>() ;

        // items 로드.
        loadItemsFromDB(items) ;

        //텍스트 설정
        rdText = new TextView(this);
        rdText = findViewById(R.id.rdText);
        rdText.setText("로딩 중......");

        //현재 상태 확인, 셋
        Thread setThread = new Thread("setThread"){
            public void run(){
                while(true){
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    setHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final WifiManager one = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            final LocationManager two = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                            int brValue = android.provider.Settings.System.getInt(getContentResolver(), "screen_brightness", 0);
                            boolean wifiOn = one.isWifiEnabled();
                            boolean locationOn = two.isLocationEnabled();
                            setTextByOn(brValue, wifiOn, locationOn);
                        }
                    });
                }
            }
        };
        setThread.start();

        // Adapter 생성
        adapter = new ListViewAdapter(this, R.layout.listview_item, items) ;
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        lineChart = (LineChart)findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<>();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        int maxnum = pref.getInt("maxnum", 0);

        //샘플 엔트리 1
        entries.add(new Entry(pref.getInt(Integer.toString(maxnum-10),0),
                pref.getInt("remain"+Integer.toString(maxnum-10),0)));

        LineDataSet lineDataSet = new LineDataSet(entries, "속성명1");
        lineDataSet.setLineWidth(2);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }



    public void setTextByOn(int brightness, boolean wifiOn, boolean locationOn){
        String wString, cString;

        if(wifiOn){
            wString = "ON";
        }
        else wString = "OFF";
        if(locationOn){
            cString = "ON";
        }
        else cString = "OFF";

        rdText.setText ("밝기 :  "+brightness+
                "\nWIFI : "+wString+
                "\nGPS : "+cString);
    }
}
