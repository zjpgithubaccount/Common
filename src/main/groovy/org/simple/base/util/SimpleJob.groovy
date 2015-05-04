package org.simple.base.util

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache

class SimpleJob {

    private
    static LoadingCache<String, SimpleJob> jobs = CacheBuilder.newBuilder().build(new CacheLoader<String, SimpleJob>() {
        @Override
        SimpleJob load(String key) throws Exception {
            return new SimpleJob(key: key)
        }
    })

    String key

    Date date

    Long after = 0

    Long interval

    Runnable runnable

    private Timer timer = new Timer()
    private TimerTask timerTask
    private boolean isStart

    static SimpleJob get(String key) {
        return jobs.get(key)
    }

    private void removeJob(String key) {
        jobs.invalidate(key)
    }

    SimpleJob start() {
        // 已经运行，重新加载
        if (isStart) {
            cancel()
        }

        isStart = true
        if (date && interval) {
            timer.schedule(createTimeTask(), date, interval * 1000)
        } else if (date) {
            // 若需要运行的时间点在当前时间之前，则忽略运行
            if (date.before(new Date())) {
                return this
            }

            timer.schedule(createTimeTask(), date)
        } else if (after != null && interval != null) {
            timer.schedule(createTimeTask(), after * 1000, interval * 1000)
        } else if (after != null) {
            timer.schedule(createTimeTask(), after * 1000)
        }

        return this
    }

    TimerTask createTimeTask() {
        timerTask = new TimerTask() {
            @Override
            void run() {
                runnable?.run()
            }
        }

        return timerTask
    }

    SimpleJob cancel() {
        timerTask?.cancel()
        removeJob(this.key)
        timer?.purge()
        return this
    }

    SimpleJob setDate(Date date) {
        this.date = date
        return this
    }

    SimpleJob setAfter(Long after) {
        this.after = after
        return this
    }

    SimpleJob setInterval(Long interval) {
        this.interval = interval
        return this
    }

    SimpleJob setRunnable(Runnable runnable) {
        this.runnable = runnable
        return this
    }

    SimpleJob from(Map<String, Object> map) {
        for (it in map) {
            setProperty(it.key, it.value)
        }

        return this
    }
}
