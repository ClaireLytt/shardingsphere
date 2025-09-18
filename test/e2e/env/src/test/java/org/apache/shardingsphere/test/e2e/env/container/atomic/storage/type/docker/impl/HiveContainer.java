/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.e2e.env.container.atomic.storage.type.docker.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.test.e2e.env.container.atomic.storage.option.StorageContainerConfigurationOption;
import org.apache.shardingsphere.test.e2e.env.container.atomic.storage.type.docker.DockerStorageContainer;

import java.io.IOException;

/**
 * Hive container.
 */
@Slf4j
public final class HiveContainer extends DockerStorageContainer {
    
    public HiveContainer(final String containerImage, final StorageContainerConfigurationOption option, final String scenario) {
        super(containerImage, option, scenario);
    }
    
    @Override
    protected void postStart() {
        super.postStart();
        try {
            executeMountedSQLScripts();
            log.info("Mounted SQL scripts executed successfully");
        } catch (final InterruptedException | IOException ex) {
            log.error("Failed to execute mounted SQL scripts", ex);
        }
        log.info("Hive container postStart completed successfully");
    }
    
    private void executeMountedSQLScripts() throws InterruptedException, IOException {
        execInContainer("bash", "-c",
                "if [ -f /docker-entrypoint-initdb.d/50-scenario-actual-init.sql ]; then beeline -u \"jdbc:hive2://localhost:10000/default\" -f "
                        + "/docker-entrypoint-initdb.d/50-scenario-actual-init.sql; fi");
        execInContainer("bash", "-c",
                "if [ -f /docker-entrypoint-initdb.d/60-scenario-expected-init.sql ]; then beeline -u \"jdbc:hive2://localhost:10000/default\" -f "
                        + "/docker-entrypoint-initdb.d/60-scenario-expected-init.sql; fi");
    }
}
