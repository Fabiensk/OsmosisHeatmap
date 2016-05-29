/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

/**
 * A kind of task that writes the coordinates of the nodes and ways into a JavaScript file.
 * 
 * <p>Several instances of this task can output to the same file.</p>
 * <p>Parameters:
 * <ul><li>Unnamed parameter: type of processing, either "node" or "way" (for the latter, output the average
 *  coordinates of its members)</li>
 * <li>Named parameter "output": name/path of the JavaScript to be generated</li>
 * </p>
 * @author fab
 */
class HeatmapMergeTaskManager implements Sink {

    /** The output backend for this task (may be shared).*/
    private HeatmapOutput output;
    /** The type of processing (either nodes coordinates or average coordinates of the ways). */
    private HeatmapOutput.Type type;

    /**
     * Constructor.
     * @param type How the entities will be processed: directly output the nodes or output the average position
     *  of the ways.
     * @param outputFilename The output file name.
     */
    HeatmapMergeTaskManager(final String type, final String outputFilename) {
        try {
            this.output = HeatmapOutput.getOutput(outputFilename, this);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to open output file", ex);
        }
        if (type.equals("node")) {
            this.type = HeatmapOutput.Type.NODE;
        } else if (type.equals("way")) {
            this.type = HeatmapOutput.Type.WAY;
        } else {
            throw new InvalidParameterException();
        }
    }

    @Override
    public void process(EntityContainer ec) {
        Entity entity = ec.getEntity();
        try {
            output.process(entity, type);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write", ex);
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
        this.output.unregisterClient(this);
    }
}
