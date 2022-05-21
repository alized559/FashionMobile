package com.fashion.fashionmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutActivity extends AppCompatActivity {

    private EditText fullName, address1, mobileNumber;
    private Button back, next;
    private Spinner addressSpinner, countrySpinner;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        fullName = findViewById(R.id.fullname);
        address1 = findViewById(R.id.address1);
        countrySpinner = findViewById(R.id.country_spinner);
        mobileNumber = findViewById(R.id.mobile_number);
        addressSpinner = findViewById(R.id.address_spinner);
        back = findViewById(R.id.back_btn);
        next = findViewById(R.id.next_btn);
        error = findViewById(R.id.error);

        error.setVisibility(View.GONE);

        String[] countries = {
                "Lebanon", "United States"
        };
        String[] countriesCode = {
                "(+961) Lebanon", "(+1) United States"
        };
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        addressSpinner.setAdapter(adapter1);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countriesCode);
        countrySpinner.setAdapter(adapter2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                ((Activity) CheckoutActivity.this).overridePendingTransition(0, 0);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullName.getText().toString().equals("")) {
                    error.setText("Please Enter Your Fullname");
                    error.setVisibility(View.VISIBLE);
                } else if (address1.getText().toString().equals("")) {
                    error.setText("Please Enter Your Address");
                    error.setVisibility(View.VISIBLE);
                } else if (mobileNumber.getText().toString().equals("")) {
                    error.setText("Please Enter Your Mobile Number");
                    error.setVisibility(View.VISIBLE);
                } else {
                    Intent i = new Intent(CheckoutActivity.this, CheckoutInfoActivity.class);
                    i.putExtra("fullname", fullName.getText().toString());
                    i.putExtra("address1", address1.getText().toString());
                    i.putExtra("address2", addressSpinner.getSelectedItem().toString());
                    String[] array = countrySpinner.getSelectedItem().toString().split(" ");
                    i.putExtra("countryCode", array[0].substring(2, array[0].length() - 1));
                    i.putExtra("mobileNumber", mobileNumber.getText().toString());
                    startActivity(i);
                    ((Activity) CheckoutActivity.this).overridePendingTransition(0, 0);
                    finish();
                }
            }
        });
    }
}