package com.example.senensig.admin;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class ValueFormatter implements IAxisValueFormatter {
    //private final String[] mLabels;
    final ArrayList<String> mLabels ;

    //public ValueFormatter(String[] labels) {mLabels = labels;}

    public ValueFormatter(ArrayList<String> labels) {mLabels = labels;}

    @Override
    public String getFormattedValue(float value, AxisBase axis) {return mLabels.get((int) value); }
    //public String getFormattedValue(float value, AxisBase axis) {return mLabels[(int) value]; }
}