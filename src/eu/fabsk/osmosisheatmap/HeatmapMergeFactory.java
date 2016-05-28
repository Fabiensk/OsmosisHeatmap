/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import java.security.InvalidParameterException;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;

/**
 * Create a task for the "Heatmap merge" (see {@see HeatmapMergeTaskManager}).
 *
 * @author fab
 */
class HeatmapMergeFactory extends TaskManagerFactory {

    @Override
    protected TaskManager createTaskManagerImpl(final TaskConfiguration taskConfig) {
        String output = getStringArgument(taskConfig, "output");
        String type = this.getDefaultStringArgument(taskConfig, null);
        if (type == null) {
            throw new InvalidParameterException();
        }
        return new SinkManager(taskConfig.getId(), new HeatmapMergeTaskManager(type, output), taskConfig.getPipeArgs());
    }

}
