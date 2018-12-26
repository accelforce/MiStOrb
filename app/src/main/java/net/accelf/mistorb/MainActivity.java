package net.accelf.mistorb;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.accelf.mistorb.util.InstancePickUtil;
import net.accelf.mistorb.viewhelper.GlobalMenuHelper;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private InstancePickUtil instancePicker;
    private String selectedDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instancePicker = new InstancePickUtil(this);

        if (!checkLoggedIn()) {
            requestInstanceDomain();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shared, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (GlobalMenuHelper.onGlobalMenuItemSelected(this, item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkLoggedIn() {
        if (instancePicker.selectAnyway()) {
            selectedDomain = instancePicker.selectedInstance();
            return true;
        }
        return false;
    }

    private void requestInstanceDomain() {
        Intent intent = new Intent(this, DomainInputActivity.class);
        startActivity(intent);
        finish();
    }
}
