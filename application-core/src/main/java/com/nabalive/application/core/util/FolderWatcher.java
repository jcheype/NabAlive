package com.nabalive.application.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: mush
 * Date: Dec 16, 2009
 * Time: 10:42:23 PM
 */
public abstract class FolderWatcher implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(FolderWatcher.class);

    private final long interval;
    private final File rootFolder;

    private boolean isRecursive = false;


    private Map<String, Long> lastModifiedMap = new HashMap();

    public FolderWatcher(File rootFolder) {
        this(rootFolder, 5000);
    }

    public FolderWatcher(File rootFolder, long interval) {
        this.interval = interval;
        this.rootFolder = rootFolder;
        if (!rootFolder.isDirectory()) {
            throw new IllegalStateException("Root folder must be a folder");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                logger.debug("scandir");
                scanFolder(rootFolder);
                Thread.sleep(interval);
            }
        } catch (InterruptedException e) {
            logger.info("FileWatcher interrupted");
        }
    }

    private void scanFolder(File folder) {
        Set<String> absolutePathSet = new HashSet<String> (lastModifiedMap.keySet());
        logger.debug("files before: {}", absolutePathSet);
        for (File file : folder.listFiles()) {
            if (!file.getName().startsWith(".")) {
                if (file.isDirectory()) {
                    if (isRecursive) {
                        scanFolder(file);
                    }
                } else {
                    Long lastModified = lastModifiedMap.get(file.getAbsolutePath());
                    if (lastModified == null || lastModified != file.lastModified()) {
                        logger.debug("file modified: {}", file.getAbsolutePath());
                        lastModifiedMap.put(file.getAbsolutePath(), file.lastModified());
                        onChange(file);
                    }
                }
            }
            absolutePathSet.remove(file.getAbsolutePath());
        }

        logger.debug("files after: {}", absolutePathSet);
        for(String absolutePath : absolutePathSet){
            lastModifiedMap.remove(absolutePath);
            onChange(new File(absolutePath));
        }
    }

    protected abstract void onChange(File file);
}