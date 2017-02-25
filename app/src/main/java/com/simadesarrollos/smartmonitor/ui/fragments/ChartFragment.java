package com.simadesarrollos.smartmonitor.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.simadesarrollos.smartmonitor.R;
import com.simadesarrollos.smartmonitor.custom.HourAxisValueFormatter;
import com.simadesarrollos.smartmonitor.custom.HourMarkerView;
import com.simadesarrollos.smartmonitor.models.GlobalData;
import com.simadesarrollos.smartmonitor.models.LogModel;
import com.simadesarrollos.smartmonitor.tools.Constants;
import com.simadesarrollos.smartmonitor.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by C POLANIA on 16/02/2017.
 */

public class ChartFragment extends Fragment{

    RequestQueue requestQueue;
    private LineChart mChartNd;
    private TextView tvX, tvY;
    long referenceTimestamp = 0;
    DecimalFormat mFormat= new DecimalFormat("00");
    String fecha = "";
    private LogModel[] logs;
    private Gson gson = new Gson();
    private static final String TAG = FirstMapFragment.class.getSimpleName();

    ArrayList<String> horaArr=new ArrayList<String>();
    ArrayList<String> Nivel1Arr=new ArrayList<String>();
    ArrayList<Entry> Nivel1Entry=new ArrayList<Entry>();

    private TextView Output;
    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;

