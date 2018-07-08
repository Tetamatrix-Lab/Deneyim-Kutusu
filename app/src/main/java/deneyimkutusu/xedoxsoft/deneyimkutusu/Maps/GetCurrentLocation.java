package deneyimkutusu.xedoxsoft.deneyimkutusu.Maps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;
import deneyimkutusu.xedoxsoft.deneyimkutusu.R;

public class GetCurrentLocation extends AppCompatActivity implements LocationListener {

    Button getLocationBtn;
    TextView locationText;
    String enlem;
    String boylam;
    String ulke;
    LocationManager locationManager;
    TextView enlemtext;
    TextView boylamtext;
    TextView ulketext;
    TextView sehirtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_get_current_location);

        getLocationBtn = (Button) findViewById(R.id.getLocationBtn);
        locationText = (TextView) findViewById(R.id.locationText);
        enlemtext = (TextView) findViewById(R.id.textView22);
        boylamtext = (TextView) findViewById(R.id.textView21);
        ulketext = (TextView) findViewById(R.id.textView20);
        sehirtext = (TextView) findViewById(R.id.textView23);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        enlem = String.valueOf(location.getLatitude());
        boylam = String.valueOf(location.getLongitude());
        enlemtext.setText(enlem);
        boylamtext.setText(boylam);

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            //Api kullanmadan direk bulunduğun ülkeyi çekme
            //ulke= getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
            ulketext.setText(addresses.get(0).getCountryName());
            sehirtext.setText(addresses.get(0).getAdminArea());
            //sehirtext.setText(addresses.get(0).getAddressLine(0));
            locationText.setText(locationText.getText() + "\n" + addresses.get(0).getAddressLine(0) + ", " +
                    addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));
        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(GetCurrentLocation.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}