package com.example.healthservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.index.IndexInfo;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.healthservice.repository")
public class MongoConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;

    public MongoConfig(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createIndexes();
    }

    private void createIndexes() {
        // Get all persistent entities
        for (MongoPersistentEntity<?> entity : mongoMappingContext.getPersistentEntities()) {
            Class<?> type = entity.getType();
            if (type.isAnnotationPresent(Document.class)) {
                IndexOperations indexOps = mongoTemplate.indexOps(type);
                IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
                resolver.resolveIndexFor(type).forEach(indexOps::ensureIndex);
            }
        }

        // Explicitly create geospatial indexes for collections that need them
        createGeoSpatialIndex("bloodBanks", "location");
        createGeoSpatialIndex("labs", "location");
    }

    private void createGeoSpatialIndex(String collectionName, String fieldName) {
        try {
            IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
            List<IndexInfo> existingIndexes = indexOps.getIndexInfo();
            
            // Check if a geospatial index already exists
            boolean geoSpatialIndexExists = existingIndexes.stream()
                .anyMatch(index -> index.getIndexFields().stream()
                    .anyMatch(field -> field.getKey().equals(fieldName)));

            if (!geoSpatialIndexExists) {
                IndexDefinition indexDefinition = new Index()
                    .on(fieldName, Sort.Direction.ASC)
                    .named(fieldName + "_2dsphere")
                    .sparse();
                indexOps.ensureIndex(indexDefinition);
            }
        } catch (Exception e) {
            // Log the error but don't stop the application
            System.err.println("Error creating geospatial index for " + collectionName + "." + fieldName + ": " + e.getMessage());
        }
    }
} 