package com.swapnildey.beashopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Range;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.swapnildey.beashopping.supportPackageAdapters.ProductsAdapter;
import com.swapnildey.beashopping.supportPackageApplication.ShopMetaData;
import com.swapnildey.beashopping.supportPackageDataModels.Product;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    private static String TAG = "MyActivity";
    private BeaconManager mBeaconManager;

    private RecyclerView recyclerView;
    private ArrayList<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"on Create");
        init();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProductsAdapter(this,products));

    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        if(products==null) products = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"on Resume");
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.bind(this);
    }

    public void onBeaconServiceConnect() {
        Log.d(TAG,"onBeaconServiceConnect");
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(TAG,beacons.size()+"");
        for (Beacon beacon : beacons) {
            if(beacon.getRssi()>-55){
                if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                    String id = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                    id=id.substring(id.indexOf('.')+1);
                    Log.d(TAG, "I see a beacon transmitting a url: " + id +
                            " approximately " + beacon.getDistance() + " meters away.");

                    boolean productExists = false;
                    for(Product product:products){
                        if(product.getId().equals(id)){
                            productExists = true;
                            break;
                        }
                    }
                    if(!productExists){
                        fetchAndSaveProduct(id);
                    }
                }
            }

        }
    }

    private void fetchAndSaveProduct(String id){


        String url = ShopMetaData.getHostUrl()+"info?id="+id;
        Log.d(TAG,"Sending request: "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                saveProduct(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                Log.d(TAG,error.getLocalizedMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void saveProduct(JSONObject response) {
        String productName = "", productOffer= "", productID="", productImg="";
        try {
            productName = response.getString("name");
            productOffer = response.getString("offer");
            productID = response.getString("id");
            productImg = response.getString("imageUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        products.add(new Product(productID,productName,productOffer,productImg));
        recyclerView.getAdapter().notifyDataSetChanged();
        showNotification(productName,productOffer);
    }

    private void showNotification(String productName, String productOffer){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("channel id","Beashop",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"channel id")
                .setContentTitle(productName)
                .setContentText(productOffer)
                .setSmallIcon(R.drawable.shopping)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        manager.notify((int)System.currentTimeMillis(),builder.build());
    }
}
