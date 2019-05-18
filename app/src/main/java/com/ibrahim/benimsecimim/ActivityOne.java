package com.ibrahim.benimsecimim;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityOne extends AppCompatActivity {
    private static String TAG = "MainActivity";


    PieChart pieChart;
    private long[] partileroy={0,0,0,0,0,0};
    private  float[] partilersonuc={0,0,0,0,0,0};
    private String[] partiler =
            {"AKP", "CHP", "SP", "DSP", "DP", "Diğer"};
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int b=0;
    long c=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        Log.d(TAG, "onCreate: starting to create chart");

        pieChart = (PieChart) findViewById(R.id.idPieChart);

        pieChart.setDescription("Seçim sonuç tahminleri");
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Seçim sonuç tahminleri");
        pieChart.setCenterTextSize(10);
        //pieChart.setDrawEntryLabels(true);
        //pieChart.setEntryLabelTextSize(20);
        //More options just check out the documentation!

        getData();


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("(sum): ");
                String oy = e.toString().substring(pos1 + 6);
                Log.d(TAG, "onValueSelect: " + oy); //oylar aynı olunca tek parti yazıyor




                for(int i = 0; i < partilersonuc.length; i++){

                    if(partilersonuc[i]==Float.parseFloat(oy)){
                        pos1 = i;
                        break;
                    }
                }
                String sonuc = partiler[pos1];
                Toast.makeText(ActivityOne.this,  sonuc + "  oy sayısı: " + partileroy[pos1] , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();


        for(int i = 0; i < partilersonuc.length; i++){
            yEntrys.add(new PieEntry(partilersonuc[i] , i));
        }

        for(int i = 1; i < partiler.length; i++){
            xEntrys.add(partiler[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Oy sayısı"+c);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    public  void getData(){

        DatabaseReference reference=database.getReference("Partiler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    long a=ds.getChildrenCount();
                    partilersonuc[b]=a;
                    partileroy[b]=a;
                    b++;
                    c=c+a;
                    if(b>=6){
                        b=0;
                    }
                }
                for(int i = 0; i < partilersonuc.length; i++){
                    float z= partilersonuc[i]*100;
                    float x=z/c;
                    partilersonuc[i]=x;
                }



                addDataSet();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
