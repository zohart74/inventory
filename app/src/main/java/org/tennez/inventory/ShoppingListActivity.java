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

public class ShoppingListActivity extends Activity {

    static final String SELECTED_SECTION_EXTRA = "selectedSection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShoppingListView();

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
        setShoppingListView();
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

    private void setShoppingListView() {
        setContentView(R.layout.activity_shopping_list);
        ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
        List<String> storeSectionNames = repository.getShoppingListStoreSections();
        List<StoreSection>  storeSections = new ArrayList<StoreSection>(storeSectionNames.size());
        for(String storeSectionName : storeSectionNames) {
            List<Item> items = repository.getShoppingListItems(storeSectionName);
            int cartItems = 0;
            for(Item item : items) {
                if(repository.isInShoppingCart(item)) {
                    cartItems++;
                }
            }
            storeSections.add(new StoreSection(storeSectionName, items.size(), cartItems));
        }

        StoreSectionAdapter adapter = new StoreSectionAdapter(this, android.R.layout.simple_list_item_1, storeSections.toArray(new StoreSection[storeSections.size()]));
        ListView listView = (ListView)findViewById(R.id.storeSections);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) findViewById(R.id.storeSections);
                String storeSection = ((StoreSection)listView.getAdapter().getItem(position)).name;
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ShoppingListSectionActivity.class);
                Log.e("IVTD","Setting extra "+SELECTED_SECTION_EXTRA+" to "+storeSection);
                intent.putExtra(SELECTED_SECTION_EXTRA, storeSection);
                startActivity(intent);
            }
        });

        Button finishButton = (Button)findViewById(R.id.endBtn);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
                repository.completeShopping();
                finish();
            }
        });
        Button clearBtn = (Button)findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
                repository.clearShoppingCart();
                setShoppingListView();
            }
        });

    }

    static class StoreSection {
        public String name;
        public int items;
        public int itemsInCart;

        public StoreSection(String name, int items, int itemsInCart) {
            this.name = name;
            this.items = items;
            this.itemsInCart = itemsInCart;
        }

        @Override
        public String toString() {
            return name+" ("+items+" / "+itemsInCart+")";
        }
    }

    static class StoreSectionAdapter extends ArrayAdapter<StoreSection> {

        public StoreSectionAdapter(Context context, int resource) {
            super(context, resource);
        }

        public StoreSectionAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public StoreSectionAdapter(Context context, int resource, StoreSection[] objects) {
            super(context, resource, objects);
        }

        public StoreSectionAdapter(Context context, int resource, int textViewResourceId, StoreSection[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public StoreSectionAdapter(Context context, int resource, List<StoreSection> objects) {
            super(context, resource, objects);
        }

        public StoreSectionAdapter(Context context, int resource, int textViewResourceId, List<StoreSection> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            StoreSection storeSection = getItem(position);
            if(storeSection.itemsInCart == storeSection.items) {
                view.setBackgroundColor(Color.GREEN);
            } else if(storeSection.itemsInCart == 0){
                view.setBackgroundColor(Color.RED);
            } else {
                view.setBackgroundColor(Color.YELLOW);
            }
            return view;
        }
    }
}
