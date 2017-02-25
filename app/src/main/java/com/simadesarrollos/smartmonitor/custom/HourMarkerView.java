package com.simadesarrollos.smartmonitor.custom;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.simadesarrollos.smartmonitor.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by C POLANIA on 26/11/2016.
 */

public class HourMarkerView extends MarkerView {

    private TextView tvContent;
    private long referenceTimestamp;  // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;

    public HourMarkerView (Context context, int layoutResource, long referenceTimestamp) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        this.mDate = new Date();
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long currentTimestamp = (int)e.getX() + referenceTimestamp;

        if (e instanceof BarEntry) {
            BarEntry ce = (BarEntry) e;
            tvContent.setText("Alimenticio: " + ce.getYVals()[0] + System.getProperty ("line.separator") + "Corrección: " + ce.getYVals()[1]
                    + System.getProperty ("line.separator") + "Total: " + ce.getPositiveSum() + System.getProperty ("line.separator") + " vs "
                    + System.getProperty ("line.separator") + getHour(currentTimestamp));
        } else {
            tvContent.setText(e.getY() + " vs " + getHour(currentTimestamp)); // set the entry-value as the display text
            //tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }


        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        //return -(getWidth() / 2);
        return 0;
    }

    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        //return -getHeight();
        return 0;
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight()-5);
        }

        return mOffset;
    }

    public MPPointF getOffsetForDrawingAtPos(float posX, float posY) {
        return null;
    }

    public String getHour(long time){

        int hours = (int) time / 3600;
        int remainder = (int) time - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        mDate.setHours(hours);
        mDate.setMinutes(mins);
        mDate.setSeconds(secs);

        return mDataFormat.format(mDate);
    }
}