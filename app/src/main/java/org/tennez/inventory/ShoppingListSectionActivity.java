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

import java.util.ArrayList;
import java.util.List;

public class ShoppingListSectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String storeSection = getIntent().getExtras().getString(ShoppingListActivity.SELECTED_SECTION_EXTRA);
        Log.e("IVTD", "extra value for " + ShoppingListActivity.SELECTED_SECTION_EXTRA + "is "+storeSection);
        setShoppingListView(storeSection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_list_section, menu);
        return true;
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

    private void setShoppingListView(final String sectionName) {
        setContentView(R.layout.activity_shopping_list_section);
        ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
        Log.e("IVTD", "section name is "+sectionName);
        List<Item> storeSectionItems = repository.getShoppingListItems(sectionName);

        StoreSectionItemAdapter adapter = new StoreSectionItemAdapter(this, android.R.layout.simple_list_item_1, storeSectionItems.toArray(new Item[storeSectionItems.size()]));
        ListView listView = (ListView)findViewById(R.id.itemsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) findViewById(R.id.itemsList);
                Item item = (Item) listView.getAdapter().getItem(position);
                ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
                if (repository.isInShoppingCart(item)) {
                    repository.removeFromShoppingCart(item);
                } else {
                    repository.addToShoppingCart(item);
                }
                listView.invalidateViews();
            }
        });



    }

    static class StoreSectionItemAdapter extends ArrayAdapter<Item> {

        public StoreSectionItemAdapter(Context context, int resource) {
            super(context, resource);
        }

        public StoreSectionItemAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public StoreSectionItemAdapter(Context context, int resource, Item[] objects) {
            super(context, resource, objects);
        }

        public StoreSectionItemAdapter(Context context, int resource, int textViewResourceId, Item[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public StoreSectionItemAdapter(Context context, int resource, List<Item> objects) {
            super(context, resource, objects);
        }

        public StoreSectionItemAdapter(Context context, int resource, int textViewResourceId, List<Item> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Item item = getItem(position);
            ItemsRepository repository = ItemsRepository.getItemsRepository(view.getContext().getApplicationContext());
            if(repository.isInShoppingCart(item)) {
                view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
            return view;
        }
    }
}
