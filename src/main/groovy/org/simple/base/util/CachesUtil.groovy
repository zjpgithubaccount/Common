package org.simple.base.util

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class CacheForm {
    Long refreshSecondsAfterWrite
    Long expireSecondsAfterWrite
    Long expireSecondsAfterAccess
    Long maximumSize
    Long maximumWeight
}

class CachesUtil {

    static Cache<String, Cache<Object, Object>> caches = CacheBuilder.newBuilder().build()

    private static Cache<Object, Object> defaultCache = getOrCreateCache("DEFAULT_CACHE")

    static Cache<Object, Object> getOrCreateCache(String group, CacheForm form = null) {
        caches.get(group, {
            createBuilder(form).build()
        } as Callable)
    }

    static LoadingCache<Object, Object> getOrCreateCache(String group,
                                                         CacheLoader<Object, Object> loader,
                                                         CacheForm form) {
        caches.get(group, {
            createBuilder(form).build(loader)
        } as Callable) as LoadingCache
    }

    static Cache<Object, Object> cache() {
        return defaultCache
    }

    static Cache<Object, Object> getCache(String group) {
        caches.getIfPresent(group)
    }

    static <T> T getIfPresent(String group, Object key) {
        (T) getCache(group)?.getIfPresent(key)
    }

    static <T> T get(String group, Object key, Callable valueLoader) {
        get(group, key, valueLoader, null)
    }

    static <T> T get(String group, Object key, Callable valueLoader, CacheForm form) {
        (T) getOrCreateCache(group, form)?.get(key, valueLoader)
    }

    static <T> T getIfPresent(Object key) {
        (T) cache().getIfPresent(key)
    }

    static <T> T getOnceIfPresent(Object key) {
        def value = getIfPresent(key)

        if (value != null) {
            invalidate(key)
        }

        (T) value
    }

    static <T> T getOnceIfPresent(String group, Object key) {
        def value = getIfPresent(group, key)

        if (value != null) {
            invalidate(group, key)
        }

        (T) value
    }

    static <T> T get(Object key, Callable valueLoader) {
        (T) cache().get(key, valueLoader)
    }

    static void put(String group, Object key, Object value, CacheForm form = null) {
        getOrCreateCache(group, form).put(key, value)
    }

    static void put(Object key, Object value) {
        cache().put(key, value)
    }

    static void invalidate(String group, Object key) {
        getCache(group)?.invalidate(key)
    }

    static void invalidate(Object key) {
        cache().invalidate(key)
    }

    static void invalidateAll(String group) {
        getCache(group)?.invalidateAll()
    }

    static Cache<Object, Object> withCache(
            Object obj,
            Object key,
            String group,
            CacheForm form = null) {
        Cache<Object, Object> cache = getOrCreateCache(group, form)
        cache.put(key, obj)
        return cache
    }

    static Cache<Object, Object> withCache(Object obj, Object key) {
        put(key, obj)
        return cache()
    }

    static void clearUp(String group) {
        getCache(group)?.cleanUp()
    }

    static void clearUp() {
        cache().cleanUp()
    }

    private static CacheBuilder<Object, Object> createBuilder(CacheForm form) {
        def builder = CacheBuilder.newBuilder()
        form?.with {
            if (maximumSize != null) {
                builder.maximumSize(maximumSize)
            }

            if (maximumWeight != null) {
                builder.maximumWeight(maximumWeight)
            }

            if (expireSecondsAfterAccess != null) {
                builder.expireAfterAccess(expireSecondsAfterAccess, TimeUnit.SECONDS)
            }

            if (expireSecondsAfterWrite != null) {
                builder.expireAfterWrite(expireSecondsAfterWrite, TimeUnit.SECONDS)
            }

            if (refreshSecondsAfterWrite != null) {
                builder.refreshAfterWrite(refreshSecondsAfterWrite, TimeUnit.SECONDS)
            }

            return null
        }

        return builder
    }
}
