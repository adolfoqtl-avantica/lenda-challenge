package com.lenda.challenge.spring;

import com.google.common.collect.Lists;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.List;
import java.util.Set;

/**
 * Clear Mongo database after each test.
 */
public class MongoIntegrationTestExecutionListener extends AbstractTestExecutionListener {

    private static final List<String> SKIP_COLLECTIONS = Lists.newArrayList("system.indexes");

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {

        // Clear Mongo database
        MongoTemplate mongoTemplate = testContext.getApplicationContext().getBean(MongoTemplate.class);
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        for (String collectionName : collectionNames) {
            if (SKIP_COLLECTIONS.contains(collectionName)) {
                continue;
            }
            mongoTemplate.dropCollection(collectionName);
        }
    }
}
