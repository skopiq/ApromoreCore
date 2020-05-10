/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;

/**
 * Created by napoli on 7/08/14.
 */
public class BpmnEventBasedGateway extends BpmnAbstractGateway{

    public BpmnEventBasedGateway(String tag) {
        super(tag);
    }

    @Override
    public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
        Gateway gateway = diagram.addGateway(name, Gateway.GatewayType.EVENTBASED, lane);
        gateway.getAttributeMap().put("Original id", id);
        id2node.put(id, gateway);
    }

    @Override
    public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
        if (elements.contains(id)) {
            Gateway gateway = diagram.addGateway(name, Gateway.GatewayType.EVENTBASED, lane);
            gateway.getAttributeMap().put("Original id", id);
            id2node.put(id, gateway);
        }
    }
    @Override
    public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, SubProcess subProcess) {
        Gateway gateway = diagram.addGateway(name, Gateway.GatewayType.EVENTBASED, subProcess);
        gateway.getAttributeMap().put("Original id", id);
        id2node.put(id, gateway);
    }

    @Override
    public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, SubProcess subProcess) {
        if (elements.contains(id)) {
            Gateway gateway = diagram.addGateway(name, Gateway.GatewayType.EVENTBASED, subProcess);
            gateway.getAttributeMap().put("Original id", id);
            id2node.put(id, gateway);
        }
    }
}
