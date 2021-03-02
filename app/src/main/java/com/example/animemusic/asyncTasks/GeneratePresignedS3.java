package com.example.animemusic.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.animemusic.BuildConfig;

import java.net.URL;
import java.util.Date;


public class GeneratePresignedS3 extends AsyncTask<Context, Integer, URL> {
    private String bucket;
    private long expirationInMillisecond = -1;
    private String key;
    private S3Listener s3Listener;

    public interface S3Listener {
        void onEnd(URL url);

        void onStart();
    }

    public GeneratePresignedS3(String str, S3Listener s3Listener2) {
        this.s3Listener = s3Listener2;
        this.key = str;
    }

    public GeneratePresignedS3(String str, long j, String str2, S3Listener s3Listener2) {
        this.s3Listener = s3Listener2;
        this.key = str;
        this.expirationInMillisecond = j;
        this.bucket = str2;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        s3Listener.onStart();
    }

    @Override
    protected URL doInBackground(Context... contexts) {
        AmazonS3Client amazonS3Client = new AmazonS3Client((AWSCredentialsProvider) new CognitoCachingCredentialsProvider(contexts[0], "us-west-2:0a3dabb2-289f-448b-afc1-051f79be8a63", Regions.US_WEST_2), Region.getRegion(Regions.US_WEST_2));
        Date date = new Date();
        long time = date.getTime();
        long j = this.expirationInMillisecond;
        if (j == -1) {
            date.setTime(time + 604800000);
        } else {
            date.setTime(time + j);
        }
        if (this.bucket == null) {
            this.bucket = "anime-music-2020";
        }
        return amazonS3Client.generatePresignedUrl(new GeneratePresignedUrlRequest(this.bucket, this.key).withMethod(HttpMethod.GET).withExpiration(date));

    }

    @Override
    protected void onPostExecute(URL url) {
        super.onPostExecute(url);
        s3Listener.onEnd(url);
    }
}
