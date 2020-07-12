package com.example.constaflux2.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface WorkerAssistedInjectFactory {
    fun create(appContext: Context, workerParams: WorkerParameters): ListenableWorker
}