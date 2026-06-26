package de.gimik.apps.gpstracker.backend.service;

public interface ThreadingService {
    void execute(Runnable thread);

    void executeIgnoreError(Runnable thread);
}
