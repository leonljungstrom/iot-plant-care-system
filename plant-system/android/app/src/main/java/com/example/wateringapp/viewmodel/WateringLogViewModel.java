package com.example.wateringapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wateringapp.wateringlog.WateringLog;
import com.example.wateringapp.wateringlog.WateringLogDao;
import com.example.wateringapp.wateringlog.WateringLogDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WateringLogViewModel extends AndroidViewModel {

    private final WateringLogDao dao;
    private final LiveData<List<WateringLog>> last10Logs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WateringLogViewModel(@NonNull Application application) {
        super(application);
        WateringLogDatabase db = WateringLogDatabase.getInstance(application);
        dao = db.wateringLogDao();
        last10Logs = dao.getLast10Logs();
    }

    public LiveData<List<WateringLog>> getLast10Logs() {
        return last10Logs;
    }

    public void addLog(WateringLog log) {
        executor.execute(() -> dao.insert(log));
    }
}
