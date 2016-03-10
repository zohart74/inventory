package org.tennez.inventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ItemsConfigurationActivity extends Activity {

    static final String SOURCE_ITEM_EXTRA = "sourceItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_configuration, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setItemsConfigurationView();
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

    private void setItemsConfigurationView() {
        setContentView(R.layout.activity_items_configuration);
        ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
        List<Item> allItems = repository.getAllItems();

        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, allItems.toArray(new Item[allItems.size()]));
        ListView listView = (ListView)findViewById(R.id.itemsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) findViewById(R.id.itemsList);
                Item item = (Item) listView.getAdapter().getItem(position);
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AddProductsActivity.class);
                intent.putExtra(SOURCE_ITEM_EXTRA, item.getName());
                startActivity(intent);
            }
        });



    }
}
