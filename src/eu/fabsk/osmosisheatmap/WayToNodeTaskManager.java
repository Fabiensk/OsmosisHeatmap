/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fabsk.osmosisheatmap;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityProcessor;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.store.StoreClassRegister;
import org.openstreetmap.osmosis.core.store.StoreWriter;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;

/**
 *
 * @author fab
 */
public class WayToNodeTaskManager implements SinkSource {

    static final class Coord {

        final double lat;
        final double lon;

        public Coord(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    private Sink sink;
    private Map<Long, Coord> idCoordMap = new HashMap<>();
    long idGen = 0xffffffffffl;

    public WayToNodeTaskManager() {
    }

    @Override
    public void process(EntityContainer entityContainer) {
        if (true) {
            sink.process(entityContainer);
        } else {
            Entity entity = entityContainer.getEntity();
            System.out.println(entity.getId() + " " + entity.getClass());
            if (entity instanceof Node) {
                Node node = (Node) entity;
                idCoordMap.put(entity.getId(), new Coord(node.getLatitude(), node.getLongitude()));

            } else if (entity instanceof Way) {
                Way way = (Way) entity;
                Set<Long> idSet = new HashSet<>();
                for (WayNode ref : way.getWayNodes()) {
                    idSet.add(ref.getNodeId());
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
                Node newNode = new Node(new CommonEntityData(idGen, 0, new Date(), OsmUser.NONE, 0), sumLat / cnt, sumLon / cnt);
                ++idGen;
                sink.process(new NodeContainer(newNode));
            }
        }
    }

    @Override
    public void initialize(Map<String, Object> metaData) {
        System.out.println("initialize");
        sink.initialize(metaData);
    }

    @Override
    public void complete() {
        System.out.println("complete");
        sink.complete();
    }

    @Override
    public void release() {
        System.out.println("release");
        sink.release();
    }

    @Override
    public void setSink(Sink sink) {
        System.out.println("setSink");
        this.sink = sink;
    }

}
