package org.tennez.inventory;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.tennez.inventory.db.DbBackup;

import java.util.LinkedList;
import java.util.List;

public class InventoryCheckActivity extends Activity {

    private static final String CURRENT_SECTION_INDEX_KEY = "currentSectionIndex";
    private static final String SECTIONS_KEY = "sections";
    private static final String CURRENT_SECTION_ITEMS_KEY = "currentSectionItems";
    private static final String CURRENT_ITEM_INDEX_KEY = "currentItemIndex";

    private int currentSectionIndex;
    private List<String> sections;
    private List<Item> currentSectionItems;
    private int currentItemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadState(savedInstanceState);
        showInventoryCheck();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory_check, menu);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loadState(savedInstanceState);
        setupView();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        loadState(savedInstanceState);
        setupView();
    }

    private void saveState(Bundle outState) {
        try {
            outState.putInt(CURRENT_SECTION_INDEX_KEY, currentSectionIndex);
            outState.putInt(CURRENT_ITEM_INDEX_KEY, currentItemIndex);
            outState.putString(CURRENT_SECTION_ITEMS_KEY, DbBackup.ItemBackup.itemsToJSONArray(currentSectionItems).toString());
            outState.putString(SECTIONS_KEY, DbBackup.stringListToJsonArray(sections).toString());
        } catch (JSONException jsone) {

        }
    }

    private void loadState(Bundle savedState) {
        if(savedState != null) {
            try {
                currentSectionIndex = savedState.getInt(CURRENT_SECTION_INDEX_KEY);
                currentItemIndex = savedState.getInt(CURRENT_ITEM_INDEX_KEY);
                currentSectionItems = DbBackup.ItemBackup.jsonArrayToItems(new JSONArray(savedState.getString(CURRENT_SECTION_ITEMS_KEY)));
                sections = DbBackup.stringsJsonArrayToStrings(new JSONArray(savedState.getString(SECTIONS_KEY)));
            } catch (JSONException jsone) {

            }
        } else {
            ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
            sections = repository.getStorageSections();
            currentSectionIndex = -1;
            moveToNextSection();
        }
    }

    private void showInventoryCheck() {
        setContentView(R.layout.activity_inventory_check);
        if(currentSectionIndex == sections.size()) {
            finish();
        } else {
            setupView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentSectionIndex == sections.size()) {
            finish();
        }

    }

    private void left() {

        if(currentItemIndex == -1) {
            currentItemIndex = 0;
            TextView title = (TextView)findViewById(R.id.title);
            title.setText(currentSectionItems.get(currentItemIndex).getName());
            Button leftBtn = (Button)findViewById(R.id.left);
            leftBtn.setText(R.string.exists);
            Button rightBtn = (Button)findViewById(R.id.right);
            rightBtn.setText(R.string.required);
        } else{
            currentItemIndex++;
            if(currentItemIndex == currentSectionItems.size()) {
                moveToNextSection();
                if(currentSectionIndex == sections.size()) {
                    finish();
                } else {
                    TextView title = (TextView)findViewById(R.id.title);
                    title.setText(sections.get(currentSectionIndex));
                    Button leftBtn = (Button)findViewById(R.id.left);
                    leftBtn.setText(R.string.check);
                    Button rightBtn = (Button)findViewById(R.id.right);
                    rightBtn.setText(R.string.skip);
                }
            } else {
                TextView title = (TextView)findViewById(R.id.title);
                title.setText(currentSectionItems.get(currentItemIndex).getName());
            }
        }
    }

    private void right() {
        if(currentItemIndex == -1) {
            moveToNextSection();
            if(currentSectionIndex == sections.size()) {
                finish();
            } else {
                TextView title = (TextView) findViewById(R.id.title);
                title.setText(sections.get(currentSectionIndex));
            }
        } else {
            ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
            repository.setItemRequired(currentSectionItems.get(currentItemIndex), true);
            currentItemIndex++;
            if(currentItemIndex == currentSectionItems.size()) {
                moveToNextSection();
                if(currentSectionIndex == sections.size()) {
                    finish();
                } else {
                    TextView title = (TextView)findViewById(R.id.title);
                    title.setText(sections.get(currentSectionIndex));
                    Button leftBtn = (Button)findViewById(R.id.left);
                    leftBtn.setText(R.string.check);
                    Button rightBtn = (Button)findViewById(R.id.right);
                    rightBtn.setText(R.string.skip);
                }
            } else {
                TextView title = (TextView)findViewById(R.id.title);
                title.setText(currentSectionItems.get(currentItemIndex).getName());
            }
        }
    }

    private void setupView() {
        Button leftBtn = (Button)findViewById(R.id.left);
        Button rightBtn = (Button)findViewById(R.id.right);
        TextView title = (TextView) findViewById(R.id.title);
        if(currentItemIndex == -1) {
            title.setText(sections.get(currentSectionIndex));
            leftBtn.setText(R.string.check);
            rightBtn.setText(R.string.skip);
        } else {
            title.setText(currentSectionItems.get(currentItemIndex).getName());
            leftBtn.setText(R.string.exists);
            rightBtn.setText(R.string.required);
        }
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right();
            }
        });
    }

    private List<Item> filterOutRequired(List<Item> items) {
        List<Item> res = new LinkedList<Item>();
        for(Item item : items) {
            if(!item.isRequired()) {
                res.add(item);
            }
        }
        return res;
    }

    private void moveToNextSection() {
        ItemsRepository repository = ItemsRepository.getItemsRepository(getApplicationContext());
        do {
            currentSectionIndex++;
            if (currentSectionIndex == sections.size()) {
                break;
            }
            currentSectionItems = filterOutRequired(repository.getStorageItems(sections.get(currentSectionIndex)));
            currentItemIndex = -1;
        } while(currentSectionItems.isEmpty());
    }
}
