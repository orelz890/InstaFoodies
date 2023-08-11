package Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instafoodies.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Getlocation extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView address, city, country, lattitude, longitude;
    Button loc_btn;
    public static final int REQUEST_CODR = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlocation);

        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        country = findViewById(R.id.Country);
        lattitude = findViewById(R.id.Lattitude);
        longitude = findViewById(R.id.Longitude);
        loc_btn = findViewById(R.id.loc_btn);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        loc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindLocation();
            }
        });
    }

    private void FindLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(Getlocation.this, Locale.getDefault());
                        List<Address> addressList = null;
                        try {
                            addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            lattitude.setText("Latitude : " + addressList.get(0).getLatitude());
                            longitude.setText("Longitude : " + addressList.get(0).getLongitude());
                            city.setText("City : " + addressList.get(0).getLocality());
                            longitude.setText("Country : " + addressList.get(0).getCountryName());
                            address.setText("Address : " + addressList.get(0).getAddressLine(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        else{
            Askforpermission();

        }
    }

    private void Askforpermission() {
        ActivityCompat.requestPermissions(Getlocation.this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        },REQUEST_CODR);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODR){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                FindLocation();
            }
            else{
                Toast.makeText(Getlocation.this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}