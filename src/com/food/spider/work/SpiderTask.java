package com.food.spider.work;

import com.food.framework.work.ITask;

import java.util.Map;

/**
 * Created by liubihui on 16/12/24.
 */
public class SpiderTask implements ITask {

    public SpiderTask() {
    }

    public SpiderTask(Map.Entry<String, String> entry) {
        this.distinctUrlEntry = entry;
    }

    public Map.Entry<String, String> distinctUrlEntry;

    public Map.Entry<String, String> getDistinctUrlEntry() {
        return distinctUrlEntry;
    }

    public void setDistinctUrlEntry(Map.Entry<String, String> distinctUrlEntry) {
        this.distinctUrlEntry = distinctUrlEntry;
    }
}
