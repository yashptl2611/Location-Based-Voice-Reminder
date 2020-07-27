package com.example.LocationReminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    FusedLocationProviderClient client;
    TextView text;
    Switch aSwitch;
    static int REQUEST_CODE_FINE_LOCATION = 1234;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ArrayList<pojo> arrayList;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Location Reminder");
        Toolbar toolbar = findViewById(R.id.toolbar);
        aSwitch = findViewById(R.id.opengps);
        recyclerView = findViewById(R.id.list);
        text = findViewById(R.id.text);
        setSupportActionBar(toolbar);

        LinearLayoutManager lp=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);//linear
        recyclerView.setLayoutManager(lp);
//        pojo n=new pojo();
        pd=new ProgressDialog(getApplicationContext());
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setMessage("Please wait....");
        arrayList = new ArrayList<>();



        //getdatafromurl();
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,second.class);
                startActivity(intent);
            }
        });
        aSwitch.setOnCheckedChangeListener(this);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "permission granted.. ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "permission not granted.. ", Toast.LENGTH_SHORT).show();


        }
        Toast.makeText(this, "First ON the switch to allow location permission", Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(commonUtils.baseString+"fetch.php",new RequestParams(), new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getString("responce").equals("sucess")) {
                        JSONArray ja = response.getJSONArray("data");
                        for(int i=0;i<ja.length();i++)
                        {
                            JSONObject jo = ja.getJSONObject(i);
                            pojo bp = new pojo();
                            bp.setRemind(jo.getString("remind"));
                            bp.setLati(jo.getString("lati"));
                            bp.setLongi(jo.getString("longi"));
                            arrayList.add(bp);



                        }
                    }
                    else {
                        text.setText("You don't have any tasks. Click the  + \\n button to add new Tasks.");
                    }
                    recyclerView.setAdapter(new CustomAdapter(arrayList));

                } catch (JSONException e){

                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (aSwitch.isChecked()){
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                //textView.setText("permission not granted");
                //permission not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    new AlertDialog.Builder(MainActivity.this).setMessage("We need permission for fine location").setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_FINE_LOCATION);

                        }
                    }).show();

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_FINE_LOCATION);
                }
            } else {
                //permission granted
                Toast.makeText(this, "permission accepted", Toast.LENGTH_SHORT).show();

            }


        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FINE_LOCATION){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //permission granted
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            } else {
                //permission not granted
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    // This block means the permission is denied permantenly
                    new AlertDialog.Builder(MainActivity.this).setMessage("You Have permanently denied this permission, go to settings to enable this permission").setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gottoapplicationsetting();
                        }
                    }).setNegativeButton("Cancel", null).setCancelable(false).show();
                } else {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void gottoapplicationsetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        Uri uri= Uri.fromParts("package",this.getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void askLocationpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

            }
        }
    }

    private void getCurrentLocation() {
        // Initailze task location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // when success
                if (location != null)
                {
                    //syn map
//                    supportMa
                }
            }
        });
    }

    private class CustomAdapter extends RecyclerView.Adapter<MyViewholderclass>
    {
        ArrayList<pojo> arrayList;
        public CustomAdapter(ArrayList<pojo> arrayList) {
            this.arrayList=arrayList;
        }
        @NonNull
        @Override
        public MyViewholderclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewholderclass(getLayoutInflater().inflate(R.layout.fetch_data,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewholderclass holder, final int position) {

            pojo np=arrayList.get(position);
            holder.remind.setText(np.getRemind());
            holder.lati.setText(np.getLati());
            holder.longi.setText(np.getLongi());


//            holder.product_cardViewid.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //int pos = 0;
//                    //pos =position +1;
//                    //Toast.makeText(getActivity(), ""+pos, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(),Seller.class);
//                    intent.putExtra("category_select_id",np.getId_buy());
//                    startActivity(intent);
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }


    private class MyViewholderclass extends RecyclerView.ViewHolder {
        CardView cardViewid;

        TextView remind,lati,longi;
        public MyViewholderclass(@NonNull View itemView) {
            super(itemView);
            remind = itemView.findViewById(R.id.remind);
            lati = itemView.findViewById(R.id.lati);
            longi = itemView.findViewById(R.id.longi);
        }
    }

}
