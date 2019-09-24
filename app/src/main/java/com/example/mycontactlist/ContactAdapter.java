package com.example.mycontactlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> items;
    private Context adapterContext;

    public ContactAdapter(Context context, ArrayList<Contact> items)    {
        super(context, R.layout.list_item, items);
        adapterContext = context;
        this.items = items;
    }

    // Populates the list
    public View getView(int position, View convertView, ViewGroup parent)   {
        View v = convertView;
        try {
            Contact contact = items.get(position);

            if (v == null)  {
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

//            IN PROGRESS
//            Exercise 6.4 Alternate between red and blue text
//                if (position % 2 == 0) {
//                    TextView contactNameRed = (TextView) v.findViewById(R.id.textContactNameRed);
//                    contactNameRed.setText(contact.getContactName());
//
//                    //            Exercise 6.1 Display Home: # and Cell: #
//                    TextView contactHomeNumber = (TextView) v.findViewById(R.id.textHomeNumberRed);
//                    TextView contactCellNumber = (TextView) v.findViewById(R.id.textCellNumberRed);
//                    contactHomeNumber.setText("HomeR: " + contact.getPhoneNumber());
//                    contactCellNumber.setText("CellR: " + contact.getCellNumber());
//                    Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
//                    b.setVisibility(View.INVISIBLE);
//                } else {
//                    TextView contactNameBlue = (TextView) v.findViewById(R.id.textContactNameBlue);
//                    contactNameBlue.setText(contact.getContactName());
//
//                    //            Exercise 6.1 Display Home: # and Cell: #
//                    TextView contactHomeNumber = (TextView) v.findViewById(R.id.textHomeNumberBlue);
//                    TextView contactCellNumber = (TextView) v.findViewById(R.id.textCellNumberBlue);
//                    contactHomeNumber.setText("HomeB: " + contact.getPhoneNumber());
//                    contactCellNumber.setText("CellB: " + contact.getCellNumber());
//                    Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
//                    b.setVisibility(View.INVISIBLE);
//                }

            TextView contactName = (TextView) v.findViewById(R.id.textContactName);
            contactName.setText(contact.getContactName());

//            Exercise 6.3 to display Address
            TextView contactAddress = (TextView) v.findViewById(R.id.textListAddress);
            contactAddress.setText(contact.getStreetAddress() + " " + contact.getCity() + " "
                    + contact.getState() + " " + contact.getZipCode());

//            Exercise 6.1 Display Home: # and Cell: #
            TextView contactHomeNumber = (TextView) v.findViewById(R.id.textHomeNumber);
            TextView contactCellNumber = (TextView) v.findViewById(R.id.textCellNumber);
            contactHomeNumber.setText("Home: " + contact.getPhoneNumber());
            contactCellNumber.setText("Cell: " + contact.getCellNumber());

            Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
            b.setVisibility(View.INVISIBLE);
//            ImageView bestFriendForever = (ImageView) v.findViewById(R.id.imageBestFriend);





        }
        catch (Exception e)   {
            e.printStackTrace();
            e.getCause();
        }

        return v;

    }

    public void showDelete(final int position, final View convertView, final Context context, final Contact contact)    {

        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);

        if (b.getVisibility() == View.INVISIBLE)    {
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDelete(position, convertView, context);
                    items.remove(contact);
                    deleteOption(contact.getContactID(), context);
                }
            });
        }
        else {
            hideDelete(position, convertView, context);
        }
    }

    private void deleteOption(int contactToDelete, Context context) {

        ContactDataSource db = new ContactDataSource(context);
        try {
            db.open();
            db.deleteContact(contactToDelete);
            db.close();
        }
        catch (Exception e) {
            Toast.makeText(adapterContext, "Delete Contact Failed", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();
    }

    public void hideDelete(int position, View convertView, Context context) {
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
        b.setVisibility(View.INVISIBLE);
        b.setOnClickListener(null);
    }


}
