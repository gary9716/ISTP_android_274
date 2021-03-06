package com.example.user.istpandroidproject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.istpandroidproject.model.OwnedPokemonInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailActivity extends CustomizedActivity {

    Resources mRes;
    String packageName;
    OwnedPokemonInfo mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityName = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mRes = getResources();
        packageName = getPackageName();

        Intent srcIntent = getIntent();
        mData = srcIntent.getParcelableExtra(PokemonListActivity.ownedPokemonInfoKey);

        setView(mData);
    }

    void setView(OwnedPokemonInfo data) {

        ImageView detailImg =
                (ImageView)findViewById(R.id.detail_appearance_img);

        TextView nameText = (TextView)findViewById(R.id.name_text);
        TextView levelText = (TextView)findViewById(R.id.level_text);
        TextView currentHP = (TextView)findViewById(R.id.currentHP_text);
        TextView maxHP = (TextView)findViewById(R.id.maxHP_text);
        TextView type1Text = (TextView)findViewById(R.id.type_1_text);
        TextView type2Text = (TextView)findViewById(R.id.type_2_text);

        TextView[] skillsText = new TextView[OwnedPokemonInfo.maxNumSkills];
        for(int i = 0; i < OwnedPokemonInfo.maxNumSkills;i++) {
            int skillTextId = mRes.getIdentifier(String.format("skill_%d_text", i+1),
                    "id",
                    packageName);
            skillsText[i] = (TextView)findViewById(skillTextId);
        }

        ProgressBar hpBar = (ProgressBar)findViewById(R.id.HP_progressBar);

        String imgUrl = String.format(
                "http://www.csie.ntu.edu.tw/~r03944003/detailImg/%d.png",
                data.getPokemonId());

        ImageLoader.getInstance().displayImage(imgUrl, detailImg);

        nameText.setText(data.getName());
        levelText.setText(String.valueOf(data.getLevel()));
        currentHP.setText(String.valueOf(data.getCurrentHP()));
        maxHP.setText(String.valueOf(data.getMaxHP()));

        if(data.getType1Index() != -1) {
            type1Text.setText(OwnedPokemonInfo.typeNames[data.getType1Index()]);
        }
        else {
            type1Text.setText("");
        }


        if(data.getType2Index() != -1) {
            type2Text.setText(OwnedPokemonInfo.typeNames[data.getType2Index()]);
        }
        else {
            type2Text.setText("");
        }

        for(int i = 0;i < data.getSkills().length;i++) {
            if(data.getSkills()[i] != null) {
                skillsText[i].setText(data.getSkills()[i]);
            }
            else {
                skillsText[i].setText("");
            }
        }

        int hpPercentage = (int)((data.getCurrentHP() / (float) data.getMaxHP()) * 100);
        hpBar.setProgress(hpPercentage);
    }

    //display action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_action_bar, menu);
        return true;
    }

    public final static int levelUp = 1;
    public final static int removeFromList = 2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_save) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(OwnedPokemonInfo.nameKey, mData.getName());
            setResult(removeFromList, returnIntent);
            finish();
            Log.d("testFinish", "here");

            return true;
        }
        else if(itemId == R.id.action_level_up) {

            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

}
