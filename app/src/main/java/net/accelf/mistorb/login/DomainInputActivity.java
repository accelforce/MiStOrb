package net.accelf.mistorb.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import net.accelf.mistorb.R;
import net.accelf.mistorb.menu.GlobalMenuHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class DomainInputActivity extends AppCompatActivity {

    private AppCompatEditText input;
    private Button submit;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain_input);

        setupLayoutVariables();
        setupSubmitButton();
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

    private void setupLayoutVariables() {
        input = findViewById(R.id.activity_edit_text_domain_input);
        submit = findViewById(R.id.activity_button_domain_submit);
        error = findViewById(R.id.activity_text_view_domain_error);
    }

    private void setupSubmitButton() {
        submit.setOnClickListener(v -> {
            String domain = getInputDomain();
            if (domain == null) {
                raiseError(getString(R.string.activity_domain_input_error_invalid));
                resetField();
                return;
            }
            startLogin(domain);
        });
    }

    private String getInputDomain() {
        if (input.getText() == null) {
            return null;
        }
        String domain = input.getText().toString();

        if (TextValidateUtil.isHostName(domain)) {
            return domain;
        }

        return null;
    }

    private void raiseError(String message) {
        error.setText(message);
    }

    private void resetField() {
        if (input.getText() == null) {
            return;
        }
        input.getText().clear();
    }

    private void startLogin(String domain) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_INSTANCE_DOMAIN, domain);
        startActivity(intent);
        finish();
    }
}
