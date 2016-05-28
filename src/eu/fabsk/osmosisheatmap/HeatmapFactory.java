/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;

/**
 *
 * @author fab
 */
class HeatmapFactory extends TaskManagerFactory {

    public HeatmapFactory() {
    }

    @Override
    protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
        return new SinkManager(taskConfig.getId(), new HeatmapTaskManager(), taskConfig.getPipeArgs());
    }
    
}
