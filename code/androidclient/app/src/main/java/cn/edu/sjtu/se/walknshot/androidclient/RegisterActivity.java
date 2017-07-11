package cn.edu.sjtu.se.walknshot.androidclient;

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
            MyToast.makeText(getApplicationContext(), R.string.error_invalid_blank, Toast.LENGTH_SHORT).show();
        }
        // invalid username
        else if (!Pattern.matches("([a-z][a-z0-9_]{3,30})", username)) {
            MyToast.makeText(getApplicationContext(), R.string.error_invalid_username, Toast.LENGTH_SHORT).show();
        }
        // invalid password
        else if (password.length() < 6) {
            MyToast.makeText(getApplicationContext(), R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
            mPassword.setText("");
            mPasswordConfirm.setText("");
        }
        // password confirming fails
        else if (!passwordConfirm.equals(password)) {
            MyToast.makeText(getApplicationContext(), R.string.error_invalid_password_confrim, Toast.LENGTH_SHORT).show();
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
                        MyToast.makeText(getApplicationContext(), R.string.error_network_fail, Toast.LENGTH_SHORT).show();
                    } else if ("FAIL".equals(status)) {
                        MyToast.makeText(getApplicationContext(), R.string.error_username_exist, Toast.LENGTH_SHORT).show();
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
