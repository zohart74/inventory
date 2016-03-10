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

import org.tennez.inventory.db.DbBackup;

import java.io.File;
import java.io.InputStream;

public class MainMenu extends Activity {

    private static final int INVENTORY_CHECK_INDEX = 0;
    private static final int SHOPPING_LIST_INDEX = 1;
    private static final int ADD_PRODUCTS_INDEX = 2;
    private static final int EDIT_PRODUCTS_INDEX = 3;
    private static final int BACKUP_INDEX = 4;
    private static final int RESTORE_INDEX = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMainMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

    private void showMainMenu() {
        setContentView(R.layout.activity_main_menu);
        String[] mainMenuItems = new String[] {
                getResources().getString(R.string.inventoryCheck),
                getResources().getString(R.string.shoppingList),
                getResources().getString(R.string.addProducts),
                getResources().getString(R.string.editProducts),
                getResources().getString(R.string.backup),
                getResources().getString(R.string.restore)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainMenuItems);
        ListView listView = (ListView)findViewById(R.id.mainMenu);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == INVENTORY_CHECK_INDEX) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), InventoryCheckActivity.class);
                    startActivity(intent);
                } else if (position == SHOPPING_LIST_INDEX) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), ShoppingListActivity.class);
                    startActivity(intent);
                } else if (position == ADD_PRODUCTS_INDEX) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), AddProductsActivity.class);
                    startActivity(intent);
                } else if (position == EDIT_PRODUCTS_INDEX) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), ItemsConfigurationActivity.class);
                    startActivity(intent);
                } else if (position == BACKUP_INDEX) {
                    File backupFile = new File(getApplicationContext().getExternalCacheDir(),"db-backup.json");
                    try {
                        DbBackup.saveDb(getApplicationContext(), backupFile);
                    } catch (Exception e) {
                        Log.e("IVTD","Failed to backup db", e);
                    }
                } else if (position == RESTORE_INDEX) {
                    try {
                        InputStream restoreAsset = getApplicationContext().getAssets().open("db-backup.json");
                        DbBackup.loadDb(getApplicationContext(), restoreAsset);
                    } catch (Exception e) {
                        Log.e("IVTD","Failed to restore db", e);
                    }
                }
            }
        });
    }
}
