package ru.oldzoomer.pingtower.settings_manager.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.oldzoomer.pingtower.settings_manager.dto.Setting;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SETTING_PREFIX = "settings:";
    private static final String MODULE_PREFIX = "settings:";
    private static final String RECENT_PREFIX = "settings:recent:";

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheSetting(Setting setting) {
        String key = SETTING_PREFIX + setting.getModule() + ":" + setting.getKey();
        redisTemplate.opsForValue().set(key, setting, 1, TimeUnit.HOURS);
    }

    public Setting getSettingFromCache(String module, String key) {
        String redisKey = SETTING_PREFIX + module + ":" + key;
        return (Setting) redisTemplate.opsForValue().get(redisKey);
    }

    public void cacheModuleSettings(String module, List<Setting> settings) {
        String key = MODULE_PREFIX + module + ":*";
        redisTemplate.opsForValue().set(key, settings, 1, TimeUnit.HOURS);
    }

    public List<Setting> getModuleSettingsFromCache(String module) {
        String key = MODULE_PREFIX + module + ":*";
        return (List<Setting>) redisTemplate.opsForValue().get(key);
    }

    public void cacheRecentSettings(List<Setting> settings) {
        String key = RECENT_PREFIX + "*";
        redisTemplate.opsForValue().set(key, settings, 30, TimeUnit.MINUTES);
    }

    public List<Setting> getRecentSettingsFromCache() {
        String key = RECENT_PREFIX + "*";
        return (List<Setting>) redisTemplate.opsForValue().get(key);
    }

    public void invalidateSettingCache(String module, String key) {
        String redisKey = SETTING_PREFIX + module + ":" + key;
        redisTemplate.delete(redisKey);
    }

    public void invalidateModuleCache(String module) {
        String pattern = MODULE_PREFIX + module + ":*";
        redisTemplate.delete(pattern);
    }

    public void invalidateAllSettingsCache() {
        String pattern = SETTING_PREFIX + "*";
        redisTemplate.delete(pattern);
    }
}