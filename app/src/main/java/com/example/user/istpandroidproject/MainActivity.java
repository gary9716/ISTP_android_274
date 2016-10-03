package com.example.user.istpandroidproject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;


public class MainActivity extends CustomizedActivity implements View.OnClickListener, TextView.OnEditorActionListener{

    static final String[] pokemonNames = {
            "小火龍",
            "傑尼龜",
            "妙蛙種子"
    };

    TextView infoText;
    EditText nameText;
    RadioGroup optionsGrp;
    Button confirmBtn;
    ProgressBar progressBar;

    String nameOfTheTrainer;
    int selectedOptionIndex;

    Handler uiHandler;

    public final static String nameOfTheTrainerKey = "nameOfTheTrainer";
    public final static String selectedIndexKey = "selectedIndex";


    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityName = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoText = (TextView)findViewById(R.id.infoText);
        nameText = (EditText)findViewById(R.id.nameText);
        nameText.setOnEditorActionListener(this);
        nameText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        optionsGrp = (RadioGroup)findViewById(R.id.optionsGroup);
        confirmBtn = (Button)findViewById(R.id.confirmButton);
        confirmBtn.setOnClickListener(this);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setIndeterminateDrawable(new CircularProgressDrawable
                .Builder(this)
                .colors(getResources().getIntArray(R.array.gplus_colors))
                .sweepSpeed(1f)
                .strokeWidth(8f)
                .build());

        uiHandler = new Handler(getMainLooper());

        SharedPreferences preferences = getSharedPreferences(
                Application.class.getSimpleName(),
                MODE_PRIVATE);

        nameOfTheTrainer = preferences.getString(nameOfTheTrainerKey, null);
        selectedOptionIndex = preferences.getInt(selectedIndexKey, 0);

        if(nameOfTheTrainer == null) {
            confirmBtn.setVisibility(View.VISIBLE);
            optionsGrp.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.INVISIBLE);
        }
        else {
            confirmBtn.setVisibility(View.INVISIBLE);
            optionsGrp.setVisibility(View.INVISIBLE);
            nameText.setVisibility(View.INVISIBLE);

            progressBar.setVisibility(View.VISIBLE);

            confirmBtn.performClick();
        }

        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton)findViewById(R.id.loginButton);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResult.getAccessToken();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.confirmButton) {
            v.setClickable(false);

            if(nameText.getVisibility() == View.VISIBLE &&
                    optionsGrp.getVisibility() == View.VISIBLE) {
                nameOfTheTrainer = nameText.getText().toString();

                int selectedRadioButtonViewId = optionsGrp.getCheckedRadioButtonId();
                View selectedRadioButton = optionsGrp.findViewById(selectedRadioButtonViewId);
                selectedOptionIndex = optionsGrp.indexOfChild(selectedRadioButton);

                SharedPreferences preferences = getSharedPreferences(
                        Application.class.getSimpleName(),
                        MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(nameOfTheTrainerKey, nameOfTheTrainer);
                editor.putInt(selectedIndexKey, selectedOptionIndex);
                editor.commit();
            }


            String welcomeMessage = String.format(
                    "你好, 訓練家%s 歡迎來到神奇寶貝的世界, 你的第一個夥伴是%s",
                    nameOfTheTrainer,
                    pokemonNames[selectedOptionIndex]);

            infoText.setText(welcomeMessage);

            //execute jumpToNewActivityTask on Main thread after 3 secs
            uiHandler.postDelayed(jumpToNewActivityTask, 3 * 1000);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        confirmBtn.setClickable(true);

    }

    public final static String selectedOptionIndexKey = "selectedOptionIndex";

    Runnable jumpToNewActivityTask = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent();

//            Bundle bundle = new Bundle();
//            bundle.putInt(selectedOptionIndexKey, selectedOptionIndex);
//
//            intent.putExtra("bundle", bundle);

            intent.putExtra(selectedOptionIndexKey, selectedOptionIndex);

            intent.setClass(MainActivity.this, PokemonListActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

    };

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            //dismiss virtual keyboard
            InputMethodManager inm =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            //simulate button clicked
            confirmBtn.performClick();

            return true;
        }

        return false;
    }
}
