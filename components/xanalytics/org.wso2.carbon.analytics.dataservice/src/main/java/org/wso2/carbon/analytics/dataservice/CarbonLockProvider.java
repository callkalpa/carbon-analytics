/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.analytics.dataservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.datasource.core.AnalyticsLockException;
import org.wso2.carbon.analytics.datasource.core.lock.HazelcastLockProvider;
import org.wso2.carbon.analytics.datasource.core.lock.LocalInMemoryLockProvider;
import org.wso2.carbon.analytics.datasource.core.lock.Lock;
import org.wso2.carbon.analytics.datasource.core.lock.LockProvider;

import com.hazelcast.core.HazelcastInstance;

/**
 * This is a {@link LockProvider} implementation that switches between the {@link LocalInMemoryLockProvider} and 
 * {@link HazelcastLockProvider} based on the server's clustering configuration, i.e. if clustering is disabled,
 * the operations will be delegated to {@link LocalInMemoryLockProvider} or else, to {@link HazelcastLockProvider}.
 */
public class CarbonLockProvider implements LockProvider {
    
    private static final Log log = LogFactory.getLog(CarbonLockProvider.class);
    
    private LockProvider lockProviderDelegate;
    
    public CarbonLockProvider() {
        HazelcastInstance hazelcast = AnalyticsDataServiceComponent.getHazelcastInstance();
        if (hazelcast != null) {
            this.lockProviderDelegate = new HazelcastLockProvider(hazelcast);
        } else {
            this.lockProviderDelegate = new LocalInMemoryLockProvider();
        }
        if (log.isDebugEnabled()) {
            log.debug("CarbonLockProvider initialized with '" + this.lockProviderDelegate.getClass().getName() + "'");
        }
    }
    
    @Override
    public Lock getLock(String name) throws AnalyticsLockException {
        return this.lockProviderDelegate.getLock(name);
    }

    @Override
    public void clearLock(String name) throws AnalyticsLockException {
        this.lockProviderDelegate.clearLock(name);
    }

}
