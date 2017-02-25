package com.simadesarrollos.smartmonitor.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.simadesarrollos.smartmonitor.ui.fragments.FirstMapFragment;
import com.simadesarrollos.smartmonitor.R;
import com.simadesarrollos.smartmonitor.models.LogModel;
import com.simadesarrollos.smartmonitor.tools.Constants;
import com.simadesarrollos.smartmonitor.ui.fragments.FirstMapFragment_old;
import com.simadesarrollos.smartmonitor.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by C POLANIA on 18/01/2017.
 */

public class FirstMapActivity extends AppCompatActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    RequestQueue requestQueue;
    private static final String TAG = FirstMapFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private LogModel[] logs;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private Spinner mMapTypeSelector;
    private int mMapTypes[] = {
            //GoogleMap.MAP_TYPE_NONE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN };

    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    private TextView Output;
    DecimalFormat mFormat= new DecimalFormat("00");
    String fecha = "";
    ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    PolylineOptions polylineOptions = new PolylineOptions();
    Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_map);

        //No permitir que rote la pantalla
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Output = (TextView) findViewById(R.id.Output);
        mMapTypeSelector = (Spinner) findViewById(R.id.map_type_selector);
        mMapTypeSelector.setOnItemSelectedListener(this);
        mMapFragment = (FirstMapFragment_old) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        // Get current date by calendar
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        //mFormat.setRoundingMode(RoundingMode.DOWN);
        fecha = year + "-" + mFormat.format(Double.valueOf(month+1)) + "-" + day;
        // Show current date
        Output.setText(fecha);

        //Iniciar GET Asincrono con volley
        cargarAdaptador();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Ejemplo: Delimitar a Sudamérica con un rectángulo
        /*PolylineOptions sudamericaRect = new PolylineOptions()
                .add(new LatLng(10.15524, -73.956145)) // P1
                .add(new LatLng(10.152023, -73.954407)) // P2
                .add(new LatLng(10.14367, -73.950865)) // P3
                .add(new LatLng(10.062942, -73.92462))  // P4
                .add(new LatLng(10.053942, -73.92147)) // P1
                .add(new LatLng(10.001432, -73.900365))
                .add(new LatLng(9.984065, -73.893385))
                .add(new LatLng(9.98055, -73.891215))
                .add(new LatLng(9.980548, -73.891212))
                .add(new LatLng(9.98055, -73.891212))
                .add(new LatLng(9.979825, -73.890713))
                .add(new LatLng(9.978078, -73.88959))
                .add(new LatLng(9.976933, -73.888842))
                .add(new LatLng(9.97547, -73.887867))
                .add(new LatLng(9.975287, -73.887747))
                .add(new LatLng(9.974297, -73.887085))
                .add(new LatLng(9.973513, -73.886582))
                .add(new LatLng(9.974417, -73.884467))
                .add(new LatLng(9.986533, -73.871923))
                .color(Color.parseColor("#f44336"));  */  // Rojo 500

        // Instancia para posteriores usos
        //Polyline polyline = mMap.addPolyline(sudamericaRect);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mMap.setMapType(mMapTypes[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /**
     * Carga el array con los registros obtenidas
     * en la respuesta
     */
    public void cargarAdaptador() {
        requestQueue = Volley.newRequestQueue(this);

        // Petición GET
        VolleySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(new JsonObjectRequest(Request.Method.GET,
                    //*****************HARDCODE************
                Constants.GET_BY_ID_DATE + "?id_cliente=10&fecha=" + fecha, null,
        //JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Constants.GET_BY_ID_DATE + "?id_cliente=" + "10" + "&fecha=" + "2017-01-17", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta Json
                        procesarRespuesta(response);

                        //Dibujar polilineas con info del Json
                        if(logs!=null)
                            drawPlotLines();
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
    private void procesarRespuesta(JSONObject response) {
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
                    Toast.makeText(getApplicationContext(), mensaje2, Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void drawPlotLines() {
        // Adding points to ArrayList
        for (LogModel logarr:logs) {
            coordList.add(new LatLng(Double.parseDouble(logarr.getLongitud()), Double.parseDouble(logarr.getLatitud())));
        }
        // Create polyline options with existing LatLng ArrayList
        polylineOptions.addAll(coordList);
        polylineOptions
                .width(5)
                .color(Color.RED);

        // Adding multiple points in map using polyline and arraylist
        polyline = mMap.addPolyline(polylineOptions);

        // Mover cámara
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(coordList.get(0).latitude, coordList.get(0).longitude)));
        // Adding markers
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(coordList.get(0).latitude, coordList.get(0).longitude)).icon(
                BitmapDescriptorFactory.defaultMarker()));
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(coordList.get(coordList.size()-1).latitude, coordList.get(coordList.size()-1).longitude)).icon(
                BitmapDescriptorFactory.defaultMarker()));
    }

    /*Seccion dataPicker*/
    //Rutina datePicker
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            if(polyline!=null){
                coordList.clear();
                polyline.remove();
                logs = null;            }

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            mFormat.setRoundingMode(RoundingMode.DOWN);
            fecha = year + "-" + mFormat.format(Double.valueOf(month+1)) + "-" + day;

            //Iniciar GET Asincrono con volley
            cargarAdaptador();

            // Show selected date
            //mFormat.format(Double.valueOf(month+1))
            //Output.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" "));
            Output.setText(fecha);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionCalendar: {
                showDialog(DATE_PICKER_ID);
                break;
            }
        }
        return true;
    }
}