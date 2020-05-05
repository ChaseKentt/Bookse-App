package com.example.bookse.ui.collections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CollectionsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CollectionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is cellections fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}