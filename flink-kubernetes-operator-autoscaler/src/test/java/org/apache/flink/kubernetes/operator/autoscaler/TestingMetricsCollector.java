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

package org.apache.flink.kubernetes.operator.autoscaler;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.kubernetes.operator.api.AbstractFlinkResource;
import org.apache.flink.kubernetes.operator.autoscaler.metrics.FlinkMetric;
import org.apache.flink.kubernetes.operator.autoscaler.topology.JobTopology;
import org.apache.flink.kubernetes.operator.service.FlinkService;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.rest.messages.job.metrics.AggregatedMetric;

import lombok.Setter;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Testing {@link ScalingMetricCollector} implementation. */
public class TestingMetricsCollector extends ScalingMetricCollector {

    @Setter private JobTopology jobTopology;

    @Setter private Duration testMetricWindowSize;

    @Setter
    private Map<JobVertexID, Map<FlinkMetric, AggregatedMetric>> currentMetrics = new HashMap<>();

    @Setter private Map<JobVertexID, Collection<AggregatedMetric>> metricNames = new HashMap<>();

    public TestingMetricsCollector(JobTopology jobTopology) {
        this.jobTopology = jobTopology;
    }

    @Override
    protected JobTopology queryJobTopology(RestClusterClient<String> restClient, JobID jobId) {
        return jobTopology;
    }

    @Override
    protected Map<JobVertexID, Map<FlinkMetric, AggregatedMetric>> queryAllAggregatedMetrics(
            AbstractFlinkResource<?, ?> cr,
            FlinkService flinkService,
            Configuration conf,
            Map<JobVertexID, Map<String, FlinkMetric>> filteredVertexMetricNames) {
        return currentMetrics;
    }

    @Override
    protected Map<JobVertexID, Map<String, FlinkMetric>> queryFilteredMetricNames(
            FlinkService flinkService,
            AbstractFlinkResource<?, ?> cr,
            Configuration conf,
            JobTopology topology) {
        return Collections.emptyMap();
    }

    @Override
    protected Collection<AggregatedMetric> queryAggregatedMetricNames(
            RestClusterClient<?> restClient, JobID jobID, JobVertexID jobVertexID) {
        return metricNames.getOrDefault(jobVertexID, Collections.emptyList());
    }

    @Override
    protected Duration getMetricWindowSize(Configuration conf) {
        if (testMetricWindowSize != null) {
            return testMetricWindowSize;
        }
        return super.getMetricWindowSize(conf);
    }
}
