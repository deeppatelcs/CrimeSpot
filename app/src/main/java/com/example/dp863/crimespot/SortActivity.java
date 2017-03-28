package com.example.dp863.crimespot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SortActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView list;
    private String[] crimeTypes = {
            "Assault",
            "Larceny",
            "Robbery",
            "Trespassing",
            "Property",
            "Weapons",
            "Miscellaneous"
    };

    private Integer[] imageId = {
            R.drawable.rsz_6,
            R.drawable.rsz_6,
            R.drawable.rsz_6,
            R.drawable.rsz_6,
            R.drawable.rsz_6,
            R.drawable.rsz_6,
            R.drawable.rsz_6
    };

    protected boolean[] isChecked = {
            true,
            true,
            true,
            true,
            true,
            true,
            true
    };
    private ImageButton mImageButton1;
    private ImageButton mImageButton2;
    private ImageButton mImageButton3;

    boolean[] MyBooleanArray = new boolean[7];

//    private boolean[] isChecked = new boolean[7];

    private static final String TAG = SortActivity.class.getSimpleName();


    CustomList adapter = null;
//    private CheckBox mCheckBox;
//    private ImageButton mImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        if (getIntent().getExtras() != null) {
            isChecked = getIntent().getExtras().getBooleanArray("yourBool");
            for (int i = 0; i < isChecked.length; i++) {
                Log.i(TAG, isChecked[i] ? "SortActivity true" : "SortActivity false");
            }
        }

        adapter = new
                CustomList(SortActivity.this, crimeTypes, imageId, isChecked);
        list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getApplicationContext(), "You Clicked on " + crimeTypes[+position], Toast.LENGTH_SHORT).show();
            }
        });

        //Return to map
        mImageButton1 = (ImageButton) findViewById(R.id.imageButton2);
        mImageButton1.setOnClickListener(this);

        //save button
        mImageButton2 = (ImageButton) findViewById(R.id.imageButton8);
        mImageButton2.setOnClickListener(this);

        // date picker
        mImageButton3 = (ImageButton) findViewById(R.id.imageButton4);
        mImageButton3.setOnClickListener(this);
    }

        /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            //save setting button
            ImageButton mImageButton1 = (ImageButton) findViewById(R.id.imageButton8);
            mImageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0; i<isChecked.length; i++){
                        Log.i(TAG, isChecked[i]?"Save true":"Save false");
                    }
                    Toast.makeText(SortActivity.this, "Saved Settings", Toast.LENGTH_SHORT).show();
                    Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    mIntent.putExtra("yourBool", isChecked);
                    startActivity(mIntent);
                }
            });
            //return to map button
            ImageButton mImageButton2 = (ImageButton) findViewById(R.id.imageButton2);
            mImageButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(SortActivity.this, "Go to Map", Toast.LENGTH_SHORT).show();
                    Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(mIntent);
                }
            });
            return super.onCreateOptionsMenu(menu);
        }
        */


    @Override
    public void onClick(View view) {
        //return to map button
        if (view == mImageButton1) {
            Toast.makeText(SortActivity.this, "Go to Map", Toast.LENGTH_SHORT).show();
            Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(mIntent);
        }

        //save setting button
        if (view == mImageButton2) {
            for (int i = 0; i < isChecked.length; i++) {
                Log.i(TAG, isChecked[i] ? "Save true" : "Save false");
            }
            Toast.makeText(SortActivity.this, "Saved Settings", Toast.LENGTH_SHORT).show();

            Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mIntent.putExtra("yourBool", isChecked);
            startActivity(mIntent);
        }

        //date picker button
        if(view == mImageButton3){
            Intent mIntent = new Intent(getApplicationContext(), DatePickerActivity.class);
            startActivity(mIntent);
        }

    }


    public class CustomList extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] crimeTypes;
        private final Integer[] imageId;
        private final boolean[] isChecked;

        public CustomList(Activity context, String[] crimeTypes, Integer[] imageId, boolean[] isChecked) {
            super(context, R.layout.custom_template, crimeTypes);
            this.context = context;
            this.crimeTypes = crimeTypes;
            this.imageId = imageId;
            this.isChecked = isChecked;
        }

        private class ViewHolder {
            ImageView mImageView;
            CheckBox mCheckBox;
            TextView mTextView;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            ViewHolder holder = null;

            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.custom_template, null, true);

            holder = new ViewHolder();
            holder.mImageView = (ImageView) rowView.findViewById(R.id.imageButton7);
            holder.mCheckBox = (CheckBox) rowView.findViewById(R.id.checkBox);
            holder.mTextView = (TextView) rowView.findViewById(R.id.textView);
            rowView.setTag(holder);

            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    //Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(),
                    //        Toast.LENGTH_SHORT).show();
                }
            });

            MyClickListener myClickListener = new MyClickListener(position, isChecked);

            holder.mCheckBox.setOnClickListener(myClickListener);

            holder.mTextView.setText(crimeTypes[position]);
            holder.mImageView.setImageResource(imageId[position]);
            holder.mCheckBox.setChecked(isChecked[position]);
            return rowView;
        }
    }

}