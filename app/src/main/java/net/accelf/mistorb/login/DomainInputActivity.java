package net.accelf.mistorb.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import net.accelf.mistorb.R;
import net.accelf.mistorb.menu.GlobalMenuHelper;

public class DomainInputActivity extends AppCompatActivity {

    private AppCompatEditText input;
    private Button submit;
    private TextView error;

    public static Intent createIntent(Context context) {
        return new Intent(context, DomainInputActivity.class);
    }

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                error.setText(getString(R.string.activity_domain_input_error_invalid));
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

    private void resetField() {
        if (input.getText() == null) {
            return;
        }
        input.getText().clear();
    }

    private void startLogin(String domain) {
        startActivity(LoginActivity.createIntent(this, domain));
        finish();
    }
}
