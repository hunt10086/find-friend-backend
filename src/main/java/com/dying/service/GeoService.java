package com.dying.service;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GeoService {

    private final StringRedisTemplate redisTemplate;

    public GeoService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 存储位置
    public void addLocation(String key, String name, double longitude, double latitude) {
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
        geoOps.add(key, new Point(longitude, latitude), name);
    }

    // 计算距离（单位：米）
    public double getDistance(String key, String name1, String name2) {
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
        return geoOps.distance(key, name1, name2, RedisGeoCommands.DistanceUnit.KILOMETERS)
                .getValue();
    }
}