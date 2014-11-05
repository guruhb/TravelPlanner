package com.guruhb.travelplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;


public class SearchActivity extends Activity {

    private SearchLocationTextView mSourceLocation;
    private SearchLocationTextView mDestinationLocation;

    private long mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_search);

        mSourceLocation = (SearchLocationTextView) findViewById(R.id.sourceLocationTextId);
        if (mSourceLocation != null) {
            mSourceLocation.setThreshold(2);
            mSourceLocation.setAdapter(new SearchLocationAdapter(this));
            mSourceLocation.setThreshold(2);
            mSourceLocation.setLoadingProgress((android.widget.ProgressBar) findViewById(R.id.sourceLocationProgressId));
            mSourceLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SuggestLocation sl = (SuggestLocation) parent.getItemAtPosition(position);
                    Log.v("SearchActivity", "item selected : " + sl.getName());
                    mSourceLocation.setText(sl.getFullName());
                }
            });
        } else {
            Toast.makeText(this, "SearchLocationTextView get resource failed", Toast.LENGTH_SHORT).show();
        }

        mDestinationLocation = (SearchLocationTextView) findViewById(R.id.destinationLocationTextId);
        if (mDestinationLocation != null) {
            mDestinationLocation.setThreshold(2);
            mDestinationLocation.setAdapter(new SearchLocationAdapter(this));
            mDestinationLocation.setThreshold(2);
            mDestinationLocation.setLoadingProgress((android.widget.ProgressBar) findViewById(R.id.destinationLocationProgressId));
            mDestinationLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SuggestLocation sl = (SuggestLocation) parent.getItemAtPosition(position);
                    Log.v("SearchActivity", "item selected : " + sl.getName());
                    mDestinationLocation.setText(sl.getFullName());
                }
            });
        } else {
            Toast.makeText(this, "SearchLocationTextView get resource failed", Toast.LENGTH_SHORT).show();
        }

        Button searchButton = (Button) findViewById(R.id.searchButtonId);
        if(searchButton != null) {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this,  getResources().getString(R.string.search_not_implemented), Toast.LENGTH_SHORT).show();
                }
            });
        }


        Button calendarButton = (Button) findViewById(R.id.calenderButtonId);
        if(calendarButton != null) {
            calendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCalenderDialog();
                }
            });
        }


        //Search calendar Dialog
        EditText editText = (EditText) findViewById(R.id.editTextCalendarId);
        if(editText != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            date.getTime();
            String ddmmyy = simpleDateFormat.format(date);
            editText.setText(ddmmyy);
            editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalenderDialog();
            }
    });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTravelDate() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(Calendar.DATE, -1);
        long currentTimeInMilli = gregorianCalendar.getTime().getTime();
        if(currentTimeInMilli < mSelectedDate) {
            //update date in textview
            EditText editText = (EditText) findViewById(R.id.editTextCalendarId);
            Date date = new Date(mSelectedDate);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String ddmmyy = dateFormat.format(date);

            editText.setText(ddmmyy);
        } else {
            Log.v("SearchActivity", "Selected date is out of Range");
        }
        mSelectedDate = 0;//reset the temp time
    }

    private void showCalenderDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View calView = inflater.inflate(R.layout.calender_layout, null);
        AlertDialog calDialog = new AlertDialog.Builder(SearchActivity.this)
                .setTitle("Select Date")
                .setView(calView)
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateTravelDate();
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedDate = 0;//reset the temp time
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedDate = 0;//reset the temp time
                    }
                })
                .create();
        calDialog.show();

        final CalendarView calendarView = (CalendarView) calView.findViewById(R.id.selectDateId);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Log.v("SearchActivity", "showCalenderDialog onDateChangedListener : " + dayOfMonth + " . " + month + " . " + year);

                mSelectedDate = calendarView.getDate();
            }
        });

        /*Calendar cal = Calendar.getInstance();
        CalendarView calendarView = (CalendarView) findViewById(R.id.selectDateId);
        if(calendarView != null) {
            //calendarView.setMinDate((cal.getTimeInMillis() - (1000 * 60))); //FIXME : Issue 256159:
        }*/
        Log.v("SearchActivity", "showCalenderDialog <--");
    }
}
