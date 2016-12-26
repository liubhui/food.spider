package com.food.spider.work;

import com.food.framework.util.DateUtil;
import com.food.framework.work.ITask;
import com.food.framework.work.IWorker;
import com.food.framework.work.WorkState;
import com.food.spider.spider.SpiderHandler;

import java.util.Date;

/**
 * Created by liubihui on 16/12/24.
 */
public class SpiderWorker implements IWorker {

    private int state = 0;

    private Date lastWorkedTime;

    private SpiderTask spiderTask;

    private SpiderHandler spiderHandler = new SpiderHandler();

    private Thread workThread;

    public SpiderWorker() {
        workThread = new WorkThread(this);
        workThread.setName(DateUtil.toString(new Date()));
    }

    @Override
    public boolean isWorking() {
        return WorkState.isWorkingState(state);
    }

    @Override
    public void arrangementWork(ITask task) {
        this.spiderTask = (SpiderTask) task;

    }

    @Override
    public void doWork() {
        if (spiderTask == null) {
            return;
        }

        workThread.start();

    }

    public void work() {
        if (spiderTask == null) {
            return;
        }
        synchronized (this) {
            state = WorkState.WORKING;
        }
        lastWorkedTime = new Date();
        //执行爬虫任务,完成后调整工作人的状态
        spiderHandler.fixOneDistrict(spiderTask.distinctUrlEntry);
        synchronized (this) {
            state = WorkState.COMPLETE;
        }
    }

    @Override
    public Date getLastWorkedTime() {
        return lastWorkedTime;
    }

    @Override
    public int getWorkState() {
        return state;
    }


    public class WorkThread extends Thread {

        SpiderWorker worker;

        public WorkThread(SpiderWorker worker) {
            this.worker = worker;
        }

        public void run() {
            if (worker != null) {
                worker.work();
            }
        }
    }

}
