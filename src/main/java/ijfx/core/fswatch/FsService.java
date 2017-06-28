package ijfx.core.fswatch;

/**
 * Interface definition for FS services.
 */
public interface FsService {
    /**
     * Starts the service. This method blocks until the service has completely started.
     */
    void start() throws Exception;

    /**
     * Stops the service. This method blocks until the service has completely shut down.
     */
    void stop();
}