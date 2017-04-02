package com.example.dp863.crimespot;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.Override;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

public class DatePickerActivity extends AppCompatActivity implements View.OnClickListener {

    //    UI References
    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    Date fromDate, toDate;

    private ImageButton mImageButton1;
    private ImageButton mImageButton2;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        findViewsById();

        setDateTimeField();

        //Return to map
        mImageButton1 = (ImageButton) findViewById(R.id.imageButton2);
        mImageButton1.setOnClickListener(this);

        //save button
        mImageButton2 = (ImageButton) findViewById(R.id.imageButton8);
        mImageButton2.setOnClickListener(this);
    }

    private void findViewsById() {
        fromDateEtxt = (EditText) findViewById(R.id.etxt_fromdate);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();

        toDateEtxt = (EditText) findViewById(R.id.etxt_todate);
        toDateEtxt.setInputType(InputType.TYPE_NULL);
    }

    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                //Date fromDate = newDate.getTime();


                //Log.e("My App", "From Date" + fromDateEtxt.getText() );
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        }
        if(view == toDateEtxt) {
            toDatePickerDialog.show();
        }


        //return to map button
        if (view == mImageButton1) {
            Toast.makeText(DatePickerActivity.this, "Go to Map", Toast.LENGTH_SHORT).show();
            Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(mIntent);
        }

        //save dates and return to map
        if (view == mImageButton2)  {
            Toast.makeText(DatePickerActivity.this, "Saved Settings", Toast.LENGTH_SHORT).show();
            Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mIntent.putExtra("fromDate", fromDateEtxt.getText().toString());
            mIntent.putExtra("toDate", toDateEtxt.getText().toString());
            startActivity(mIntent);
        }
    }
}
