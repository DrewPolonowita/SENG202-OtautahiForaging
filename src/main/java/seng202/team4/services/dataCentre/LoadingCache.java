package seng202.team4.services.dataCentre;

import seng202.team4.models.CacheStats;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * https://medium.com/@alxkm/concurrenthashmap-for-caching-05c48d1d6bf0
 * https://github.com/alxkm/evictor/tree/master/src/main/java/org/cache
 * Adapted for use with an external database instead of storing the data itself in the concurrent map
 * Class to handle data concurrency issues. This helps stop spam requests for API/Database data
 * run into race conditions.
 * @param <K> This is the key for the data being looked for
 * @param <V> This is the data to be returned
 */
public class LoadingCache<K, V> {

    private final ConcurrentMap<K, Object> locks = new ConcurrentHashMap<>();
    private final Function<K, V> localLoad;
    private final Function<K, V> remoteLoad;

    private long hitCount;
    private long missCount;
    private long remoteLoadCount;

    /**
     * Constructor
     * @param localLoader A function mapping a key to how we get the data from the database
     * @param remoteLoader A function mapping a key to how we get the data from the API
     */
    public LoadingCache(Function<K, V> localLoader, Function<K, V> remoteLoader) {
        this.localLoad = localLoader;
        this.remoteLoad = remoteLoader;
    }

    /**
     * Get something that is cached
     * First try the local database with the provided local function
     * If this fails, we create a lock object linked to the key. This way if multiple threads
     * try to find this, it's connected to the same lock.
     * This allows us to make only one thread try for the api using synchronized
     * @param key The key for the object we are looking for
     * @return the object to be found
     */
    public V get(K key) {
        V cached = localLoad.apply(key);
        if (cached != null) {
            recordHit();
            return cached;
        }

        Object lock = locks.computeIfAbsent(key, ignored -> new Object());
        try {
            synchronized (lock) {
                cached = localLoad.apply(key);
                if (cached != null) {
                    recordHit();
                    return cached;
                }
                recordMiss();
                recordRemoteLoad();
                return remoteLoad.apply(key);
            }
        } finally {
            locks.remove(key, lock);
        }
    }

    /**
     * Get access to repository vs api data
     * @return CacheStats object with data
     */
    public synchronized CacheStats stats() {
        return new CacheStats(hitCount, missCount, remoteLoadCount);
    }

    /**
     * Reset the cache stats count
     */
    public synchronized void resetStats() {
        hitCount = 0;
        missCount = 0;
        remoteLoadCount = 0;
    }

    private synchronized void recordHit() {
        hitCount++;
    }

    private synchronized void recordMiss() {
        missCount++;
    }

    private synchronized void recordRemoteLoad() {
        remoteLoadCount++;
    }



}
