package de.gimik.apps.gpstracker.backend.service.impl;

import org.springframework.stereotype.Service;

import de.gimik.apps.gpstracker.backend.service.ThreadingService;

import javax.annotation.PostConstruct;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ThreadingServiceImpl implements ThreadingService {

    private static final int DEFAULT_POOL_SIZE = 10;

    private ExecutorService threadingPool;

    @PostConstruct
    public void init() {
        threadingPool = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
    }

    @Override
    public void execute(Runnable thread) {
        threadingPool.execute(thread);
    }

    @Override
    public void executeIgnoreError(Runnable thread) {
        try {
            threadingPool.execute(thread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
