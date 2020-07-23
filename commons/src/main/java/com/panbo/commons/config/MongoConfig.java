package com.panbo.commons.config;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * @author PanBo 2020/7/22 16:02
 */
@Configuration
public class MongoConfig {
    @Bean
    public GridFSBucket gridFSBucket(@Autowired MongoDbFactory mongoDbFactory){
        return GridFSBuckets.create(mongoDbFactory.getDb());
    }

}
