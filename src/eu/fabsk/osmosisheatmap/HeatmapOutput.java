/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

/**
 * Write entities coordinates to a file.
 * 
 * <p>There will be only one object for a given output filename, which will be shared by several tasks.</p>
 * @author fab
 */
final class HeatmapOutput {

    /** All the output objects by name. */
    static Map<String, HeatmapOutput> allOutputsMap = new HashMap<>();    
    
    /** The file where to write. */
    private final Writer writer;
    /** The clients using this object. */
    private final Set<HeatmapMergeTaskManager> clients = new HashSet<>();
    /** The coordinates of all the nodes that may belong to a way. */
    private final Map<Long, Coord> idCoordMap = new HashMap<>();
    /** Standard output or files? */
    final boolean stdout;

    /** Type of processing. */
    enum Type {
        /** Output directly the node coordinates. */
        NODE,
        /** Output the average coordinates of the nodes of a way. */
        WAY
    }

    /**
     * The coordinates of a point.
     */
    static final class Coord {
        /** Latitude. */
        final double lat;
        /** Longitude. */
        final double lon;

        /**
         * Constructor.
         * @param lat Latitude.
         * @param lon Longitude.
         */
        public Coord(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    /**
     * Get the backend for a given output file.
     * @param filename Name of the file ("-" for stdout).
     * @param client Client requesting the backend.
     * @return The backend.
     * @throws IOException File failed to be opened.
     */
    static synchronized HeatmapOutput getOutput(final String filename, final HeatmapMergeTaskManager client)
            throws IOException {
        HeatmapOutput res = allOutputsMap.get(filename);
        if (res == null) {
            res = new HeatmapOutput(filename);
            allOutputsMap.put(filename, res);
        }
        res.clients.add(client);
        return res;
    }
    /**
     * Constructor.
     *
     * @param filename Name of the output file
     * @throws IOException Could not open output file.
     */
    private HeatmapOutput(final String filename) throws IOException {
        if (filename.equals("-")) {
            this.writer = new OutputStreamWriter(System.out);
            this.stdout = true;
        } else {
            this.writer = new BufferedWriter(new FileWriter(filename));
            this.stdout = false;
        }
        this.writer.write("var heatmap_data = [\n");
    }
    
    /**
     * Called by a client when finished, the last of closing the file.
     * @param client Client having finished.
     */
    synchronized void unregisterClient(final HeatmapMergeTaskManager client) {
        clients.remove(client);
        if (clients.isEmpty()) {
            try {
                this.writer.write("];\n");
                this.writer.close();
            } catch (IOException ex) {
                Logger.getLogger(HeatmapOutput.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Write one node or way into the output.
     *
     * @param entity Node/Way.
     * @param type The type of processing. 'Node' outputs directly the nodes coordinates, and 'Way' output the average
     * coordinates of the way.
     */
    synchronized void process(final Entity entity, final Type type) throws IOException {
        if (type == Type.WAY) {
            // Way => output the middle of the way.
            if (entity instanceof Node) {
                // If node, keep track of its coordinates for the moment which will be used by a way.
                Node node = (Node) entity;
                idCoordMap.put(entity.getId(), new Coord(node.getLatitude(), node.getLongitude()));

            } else if (entity instanceof Way) {
                // If way, find all the related nodes and make the average coordinates (which does not work on the
                // 180th meridian).
                Way way = (Way) entity;
                Set<Long> idSet = new HashSet<>();
                for (WayNode nodeRefId : way.getWayNodes()) {
                    idSet.add(nodeRefId.getNodeId());
                }
                double sumLat = 0;
                double sumLon = 0;
                int cnt = 0;
                for (Long id : idSet) {
                    Coord refCoord = idCoordMap.get(id);
                    if (refCoord != null) {
                        ++cnt;
                        sumLat += refCoord.lat;
                        sumLon += refCoord.lon;
                    }
                }
                if (cnt > 0) {
                    printCoord(sumLat / cnt, sumLon / cnt);
                }
            }
        } else if (type == Type.NODE) {
            if (entity instanceof Node) {
                Node node = (Node) entity;
                printCoord(node.getLatitude(), node.getLongitude());
            }
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Write to file.
     * @param lat Latitude.
     * @param lon Longitude.
     * @throws IOException Output exception.
     */
    private void printCoord(double lat, double lon) throws IOException {
        String text = MessageFormat.format("[{0},{1}],\n", String.valueOf(lat), String.valueOf(lon));
        this.writer.write(text);
    }
}