    View view;
    static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chart, container, false);
        context = getActivity().getApplicationContext();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //No permitir que rote la pantalla
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setHasOptionsMenu(true);

        Output = (TextView) view.findViewById(R.id.Output);
        mChartNd = (LineChart) view.findViewById(R.id.chartNd);

        // no description text
        Description desc = new Description();
        desc.setText("");
        mChartNd.setDescription(desc);
        mChartNd.setNoDataText("La fecha seleccionada no tiene datos almacenados");

        // enable touch gestures
        mChartNd.setTouchEnabled(true);

        // enable scaling and dragging
        mChartNd.setDragEnabled(true);
        mChartNd.setScaleEnabled(true);
        // mChartNd.setScaleXEnabled(true);
        // mChartNd.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChartNd.setPinchZoom(true);

        // set an alternative background color
        // mChartNd.setBackgroundColor(Color.GRAY);

        // Get current date by calendar
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        fecha = year + "-" + mFormat.format(Double.valueOf(month+1)) + "-" + day;
        // Show current date
        Output.setText(fecha);

        //Trae datos del web api
        cargarAdaptador2();
        //Pasa los valores de los array del web api al array<Entry>
        convertData2();


        // x-axis limit line
        LimitLine llYAxis = new LimitLine(100f, "CHO Recomendados");
        llYAxis.setLineWidth(4f);
        llYAxis.enableDashedLine(10f, 10f, 0f);
        llYAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llYAxis.setTextSize(10f);

        YAxis rightAxis = mChartNd.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChartNd.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.addLimitLine(llYAxis);

        XAxis xAxis = mChartNd.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(86400);  //Rango eje X 86400 segundos (24 h)

        //Formatea eje x en HH:mm
        IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        xAxis.setValueFormatter(xAxisFormatter);
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        //Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        mChartNd.getAxisRight().setEnabled(false);

        //mChartNd.getViewPortHandler().setMaximumScaleY(2f);
        //mChartNd.getViewPortHandler().setMaximumScaleX(2f);

        mChartNd.animateY(1500, Easing.EasingOption.EaseInCubic);

        // get the legend (only possible after setting data)
        Legend l = mChartNd.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChartNd.invalidate();

        // listener for selecting and drawing
        mChartNd.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mChartNd.setHighlightPerTapEnabled(true);
                HourMarkerView myMarkerView= new HourMarkerView(context, R.layout.custom_marker_view, referenceTimestamp);
                mChartNd.setMarkerView(myMarkerView);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        mChartNd.setData(getDataLines2());
        mChartNd.invalidate();

        return view;
    }




    /**
     * Carga el array con los registros obtenidas
     * en la respuesta
     */
    private void cargarAdaptador2() {
        requestQueue = Volley.newRequestQueue(context);

        // Petición GET
        VolleySingleton.getInstance(context)
                .addToRequestQueue(new JsonObjectRequest(Request.Method.GET,
                                //*****************HARDCODE************
                                Constants.GET_BY_ID_DATE + "?id_cliente=10&fecha=" + fecha, null,
                                //JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Constants.GET_BY_ID_DATE + "?id_cliente=" + "10" + "&fecha=" + "2017-01-17", null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        procesarRespuesta2(response);
                                        setGlobales();
                                        //Dibujar polilineas con info del Json
                                        if(logs!=null)
                                            drawPlotLines2();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }
                        )
                );
        //requestQueue.add(jor);
    }

    /**
     * Interpreta los resultados de la respuesta y así
     * realizar las operaciones correspondientes
     *
     * @param response Objeto Json con la respuesta
     */
    private void procesarRespuesta2(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");

            switch (estado) {
                case "1": // EXITO
                    // Obtener array "metas" Json
                    JSONArray mensaje = response.getJSONArray("datos");
                    // Parsear con Gson
                    logs = gson.fromJson(mensaje.toString(), LogModel[].class);
                    // Inicializar adaptador
                    //adapter = new LogAdapter(Arrays.asList(logs), getActivity());
                    // Setear adaptador a la lista
                    //lista.setAdapter(adapter);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(context, mensaje2, Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setGlobales(){

        final LogModel logVariable = (LogModel) context;

        // Adding points to ArrayList
        for (LogModel logarr:logs) {
            logVariable.setFecha(logarr.getFecha());
            logVariable.setNivel1(logarr.getNivel1());
        }

    }


    protected void drawPlotLines2() {

        horaArr.clear();
        Nivel1Arr.clear();
        Nivel1Entry.clear();

        // Adding points to ArrayList
        for (LogModel logarr:logs) {
            horaArr.add(logarr.getFecha());
            Nivel1Arr.add(logarr.getNivel1());
        }
    }

    private void convertData2(){

        float[] arrayHora = new float[horaArr.size()];
        float[] arrayCho = new float[horaArr.size()];

        //pasa las horas a int y los arrayList leidos de la BD a array
        for (int i = 0; i < horaArr.size(); ++i) {
            String time = horaArr.get(i); //hh:mm:ss
            String[] units = time.split(":"); //will break the string up into an array
            int hours = Integer.parseInt(units[0]); //first element
            int minutes = Integer.parseInt(units[1]); //second element
            int seconds = Integer.parseInt(units[2]);
            int duration = 3600 * hours + 60 * minutes + seconds; //add up our values
            arrayHora[i] = duration;
            arrayCho[i] = Float.parseFloat(Nivel1Arr.get(i));
        }

        //Pasa los array a los Entry
        for (int i=0; i< horaArr.size(); ++i) {
            Nivel1Entry.add(new Entry(arrayHora[i], arrayCho[i]));
        }
    }

    private LineData getDataLines2() {

        LineData d = new LineData();

        if(Nivel1Entry.size()==0){
            Nivel1Entry.add(new Entry(0, 0));
        }

        LineDataSet set = new LineDataSet(Nivel1Entry, "Tanque 1");
        set.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        set.setLineWidth(2.5f);
        //set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        //set.setFillColor(Color.rgb(240, 238, 70));
        //set.setDrawCubic(true);
        //set.setDrawValues(true);
        set.setValueTextSize(12f);
        //set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setCircleColor(Color.BLACK);
        set.setDrawCircleHole(true);
        set.setDrawFilled(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // set the line to be drawn like this "- - - - - -"
        //set.enableDashedLine(10f, 5f, 0f);
        //set.enableDashedHighlightLine(10f, 5f, 0f);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_blue);
            set.setFillDrawable(drawable);
        }
        else {
            set.setFillColor(Color.BLUE);
        }

        d.addDataSet(set);

        return d;
    }

    //Rutina datePicker
    protected void showDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                DatePickerDialog newFragment = new DatePickerDialog(getActivity(), pickerListener, year, month, day);
                newFragment.show();
        }
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            Output.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            cargarAdaptador2();
            convertData2();

            mChartNd.setData(getDataLines2());
            mChartNd.invalidate();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chart, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionCalendar: {
                showDialog(DATE_PICKER_ID);
                break;
            }
            /*case R.id.actionToggleValues: {
                List<ILineDataSet> sets = mChartNd.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                mChartNd.invalidate();
                break;
            }
            case R.id.actionGridAxis: {
                IDataSet set = mChartNd.getData().getDataSets().get(0);
                if(!mChartNd.getXAxis().isDrawGridLinesEnabled()){
                    mChartNd.getAxisLeft().setDrawGridLines(true);
                    mChartNd.getXAxis().setDrawGridLines(true);
                }else{
                    mChartNd.getAxisLeft().setDrawGridLines(false);
                    mChartNd.getXAxis().setDrawGridLines(false);
                }
                mChartNd.invalidate();
                break;
            }
            case R.id.actionToggleFilled: {

                List<ILineDataSet> sets = mChartNd.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                mChartNd.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = mChartNd.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                mChartNd.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                List<ILineDataSet> sets = mChartNd.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.STEPPED
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.STEPPED);
                }
                mChartNd.invalidate();
                break;
            }
            case R.id.actionToggleHorizontalCubic: {
                List<ILineDataSet> sets = mChartNd.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            :  LineDataSet.Mode.HORIZONTAL_BEZIER);
                }
                mChartNd.invalidate();
                break;
            }
            case R.id.animateY: {
                mChartNd.animateY(3000, Easing.EasingOption.EaseInCubic);
                break;
            }
            case R.id.action_settings: {
                getActivity().finish();
                Intent i = new Intent(context, Act_configuracion_nd.class);
                startActivity(i);
                break;
            }
            case R.id.action_picker: {
                showDialog(DATE_PICKER_ID);
                break;
            }*/
        }
        return true;
    }

}
