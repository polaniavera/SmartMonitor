package com.simadesarrollos.smartmonitor.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.simadesarrollos.smartmonitor.R;
import com.simadesarrollos.smartmonitor.models.GlobalData;
import com.simadesarrollos.smartmonitor.models.LogModel;
import com.simadesarrollos.smartmonitor.tools.Constants;
import com.simadesarrollos.smartmonitor.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Muestra el mapa
 */
public class FirstMapFragment extends Fragment
        implements AdapterView.OnItemSelectedListener{
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

    MapView mMapView;
    View view;
    static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_first_map, container, false);
        context = getActivity().getApplicationContext();

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //No permitir que rote la pantalla
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setHasOptionsMenu(true);

        Output = (TextView) view.findViewById(R.id.Output);
        mMapTypeSelector = (Spinner) view.findViewById(R.id.map_type_selector);
        mMapTypeSelector.setOnItemSelectedListener(this);

        // Get current date by calendar
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        fecha = year + "-" + mFormat.format(Double.valueOf(month+1)) + "-" + day;
        // Show current date
        Output.setText(fecha);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                //Iniciar GET Asincrono con volley
                //cargarAdaptador();
                drawPlotLines();
            }
        });

        return view;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mMap.setMapType(mMapTypes[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }*/

    /**
     * Carga el array con los registros obtenidas
     * en la respuesta
     */
    /*private void cargarAdaptador() {
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
   /* private void procesarRespuesta(JSONObject response) {
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
    }*/

    protected void drawPlotLines() {

        //OBTENER VARIABLE GLOBAL
        final LogModel logVariable = (LogModel) context;
        String fecha = logVariable.getFecha();



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

        LatLng inicioRuta = new LatLng(coordList.get(0).latitude, coordList.get(0).longitude);
        LatLng finalRuta = new LatLng(coordList.get(coordList.size()-1).latitude, coordList.get(coordList.size()-1).longitude);

        // Mover cámara
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(inicioRuta));
        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(inicioRuta).title("Marker Title")
                .snippet("Marker Description").icon(BitmapDescriptorFactory.defaultMarker()));
        mMap.addMarker(new MarkerOptions().position(finalRuta).title("Marker Title")
                .snippet("Marker Description").icon(BitmapDescriptorFactory.defaultMarker()));

        // For showing a move to my location button
        //mMap.setMyLocationEnabled(true);

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(inicioRuta).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
            if(polyline!=null){
                coordList.clear();
                polyline.remove();
                logs = null;
            }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}