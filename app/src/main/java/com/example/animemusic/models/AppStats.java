package com.example.animemusic.models;

public class AppStats {
    private int addSongToLocalCounter = 0;
    private long installTime = (System.currentTimeMillis() / 1000);
    private long lastTimeForeground = 0;
    private long lastTimeInterstitialShow = 0;
    private int numberOfAddToPlaylist = 0;
    private int numberOfAppOpen = 0;
    private int numberOfFavorite = 0;
    private int numberOfPaused = 0;
    private int numberOfRateDialogAutoOpen = 0;
    private int numberOfSongShare = 0;
    private int pausedCounter = 0;
    private long rateDialogMinDateToShow = 0;
    private int sharedSongCounter = 0;

    public int getNumberOfAppOpen() {
        return this.numberOfAppOpen;
    }

    public void setNumberOfAppOpen(int i) {
        this.numberOfAppOpen = i;
    }

    public int getNumberOfFavorite() {
        return this.numberOfFavorite;
    }

    public void setNumberOfFavorite(int i) {
        this.numberOfFavorite = i;
    }

    public int getNumberOfAddToPlaylist() {
        return this.numberOfAddToPlaylist;
    }

    public void setNumberOfAddToPlaylist(int i) {
        this.numberOfAddToPlaylist = i;
    }

    public int getNumberOfAddSongToLocal() {
        return this.numberOfAddToPlaylist + this.numberOfFavorite;
    }

    public int getNumberOfPaused() {
        return numberOfPaused;
    }

    public void setNumberOfPaused(int numberOfPaused) {
        this.numberOfPaused = numberOfPaused;
    }

    public int getNumberOfRateDialogAutoOpen() {
        return this.numberOfRateDialogAutoOpen;
    }

    public void setNumberOfRateDialogAutoOpen(int i) {
        this.numberOfRateDialogAutoOpen = i;
    }

    public long getRateDialogMinDateToShow() {
        return this.rateDialogMinDateToShow;
    }

    public void setRateDialogMinDateToShow(long j) {
        this.rateDialogMinDateToShow = j;
    }

    public int getNumberOfSongShare() {
        return this.numberOfSongShare;
    }

    public void setNumberOfSongShare(int i) {
        this.numberOfSongShare = i;
    }

    public int getPausedCounter() {
        return this.pausedCounter;
    }

    public void setPausedCounter(int i) {
        this.pausedCounter = i;
    }

    public int getSharedSongCounter() {
        return this.sharedSongCounter;
    }

    public void setSharedSongCounter(int i) {
        this.sharedSongCounter = i;
    }

    public int getAddSongToLocalCounter() {
        return this.addSongToLocalCounter;
    }

    public void setAddSongToLocalCounter(int i) {
        this.addSongToLocalCounter = i;
    }

    public long getLastTimeInterstitialShow() {
        return this.lastTimeInterstitialShow;
    }

    public void setLastTimeInterstitialShow(long j) {
        this.lastTimeInterstitialShow = j;
    }

    public long getLastTimeForeground() {
        return this.lastTimeForeground;
    }

    public void setLastTimeForeground(long j) {
        this.lastTimeForeground = j;
    }

    public long getInstallTime() {
        return this.installTime;
    }
}
