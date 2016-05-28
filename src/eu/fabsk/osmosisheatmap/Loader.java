/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import java.util.HashMap;
import java.util.Map;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.plugin.PluginLoader;

/**
 * This the entry point of the plugin, which declare the types of tasks available.
 * @author fab
 */
public final class Loader implements PluginLoader {

    @Override
    public Map<String, TaskManagerFactory> loadTaskFactories() {
        HashMap<String, TaskManagerFactory> map = new HashMap<>();
        map.put("waytonode", new WayToNodeFactory());
        map.put("heatmap", new HeatmapFactory());
        map.put("heatmapmerge", new HeatmapMergeFactory());
        return map;
    }
    
}
