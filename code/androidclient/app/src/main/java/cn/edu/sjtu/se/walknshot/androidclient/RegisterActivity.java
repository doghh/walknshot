package cn.edu.sjtu.se.walknshot.androidclient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class RegisterActivity extends MyAppCompatActivity implements OnClickListener {

    TextView mBtnRegister;

    EditText mUsername, mPassword, mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = (EditText) findViewById(R.id.register_input_username);
        mPassword = (EditText) findViewById(R.id.register_input_psw);
        mPasswordConfirm = (EditText) findViewById(R.id.register_input_psw_confirm);
        mBtnRegister = (TextView) findViewById(R.id.register_btn_register);

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString();

        // blank username
        if ("".equals(username) || "".equals(password) || "".equals(passwordConfirm)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_register_fail)
                    .setMessage(R.string.error_blank_info)
                    .create().show();
        }
        // invalid username
        else if (!Pattern.matches("([a-z][a-z0-9_]{3,30})", username)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_register_fail)
                    .setMessage(R.string.error_invalid_username)
                    .create().show();
        }
        // invalid password
        else if (password.length() < 6) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_register_fail)
                    .setMessage(R.string.error_invalid_password)
                    .create().show();
            mPassword.setText("");
            mPasswordConfirm.setText("");
        }
        // password confirming fails
        else if (!passwordConfirm.equals(password)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_register_fail)
                    .setMessage(R.string.error_invalid_password_confrim)
                    .create().show();
            mPassword.setText("");
            mPasswordConfirm.setText("");
        } else {
            Intent intent = new Intent(this, RegisterLoadingActivity.class);
            intent.putExtra("REGISTER_USERNAME", username);
            intent.putExtra("REGISTER_PASSWORD", password);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String status = data.getStringExtra("REGISTER_STATUS");
                    if ("FAIL_NETWORK".equals(status)) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.error_register_fail)
                                .setMessage(R.string.error_network_fail)
                                .create().show();
                    } else if ("FAIL".equals(status)) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.error_register_fail)
                                .setMessage(R.string.error_username_exist)
                                .create().show();
                        mPassword.setText("");
                        mPasswordConfirm.setText("");
                    } else if ("SUCCESS".equals(status)) {
                        finish();
                        Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
    }
}
