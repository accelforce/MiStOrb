package net.accelf.mistorb;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import net.accelf.mistorb.util.InstancePickUtil;

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
