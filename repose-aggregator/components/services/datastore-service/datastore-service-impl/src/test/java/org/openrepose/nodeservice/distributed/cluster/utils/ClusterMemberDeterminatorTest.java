/*
 * _=_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=
 * Repose
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Copyright (C) 2010 - 2015 Rackspace US, Inc.
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=_
 */
package org.openrepose.nodeservice.distributed.cluster.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.openrepose.core.services.datastore.distributed.config.*;
import org.openrepose.core.systemmodel.config.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class ClusterMemberDeterminatorTest {

    public static class WhenDeterminingClusterMembers {

        private SystemModel sysConfig;
        private DistributedDatastoreConfiguration ddConfig;
        private ReposeCluster cluster1, cluster2;
        private Node node1, node2;
        private NodeList nodeList;
        private List<Filter> filters;
        private FilterList filterList;
        private HostAccessControlList hacl;
        private boolean isAllowed;
        private HostAccessControl ctrl;
        private PortConfiguration portConfig;
        private Port node1Port, node2Port;

        @Before
        public void setUp() {

            filters = new ArrayList<Filter>();
            filterList = new FilterList();
            filterList.getFilter().addAll(filters);

            node1 = new Node();
            node1.setHttpPort(8888);
            node1.setHostname("127.0.0.1");
            node1.setId("node1");
            nodeList = new NodeList();
            nodeList.getNode().add(node1);


            node2 = new Node();
            node2.setHttpPort(8889);
            node2.setHostname("127.0.0.1");
            node2.setId("node2");
            nodeList.getNode().add(node2);

            cluster1 = new ReposeCluster();
            cluster1.setFilters(filterList);
            cluster1.setId("reposeCluster");
            cluster1.setNodes(nodeList);

            cluster2 = new ReposeCluster();
            cluster2.setFilters(filterList);
            cluster2.setId("otherReposeCluster");


            sysConfig = new SystemModel();
            sysConfig.getReposeCluster().add(cluster1);

            node1Port = new Port();
            node1Port.setCluster("reposeCluster");
            node1Port.setPort(9999);


            node2Port = new Port();
            node2Port.setCluster("reposeCluster");
            node2Port.setNode("node2");
            node2Port.setPort(3333);

            portConfig = new PortConfiguration();
            portConfig.getPort().add(node1Port);
            portConfig.getPort().add(node2Port);

            isAllowed = false;

            ctrl = new HostAccessControl();
            ctrl.setHost("127.0.0.1");

            hacl = new HostAccessControlList();
            hacl.setAllowAll(isAllowed);
            hacl.getAllow().add(ctrl);


            ddConfig = new DistributedDatastoreConfiguration();
            ddConfig.setAllowedHosts(hacl);
            ddConfig.setPortConfig(portConfig);
        }

        @Test
        public void whenDeterminingCurrentCluster() {

            ReposeCluster getCluster = ClusterMemberDeterminator.getCurrentCluster(sysConfig.getReposeCluster(), "reposeCluster");

            assertThat("should retrieve cluster", getCluster.getId(), equalTo("reposeCluster"));

        }

        @Test
        public void whenRetrievingNonExistantCluster() {

            ReposeCluster getCluster = ClusterMemberDeterminator.getCurrentCluster(sysConfig.getReposeCluster(), "nonExistantCluster");

            assertNull("No cluster retrieved", getCluster);
        }

        @Test
        public void whenRetrievingDDPort() {

            int ddPort = ClusterMemberDeterminator.getNodeDDPort(ddConfig, "reposeCluster", "node1");
            int ddPort2 = ClusterMemberDeterminator.getNodeDDPort(ddConfig, "reposeCluster", "node2");

            assertThat("Should determine proper dd port 1", ddPort, equalTo(9999));
            assertThat("Should determine proper dd port 2", ddPort2, equalTo(3333));

        }

        @Test
        public void whenRetrievingClusterMembers() {

            List<InetSocketAddress> clusterView = ClusterMemberDeterminator.getClusterMembers(sysConfig, ddConfig, "reposeCluster");

            assertThat("Cluster has 2 repose nodes", clusterView.size(), equalTo(2));
        }
    }
}
