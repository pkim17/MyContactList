package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;


public class ContactSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);
        initListButton();
        initMapButton();
        initSettingButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();
        initSetColor();


    }

    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    private void initMapButton() {
        ImageButton ibMap = (ImageButton) findViewById(R.id.imageButtonMap);
        ibMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    private void initSettingButton() {
        ImageButton ibSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibSettings.setEnabled(false);
    }

    private void initSettings() {
//        Retrieves string value associated with sortfield key
//        If no value stored, default value is assigned
        String sortBy = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE)
                .getString("sortfield","contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE)
                .getString("sortorder","ASC");
        String setColor = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE)
                .getString("setcolor","pink");


        RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
        RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
        RadioButton rbBirthday = (RadioButton) findViewById(R.id.radioBirthday);

//        Value retrieved for preferred sort field is to determine which RadioButton should be checked
        if (sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        }
        else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        }
        else {
            rbBirthday.setChecked(true);
        }

        RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
        RadioButton rbDescending = (RadioButton) findViewById(R.id.radioDescending);

        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        }
        else {
            rbDescending.setChecked(true);
        }

//        If a certain color is selected, apply that color when user enters page
        RadioButton rbPink = (RadioButton) findViewById(R.id.radioSetColorPink);
        RadioButton rbGold = (RadioButton) findViewById(R.id.radioSetColorGold);
        RadioButton rbLightPurple = (RadioButton) findViewById(R.id.radioSetColorLightPurple);
        ScrollView s = (ScrollView) findViewById(R.id.ScrollViewSettings);

        if (setColor.equalsIgnoreCase("pink")) {
            rbPink.setChecked(true);
            s.setBackgroundResource(R.color.exercise_radio_color_pink);
        }
        else if (setColor.equalsIgnoreCase("gold")) {
            rbGold.setChecked(true);
            s.setBackgroundResource(R.color.exercise_radio_color_gold);

        }
        else {
            rbLightPurple.setChecked(true);
            s.setBackgroundResource(R.color.exercise_radio_color_light_purple);

        }






    }

    private void initSortByClick()  {

        RadioGroup rgSortBy = (RadioGroup) findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
                RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);

                if (rbName.isChecked()) {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "contactname").commit();
                }
                else if (rbCity.isChecked())    {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "city").commit();
                }
                else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "birthday").commit();
                }

            }
        });


    }

    private void initSortOrderClick() {

        RadioGroup rgSortOrder = (RadioGroup) findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
                if (rbAscending.isChecked())    {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder", "ASC").commit();
                }
                else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder", "DESC").commit();
                }
            }
        });
    }


//    Exercise 5.1
//    Applying background color of the RadioButton selected
    private void initSetColor()  {


        final ScrollView s = (ScrollView) findViewById(R.id.ScrollViewSettings);

        RadioGroup rgSetColor = (RadioGroup) findViewById(R.id.RadioGroupSetColor);
        rgSetColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbPink = (RadioButton) findViewById(R.id.radioSetColorPink);
                RadioButton rbGold = (RadioButton) findViewById(R.id.radioSetColorGold);
                RadioButton rbLightPurple = (RadioButton) findViewById(R.id.radioSetColorLightPurple);

//                Inserts selected color preference into getSharedPreferences for next time
//                Also, sets color as buttons are clicked

                if (rbPink.isChecked()) {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("setcolor", "pink").commit();
                    s.setBackgroundResource(R.color.exercise_radio_color_pink);
                }
                else if (rbGold.isChecked())    {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("setcolor", "gold").commit();
                    s.setBackgroundResource(R.color.exercise_radio_color_gold);
                }
                else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("setcolor", "lightpurple").commit();
                    s.setBackgroundResource(R.color.exercise_radio_color_light_purple);
                }

            }
        });


    }


}
