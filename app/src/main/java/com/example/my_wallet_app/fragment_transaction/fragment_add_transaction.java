package com.example.my_wallet_app.fragment_transaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.my_wallet_app.Budget.Budget;
import com.example.my_wallet_app.Database.MainDatabase;
import com.example.my_wallet_app.Database.dbDAO;
import com.example.my_wallet_app.R;
import com.example.my_wallet_app.Utilities;
import com.example.my_wallet_app.fragment_accounts.fragment_accounts;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class fragment_add_transaction extends DialogFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    View mView;

    EditText dateText;
    String account_0;
    String account_1;
    dbDAO dbDAO;
    MainDatabase db;
    String transactionType;
    AppCompatActivity activity;
    String positionString = null;

    DateTimeFormatter temp_day;
    DateTimeFormatter temp_month;
    DateTimeFormatter temp_year;

    int day;
    int month;
    int year;

    private String imageUriString = null;
    private EditText descriptionTIET;
    private final MutableLiveData<Bitmap> imageBitmap = new MutableLiveData<>();
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private RequestQueue requestQueue;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean requestingLocationUpdates = false;
    private boolean isNetworkConnected = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Snackbar snackbar;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static String OSM_REQUEST_TAG = "OSM_REQUEST";

    public fragment_add_transaction(AppCompatActivity activity) {
        this.activity = activity;
    }

    public fragment_add_transaction() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction_dialog, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity != null) {
            snackbar = Snackbar.make(activity.findViewById(R.id.fragmentContainerView),
                            "No Internet Available", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    });

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isNetworkConnected = true;
                    snackbar.dismiss();
                    if(requestingLocationUpdates) {
                        startLocationUpdates(activity);
                    }
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    isNetworkConnected = false;
                    snackbar.show();
                }
            };

            requestQueue = Volley.newRequestQueue(activity);
            requestPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                startLocationUpdates(activity);
                                Log.d("LAB-ADDFRAGMENT", "PERMISSION GRANTED");
                            } else {
                                Log.d("LAB-ADDFRAGMENT", "PERMISSION NOT GRANTED");
                                showDialog(activity);
                            }
                        }
                    });
            initializeLocation(activity);

            mView.findViewById(R.id.capture_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (takePicture.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });

            ImageView imageView = mView.findViewById(R.id.picture_displayed_imageview);

            this.imageBitmap.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
                @Override
                public void onChanged(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            });

            descriptionTIET = mView.findViewById(R.id.gpsValue);

            mView.findViewById(R.id.gps_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestingLocationUpdates = true;
                    registerNetworkCallback(activity);
                    startLocationUpdates(activity);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.temp_day = DateTimeFormatter.ofPattern("dd");
        this.temp_month = DateTimeFormatter.ofPattern("MM");
        this.temp_year = DateTimeFormatter.ofPattern("yyyy");

        LocalDateTime now = LocalDateTime.now().plusHours(2);

        this.day = Integer.parseInt(String.valueOf(temp_day.format(now)));
        this.month = Integer.parseInt(String.valueOf(temp_month.format(now)));
        this.year = Integer.parseInt(String.valueOf(temp_year.format(now)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        db = MainDatabase.getDb(getActivity().getApplicationContext());
        dbDAO = db.dbDAO();

        mView = inflater.inflate(R.layout.fragment_add_transaction_dialog, null);

        dateText = mView.findViewById(R.id.dateValue);
        dateText.setText(day + "/" + month + "/" + year);
        dateText.setOnClickListener(this);

        AutoCompleteTextView accountChoose = mView.findViewById(R.id.autoCompleteTextView);
        AutoCompleteTextView toAccountChoose = mView.findViewById(R.id.toAccountAutoCompleteTextView);

        Button expenseButton = mView.findViewById(R.id.expense_button);
        Button incomeButton = mView.findViewById(R.id.income_button);
        Button transferButton = mView.findViewById(R.id.transfer_button);
        ConstraintLayout toAccountLayout = mView.findViewById(R.id.toAccountLayout);

        expenseButton.setOnClickListener(v -> {
            toAccountLayout.setVisibility(mView.GONE);
        });

        incomeButton.setOnClickListener(v -> {
            toAccountLayout.setVisibility(mView.GONE);
        });

        transferButton.setOnClickListener(v -> {
            toAccountLayout.setVisibility(mView.VISIBLE);

        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(dbDAO.getAccounts().size() > 0){
                    account_0 = dbDAO.getAccounts().get(0).Account_Name;
                }
                if(dbDAO.getAccounts().size() > 1){
                    account_1 = dbDAO.getAccounts().get(1).Account_Name;
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if(account_0 != null){
            accountChoose.setText(account_0);
        }
        if(account_1 != null){
            toAccountChoose.setText(account_1);
        }

        ArrayList<String> inputs = getAccounts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mView.getContext(), R.layout.account_menu_item, inputs);
        accountChoose.setAdapter(adapter);
        toAccountChoose.setAdapter(adapter);

        // -----------------------------------------------------------------------------------------

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Bitmap bitmap = imageBitmap.getValue();

                        try {
                            if (bitmap != null) {
                                imageUriString = String.valueOf(saveImage(bitmap, activity));
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        imageBitmap.setValue(null);

                        Button expense_button = mView.findViewById(R.id.expense_button);
                        Button income_button = mView.findViewById(R.id.income_button);
                        Button transfer_button = mView.findViewById(R.id.transfer_button);
                        MaterialButtonToggleGroup toggleGroup = mView.findViewById(R.id.toggleButton);
                        EditText amount = mView.findViewById(R.id.amountValue);
                        EditText position = mView.findViewById(R.id.gpsValue);

                        if(!position.getText().toString().equals("")){
                            positionString = position.getText().toString();
                        }

                        if(!amount.getText().toString().equals("") || Double.parseDouble(amount.getText().toString()) != 0) {
                            String amountParsed = String.valueOf(Double.parseDouble(amount.getText().toString()));

                            if (expense_button.getId() == toggleGroup.getCheckedButtonId()) {
                                transactionType = "Expense";
                            }
                            if (income_button.getId() == toggleGroup.getCheckedButtonId()) {
                                transactionType = "Income";
                            }
                            if (transfer_button.getId() == toggleGroup.getCheckedButtonId()) {
                                transactionType = "Transfer";
                            }

                            Thread thread1 = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (transactionType == "Transfer") {
                                        dbDAO.registerTransaction(new Transaction(transactionType, accountChoose.getText().toString(), toAccountChoose.getText().toString(), amountParsed, dateText.getText().toString(), positionString, imageUriString));
                                        Utilities.removeToAccount(accountChoose.getText().toString(), amountParsed, activity);
                                        Utilities.addToAccount(toAccountChoose.getText().toString(), amountParsed, activity);
                                    } else {
                                        dbDAO.registerTransaction(new Transaction(transactionType, accountChoose.getText().toString(), null,  amountParsed, dateText.getText().toString(), positionString, imageUriString));
                                        if (transactionType == "Income") {
                                            Utilities.addToAccount(accountChoose.getText().toString(), amountParsed, activity);
                                        } else if (transactionType == "Expense"){
                                            List<Budget> budgetList = dbDAO.getBudgets();

                                            int i;
                                            for(i=0; i<budgetList.size(); i++){
                                                double tempAmount = Double.parseDouble(budgetList.get(i).getBudget_Used())+Double.parseDouble(amount.getText().toString());
                                                dbDAO.updateBudget(String.valueOf(tempAmount), String.valueOf(budgetList.get(i).getId()));
                                            }
                                            Utilities.removeToAccount(accountChoose.getText().toString(), amountParsed, activity);
                                        }

                                    }
                                }

                            });
                            thread1.start();
                            try {
                                thread1.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            FragmentContainerView temp = activity.findViewById(R.id.fragmentContainerView);
                            String currentFragment = temp.getFragment().getTag();

                            if (currentFragment.equals("fragment_transaction")) {
                                Utilities.insertFragment(activity, new fragment_transaction(activity), fragment_transaction.class.getSimpleName());
                            } else if (currentFragment.equals("fragment_accounts")) {
                                Utilities.insertFragment(activity, new fragment_accounts(activity), fragment_accounts.class.getSimpleName());
                            }
                        } else {
                            Toast toast = Toast.makeText(getContext(), "Importo uguale a zero o non inserito!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fragment_add_transaction.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.dateText.setText(day + "/" + month + "/" + year);
    }

    @Override
    public void onClick(View view) {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), this, this.year, this.month, this.day);
        dialog.show();
    }

    public ArrayList<String> getAccounts() {
        ArrayList<String> temp = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for(i=0; i < dbDAO.getAccounts().size(); i++) {
                    temp.add(dbDAO.getAccounts().get(i).Account_Name);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return temp;
    }

    private void initializeLocation(Activity activity) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();

                if (isNetworkConnected) {
                    sendVolleyRequest(String.valueOf(location.getLatitude()),
                            String.valueOf(location.getLongitude()));

                    requestingLocationUpdates = false;
                    stopLocationUpdates();
                } else {
                    snackbar.show();
                }
            }
        };
    }

    private void startLocationUpdates(Activity activity) {
        final String PERMISSION_REQUESTED = Manifest.permission.ACCESS_FINE_LOCATION;
        //permission granted
        if (ActivityCompat.checkSelfPermission(activity, PERMISSION_REQUESTED)
                == PackageManager.PERMISSION_GRANTED) {
            checkStatusGPS(activity);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        } else if (ActivityCompat
                .shouldShowRequestPermissionRationale(activity, PERMISSION_REQUESTED)) {
            //permission denied before
            showDialog(activity);
        } else {
            //ask for the permission
            requestPermissionLauncher.launch(PERMISSION_REQUESTED);
        }

    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void showDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("Permission denied, but needed for gps functionality.")
                .setCancelable(false)
                .setPositiveButton("OK", ((dialogInterface, i) ->
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))))
                .setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()))
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates && getActivity() != null){
            startLocationUpdates(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void checkStatusGPS(Activity activity) {
        final LocationManager locationManager =
                (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        //if gps is off, show the alert message
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            new AlertDialog.Builder(activity)
                    .setMessage("Your GPS is off, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", ((dialogInterface, i) ->
                            activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))))
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                    .create()
                    .show();
        }
    }

    private void sendVolleyRequest(String latitude, String longitude) {
        String url = "https://nominatim.openstreetmap.org/reverse?lat=" + latitude +
                "&lon="+ longitude + "&format=jsonv2&limit=1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            descriptionTIET.setText(response.getJSONObject("address").getString("city"));
                            if(descriptionTIET.getText().equals("")){
                                descriptionTIET.setText(response.getJSONObject("address").getString("town"));
                            }

                            unregisterNetworkCallback();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LAB-ADDFRAGMENT", error.toString());
                    }
                });

        jsonObjectRequest.setTag(OSM_REQUEST_TAG);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(OSM_REQUEST_TAG);
        }
        if (requestingLocationUpdates)
            unregisterNetworkCallback();
    }

    private void registerNetworkCallback(Activity activity){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = (networkInfo != null && networkInfo.isConnected());
            }
        } else {
            isNetworkConnected = false;
        }
    }

    private void unregisterNetworkCallback(){
        if (getActivity() != null){
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connectivityManager.unregisterNetworkCallback(networkCallback);
                }
            } else {
                snackbar.dismiss();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null && requestingLocationUpdates){
            registerNetworkCallback(getActivity());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "onActivityResult: ");
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView image = mView.findViewById(R.id.picture_displayed_imageview);
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(imageBitmap);

            this.imageBitmap.setValue(imageBitmap);
        }
    }

    private Uri saveImage(Bitmap bitmap, Activity activity) throws FileNotFoundException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY)
                .format(new Date());
        String name = "JPEG_" + timestamp + ".jpg";

        ContentResolver contentResolver = activity.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");

        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);

        Log.d("AddFragment", String.valueOf(imageUri));

        OutputStream outputStream = contentResolver.openOutputStream(imageUri);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageUri;

    }
}
