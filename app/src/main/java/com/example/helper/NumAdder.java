package com.example.helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NumAdder extends AppCompatActivity implements View.OnClickListener{
    private EditText contactET;
    private Button add_btn;
    private ListView contactsList;

    private ArrayList<String> contacts;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_adder);

        contactET = findViewById(R.id.contact_edit_txt);
        add_btn = findViewById(R.id.add_btn);
        contactsList = findViewById(R.id.contacts_list);

        contacts = ContactHelper.readData(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
        contactsList.setAdapter(adapter);

        add_btn.setOnClickListener(this);

        registerForContextMenu(contactsList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_btn:
                //You have to remove the following if-else later..
                if(contacts.size()==0) {
                    String itemEntered = contactET.getText().toString();
                    adapter.add(itemEntered);
                    contactET.setText("");
                    ContactHelper.writeData(contacts, this);
                    Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
                }
                else{
                    contactET.setText("");
                    Toast.makeText(this, "Only one contact can be added at a time.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteContactList:
                AlertDialog.Builder adb = new AlertDialog.Builder(NumAdder.this);
                adb.setTitle("Warning");
                adb.setMessage("Do you want to remove all contacts from the list?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        contacts.clear();
                        adapter.notifyDataSetChanged();
                        ContactHelper.writeData(contacts, getApplicationContext());
                        Toast.makeText(getApplicationContext(), "All Contacts removed", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                adb.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String contact_name;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.deleteContact:
                contact_name = contacts.get(info.position);
                contacts.remove(info.position);
                adapter.notifyDataSetChanged();
                ContactHelper.writeData(contacts, this);
                Toast.makeText(this, contact_name+" removed", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}

