package com.example.animemusic.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.animemusic.R;
import com.example.animemusic.api.ApiBuilder;
import com.example.animemusic.models.AppStats;
import com.example.animemusic.models.Config;
import com.example.animemusic.utils.SharedPrefHelper;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ApiBuilder.getInstance().getConfig().enqueue(new Callback<Config>() {
            @Override
            public void onResponse(Call<Config> call, Response<Config> response) {
                SharedPrefHelper.getInstance(SplashActivity.this).saveConfig(response.body());
                AppStats appStats = SharedPrefHelper.getInstance(SplashActivity.this).getAppStats();
                if (appStats == null) {
                    SplashActivity.this.goToMainActivity();
                    return;
                }
                Bundle extras = SplashActivity.this.getIntent().getExtras();
                if (extras != null && extras.getString("type") != null) {
                    SplashActivity.this.goToMainActivity();
                } else if (new Date().getTime() - appStats.getLastTimeInterstitialShow() < 150000 || appStats.getNumberOfAppOpen() <= 2 || appStats.getNumberOfAppOpen() % 3 != 0) {
                    SplashActivity.this.goToMainActivity();
                }

            }

            @Override
            public void onFailure(Call<Config> call, Throwable t) {

            }
        });
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

}