package org.tennez.inventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class AddProductsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showAddProductsView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_products, menu);
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

    private void showAddProductsView() {
        String sourceItemName = null;
        Item sourceItem = null;
        setContentView(R.layout.activity_add_products);
        ItemsRepository itemsRepository = ItemsRepository.getItemsRepository(getApplicationContext());

        if(getIntent() != null && getIntent().getExtras() != null) {
            sourceItemName = getIntent().getExtras().getString(ItemsConfigurationActivity.SOURCE_ITEM_EXTRA);
            if(sourceItemName != null) {
                sourceItem = itemsRepository.getItem(sourceItemName);
            }
        }
        final boolean editMode = sourceItem != null;
        final String editItemName = sourceItemName;

        List<String> storageSections = itemsRepository.getStorageSections();
        Log.e("IVTD","Storage sections: "+storageSections.size());
        String[] storageSectionsList = new String[storageSections.size()+1];
        storageSectionsList[0] = getResources().getString(R.string.createNewStorageSection);
        for(int i = 0; i< storageSections.size() ;++i) {
            storageSectionsList[i+1] = storageSections.get(i);
        }
        ArrayAdapter<String> storageSectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storageSectionsList);
        Spinner spinner = (Spinner)findViewById(R.id.storageSectionSelection);
        spinner.setAdapter(storageSectionsAdapter);
        if(sourceItem == null) {
            spinner.setSelection(0);
        } else {
            spinner.setSelection(storageSectionsAdapter.getPosition(sourceItem.getStorageSection()));
        }

        List<String> storeSections = itemsRepository.getStoreSections();
        String[] storeSectionsList = new String[storeSections.size()+1];
        storeSectionsList[0] = getResources().getString(R.string.createNewStoreSection);
        for(int i = 0; i< storeSections.size() ;++i) {
            storeSectionsList[i+1] = storeSections.get(i);
        }
        ArrayAdapter<String> storeSectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storeSectionsList);
        spinner = (Spinner)findViewById(R.id.storeSectionSelection);
        spinner.setAdapter(storeSectionsAdapter);
        if(sourceItem == null) {
            spinner.setSelection(0);
        } else {
            spinner.setSelection(storeSectionsAdapter.getPosition(sourceItem.getStoreSection()));
        }



        Log.e("IVTD","source item name = "+sourceItemName+" ("+sourceItem+")");
        Button addBtn = (Button)findViewById(R.id.addBtn);
        if(editMode) {
            addBtn.setText(getResources().getString(R.string.edit));
        } else {
            addBtn.setText(getResources().getString(R.string.add));
        }
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storageSection;
                Spinner spinner = (Spinner) findViewById(R.id.storageSectionSelection);
                if (spinner.getSelectedItemPosition() > 0) {
                    storageSection = (String) spinner.getSelectedItem();
                } else {
                    TextView storageText = (TextView) findViewById(R.id.storageSectionText);
                    storageSection = storageText.getText().toString();
                }

                String storeSection;
                spinner = (Spinner) findViewById(R.id.storeSectionSelection);
                if (spinner.getSelectedItemPosition() > 0) {
                    storeSection = (String) spinner.getSelectedItem();
                } else {
                    TextView storeText = (TextView) findViewById(R.id.storeSectionText);
                    storeSection = storeText.getText().toString();
                }

                TextView nameText = (TextView) findViewById(R.id.nameText);
                String name = nameText.getText().toString();

                ItemsRepository itemsRepository = ItemsRepository.getItemsRepository(getApplicationContext());


                if(editMode) {
                    itemsRepository.updateItem(editItemName, name, storageSection, storeSection);
                    finish();
                } else {
                    itemsRepository.addItem(new Item(name, storageSection, storeSection, false));
                    reset();
                }
            }
        });

        Button clearBtn = (Button)findViewById(R.id.clearBtn);
        if(editMode) {
            TextView nameText = (TextView)findViewById(R.id.nameText);
            nameText.setText(sourceItem.getName());
            clearBtn.setText(getResources().getString(R.string.delete));
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemsRepository itemsRepository = ItemsRepository.getItemsRepository(getApplicationContext());
                    itemsRepository.removeItem(itemsRepository.getItem(editItemName));
                    finish();
                }
            });
        } else {
            clearBtn.setText(getResources().getString(R.string.clear));
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Spinner spinner = (Spinner) findViewById(R.id.storageSectionSelection);
                    TextView storageText = (TextView) findViewById(R.id.storageSectionText);
                    spinner.setSelection(0);
                    storageText.setText("");

                    spinner = (Spinner) findViewById(R.id.storeSectionSelection);
                    TextView storeText = (TextView) findViewById(R.id.storeSectionText);
                    spinner.setSelection(0);
                    storeText.setText("");

                    TextView nameText = (TextView) findViewById(R.id.nameText);
                    nameText.setText("");
                }
            });
        }
    }

    private void reset() {
        ItemsRepository itemsRepository = ItemsRepository.getItemsRepository(getApplicationContext());
        List<String> storageSections = itemsRepository.getStorageSections();
        String[] storageSectionsList = new String[storageSections.size()+1];
        storageSectionsList[0] = getResources().getString(R.string.createNewStorageSection);
        for(int i = 0; i< storageSections.size() ;++i) {
            storageSectionsList[i+1] = storageSections.get(i);
        }
        ArrayAdapter<String> storageSectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storageSectionsList);
        Spinner spinner = (Spinner)findViewById(R.id.storageSectionSelection);
        int currentSelection = spinner.getSelectedItemPosition();
        spinner.setAdapter(storageSectionsAdapter);
        TextView storageText = (TextView) findViewById(R.id.storageSectionText);
        if(currentSelection > 0) {
            spinner.setSelection(currentSelection);
        } else {
            spinner.setSelection(storageSections.indexOf(storageText.getText().toString())+1);
        }
        storageText.setText("");

        List<String> storeSections = itemsRepository.getStoreSections();
        String[] storeSectionsList = new String[storeSections.size()+1];
        storeSectionsList[0] = getResources().getString(R.string.createNewStoreSection);
        for(int i = 0; i< storeSections.size() ;++i) {
            storeSectionsList[i+1] = storeSections.get(i);
        }
        ArrayAdapter<String> storeSectionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storeSectionsList);
        spinner = (Spinner)findViewById(R.id.storeSectionSelection);
        currentSelection = spinner.getSelectedItemPosition();
        spinner.setAdapter(storeSectionsAdapter);
        TextView storeText = (TextView) findViewById(R.id.storeSectionText);
        if(currentSelection > 0) {
            spinner.setSelection(currentSelection);
        } else {
            spinner.setSelection(storeSections.indexOf(storeText.getText().toString())+1);
        }
        storeText.setText("");

        TextView nameText = (TextView)findViewById(R.id.nameText);
        nameText.setText("");
    }
}
