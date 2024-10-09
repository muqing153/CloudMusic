package com.muqingbfq.mq;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MusicViewModel extends ViewModel {
    private MutableLiveData<String> imageUrl = new MutableLiveData<>();

    public MutableLiveData<String> getImageUrl() {
        return imageUrl;
    }
}