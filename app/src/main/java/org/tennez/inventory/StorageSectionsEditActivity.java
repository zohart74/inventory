package org.tennez.inventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.listviewdragginganimation.DynamicListView;
import com.example.android.listviewdragginganimation.StableArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class StorageSectionsEditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_list, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        setContentView(R.layout.activity_storage_section_edit);
        ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
        final List<String> storageSections = repository.getStorageSections();
        StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.listview_dragging_textview, storageSections);
        DynamicListView listView = (DynamicListView) findViewById(R.id.storageSections);

        listView.setCheeseList(storageSections);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        Button saveButton = (Button)findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
                repository.updateStorageSections(storageSections);
                finish();
            }
        });
        Button cancelBtn = (Button)findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
