package com.simadesarrollos.smartmonitor.ui.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.simadesarrollos.smartmonitor.ui.fragments.FirstMapFragment;
import com.simadesarrollos.smartmonitor.R;
import com.simadesarrollos.smartmonitor.custom.CustomVolleyRequestQueue;
import com.simadesarrollos.smartmonitor.models.LogModel;
import com.simadesarrollos.smartmonitor.tools.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by LENOVO on 20/01/2017.
 */

public class VolleyBlockingRequestActivity extends AppCompatActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener{

    public static final String REQUEST_TAG = "VolleyBlockingRequestActivity";
    private TextView mTextView;
    private Button mButton;
    private RequestQueue mQueue;

    /*
    Etiqueta de depuracion
     */
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
            GoogleMap.MAP_TYPE_TERRAIN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_map);

        //setContentView(R.layout.activity_volley_blocking_request);
        //mTextView = (TextView) findViewById(R.id.textView);
        mButton = (Button) findViewById(R.id.button);

        mMapTypeSelector = (Spinner) findViewById(R.id.map_type_selector);
        mMapTypeSelector.setOnItemSelectedListener(this);
        //mMapFragment = (FirstMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mMapFragment.getMapAsync(this);

        //startParsingTask();

        // Now auto clicking the button
        //mButton.performClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startParsingTask();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //startParsingTask();

        //ArrayList<LatLng> coordList = new ArrayList<LatLng>();

        // Adding points to ArrayList
        /*for (LogModel logarr:logs) {
            coordList.add(new LatLng(Double.parseDouble(logarr.getLatitud()), Double.parseDouble(logarr.getLongitud())));
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        // Create polyline options with existing LatLng ArrayList
        polylineOptions.addAll(coordList);
        polylineOptions
                .width(5)
                .color(Color.RED);

        // Adding multiple points in map using polyline and arraylist
        mMap.addPolyline(polylineOptions);

        // Instancia para posteriores usos
        //Polyline polyline = mMap.addPolyline(sudamericaRect);

        // Mover cámara
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(coordList.get(0).latitude, coordList.get(0).longitude)));
        */
    }

    public void startParsingTask() {
        Thread threadA = new Thread() {
            public void run() {
                ThreadB threadB = new ThreadB(getApplicationContext());
                JSONObject jsonObject = null;
                try {
                    jsonObject = threadB.execute().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                final JSONObject receivedJSONObject = jsonObject;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mTextView.setText("Response is: " + receivedJSONObject);
                        if (receivedJSONObject != null) {
                            try {
                                //mTextView.setText(mTextView.getText() + "\n\n" + "Estado: " + receivedJSONObject.getString("estado"));
                                String dataJson = receivedJSONObject.getString("estado");
                                // Procesar la respuesta Json
                                procesarRespuesta(receivedJSONObject);
                                drawPlotLines();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };
        threadA.start();
    }

    private class ThreadB extends AsyncTask<Void, Void, JSONObject> {
        private Context mContext;

        public ThreadB(Context ctx) {
            mContext = ctx;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
            mQueue = CustomVolleyRequestQueue.getInstance(mContext.getApplicationContext()).getRequestQueue();
            String url = Constants.GET_BY_ID_DATE + "?id_cliente=" + "10" + "&fecha=" + "2017-01-17";
            //mQueue.getCache().remove(url);
            //mQueue.getCache().clear();
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method
                    .GET, url,
                    new JSONObject(), futureRequest, futureRequest);
            jsonRequest.setTag(REQUEST_TAG);
            jsonRequest.setShouldCache(false);
            mQueue.add(jsonRequest);
            try {
                return futureRequest.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
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
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();
        PolylineOptions polylineOptions = new PolylineOptions();
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
        mMap.addPolyline(polylineOptions);

        // Mover cámara
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(coordList.get(0).latitude, coordList.get(0).longitude)));
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(coordList.get(0).latitude, coordList.get(0).longitude)).icon(
                BitmapDescriptorFactory.defaultMarker()));
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(coordList.get(coordList.size()-1).latitude, coordList.get(coordList.size()-1).longitude)).icon(
                BitmapDescriptorFactory.defaultMarker()));
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
}