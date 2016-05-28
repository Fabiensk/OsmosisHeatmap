/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import java.util.HashMap;
import java.util.Map;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkSourceManager;

/**
 *
 * @author fab
 */
final class WayToNodeFactory extends TaskManagerFactory {

    public WayToNodeFactory() {
    }

    @Override
    protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
        //return new SinkManager(taskConfig.getId(), new HeatmapTaskManager(), taskConfig.getPipeArgs());
        return new SinkSourceManager(taskConfig.getId(), new WayToNodeTaskManager(), taskConfig.getPipeArgs());
    }

}
