package com.joe.eyer.task;

import java.io.File;
import java.util.HashMap;

/**
 * Description
 * Created by chenqiao on 2016/10/17.
 */

public class DownloadTaskManager {
    private static HashMap<String, FileDownloadTask> tasks = new HashMap<>();

    public static FileDownloadTask createOrGetTask(String tag, String downloadUrl, File file) {
        if (tasks.get(tag) != null) {
            return tasks.get(tag);
        } else if (file != null) {
            FileDownloadTask task = new FileDownloadTask(downloadUrl, file);
            tasks.put(tag, task);
            return task;
        } else {
            return null;
        }
    }

    public static void removeTask(String tag) {
        if (tasks.get(tag) != null) {
            tasks.get(tag).cancel();
        }
        tasks.remove(tag);
    }
}