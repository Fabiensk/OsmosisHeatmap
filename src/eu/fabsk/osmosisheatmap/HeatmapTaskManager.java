/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import java.text.MessageFormat;
import java.util.Formatter;
import java.util.Map;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

/**
 * (Untested) Create
 * @author fab
 */
class HeatmapTaskManager implements Sink {

    public HeatmapTaskManager() {
    }

    @Override
    public void process(EntityContainer ec) {
        Entity entity = ec.getEntity();
        if (entity instanceof Node) {
            Node node = (Node) entity;
            String text = MessageFormat.format("[{0},{1}],\n", node.getLatitude(), node.getLongitude());
        }
    }

    @Override
    public void initialize(Map<String, Object> map) {
    }

    @Override
    public void complete() {
    }

    @Override
    public void release() {
    }
    
}
