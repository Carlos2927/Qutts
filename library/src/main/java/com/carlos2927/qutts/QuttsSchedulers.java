package com.carlos2927.qutts;


import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QuttsSchedulers {
    public static interface Schedulerable{
        void execute(Runnable task);
    }

    public static class AndroidMainThreadScheduler implements Schedulerable{
        public Handler mainHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable task) {
            mainHandler.post(task);
        }
    }
    public static final AndroidMainThreadScheduler AndroidMainThread = new AndroidMainThreadScheduler();
    public static final Schedulerable CurrentThread = new Schedulerable(){
        @Override
        public void execute(Runnable task) {
            task.run();
        }
    };

    public static final Schedulerable WorkThread = new Schedulerable(){
        final ExecutorService executorService = new ThreadPoolExecutor(8, 64, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, "QuttsSchedulers-Worker"){
                    @Override
                    public void run() {
                        setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        super.run();
                    }
                };
                result.setDaemon(false);
                return result;
            }
        });
        @Override
        public void execute(Runnable task) {
            executorService.submit(task);
        }
    };


}
