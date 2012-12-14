package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.ContentRepository;
import org.apromore.dao.EdgeRepository;
import org.apromore.dao.NodeRepository;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.Expression;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeAttribute;
import org.apromore.dao.model.ObjectAttribute;
import org.apromore.dao.model.ObjectRef;
import org.apromore.dao.model.ObjectRefAttribute;
import org.apromore.dao.model.Resource;
import org.apromore.dao.model.ResourceAttribute;
import org.apromore.dao.model.ResourceRef;
import org.apromore.dao.model.ResourceRefAttribute;
import org.apromore.graph.canonical.CPFExpression;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.CPFObject;
import org.apromore.graph.canonical.CPFObjectReference;
import org.apromore.graph.canonical.CPFResource;
import org.apromore.graph.canonical.CPFResourceReference;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.HumanTypeEnum;
import org.apromore.graph.canonical.ICPFObject;
import org.apromore.graph.canonical.ICPFObjectReference;
import org.apromore.graph.canonical.ICPFResource;
import org.apromore.graph.canonical.ICPFResourceReference;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.graph.canonical.NonHumanTypeEnum;
import org.apromore.graph.canonical.ObjectTypeEnum;
import org.apromore.graph.canonical.ResourceTypeEnum;
import org.apromore.service.GraphService;
import org.apromore.util.FragmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;

/**
 * Implementation of the GraphService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class GraphServiceImpl implements GraphService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphServiceImpl.class);

    private ContentRepository contentRepo;
    private EdgeRepository edgeRepo;
    private NodeRepository nodeRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param contentRepository Content Repository.
     * @param edgeRepository Edge Repository.
     * @param nodeRepository Node repository.
     */
    @Inject
    public GraphServiceImpl(final ContentRepository contentRepository, final EdgeRepository edgeRepository,
            final NodeRepository nodeRepository) {
        contentRepo = contentRepository;
        edgeRepo = edgeRepository;
        nodeRepo = nodeRepository;
    }


    /**
     * @see org.apromore.service.GraphService#getContent(Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Content getContent(final Integer fragmentVersionId) {
        return contentRepo.getContentByFragmentVersion(fragmentVersionId);
    }

    /**
     * @see org.apromore.service.GraphService#getContentIds()
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getContentIds() {
        return nodeRepo.getContentIDs();
    }

    /**
     * @see org.apromore.service.GraphService#getGraph(Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Canonical getGraph(final Integer contentID) {
        Canonical g = new Canonical();
        fillNodes(g, contentID);
        fillEdges(g, contentID);
        return g;
    }

    /**
     * @see org.apromore.service.GraphService#fillNodes(org.apromore.graph.canonical.Canonical, Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillNodes(final Canonical procModelGraph, final Integer contentID) {
        INode v;
        List<Node> nodes = nodeRepo.getNodesByContent(contentID);
        for (Node node : nodes) {
            v = buildNodeByType(node, procModelGraph);
            procModelGraph.addNode((CPFNode) v);
            procModelGraph.setNodeProperty(node.getUri(), Constants.TYPE, FragmentUtil.getType(v));
        }
    }


    /**
     * @see org.apromore.service.GraphService#fillEdges(org.apromore.graph.canonical.Canonical, Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillEdges(final Canonical procModelGraph, final Integer contentID) {
        List<Edge> edges = edgeRepo.getEdgesByContent(contentID);
        for (Edge edge : edges) {
            CPFNode v1 = procModelGraph.getNode(edge.getSourceNode().getUri());
            CPFNode v2 = procModelGraph.getNode(edge.getTargetNode().getUri());
            if (v1 != null && v2 != null) {
                procModelGraph.addEdge(v1, v2);
            } else {
                if (v1 == null && v2 != null) {
                    LOGGER.info("Null source node found for the edge terminating at " + v2.getId() + " = " + v2.getName() + " in content " + contentID);
                }
                if (v2 == null && v1 != null) {
                    LOGGER.info("Null target node found for the edge originating at " + v1.getId() + " = " + v1.getName() + " in content " + contentID);
                }
                if (v1 == null && v2 == null) {
                    LOGGER.info("Null source and target nodes found for an edge in content " + contentID);
                }
            }
        }
    }

    /**
     * @see org.apromore.service.GraphService#fillNodesByFragmentId(org.apromore.graph.canonical.Canonical, Integer)
     * {@inheritDoc}
     */
    @Override
    public void fillNodesByFragmentId(final Canonical procModelGraph, final Integer fragmentID) {
        INode v;
        List<Node> nodes = nodeRepo.getNodesByFragment(fragmentID);
        for (Node node : nodes) {
            v = buildNodeByType(node, procModelGraph);
            procModelGraph.addNode((CPFNode) v);
            procModelGraph.setNodeProperty(node.getUri(), Constants.TYPE, FragmentUtil.getType(v));
        }
    }

    /**
     * @see org.apromore.service.GraphService#fillEdgesByFragmentId(org.apromore.graph.canonical.Canonical, Integer)
     * {@inheritDoc}
     */
    @Override
    public void fillEdgesByFragmentId(final Canonical procModelGraph, final Integer fragmentID) {
        List<Edge> edges = edgeRepo.getEdgesByFragment(fragmentID);
        for (Edge edge : edges) {
            CPFNode v1 = procModelGraph.getNode(edge.getSourceNode().getUri());
            CPFNode v2 = procModelGraph.getNode(edge.getTargetNode().getUri());
            if (v1 != null && v2 != null) {
                procModelGraph.addEdge(v1, v2);
            } else {
                if (v1 == null && v2 != null) {
                    LOGGER.info("Null source node found for the edge terminating at " + v2.getId() + " = " + v2.getName() + " in fragment " + fragmentID);
                }
                if (v2 == null && v1 != null) {
                    LOGGER.info("Null target node found for the edge originating at " + v1.getId() + " = " + v1.getName() + " in fragment " + fragmentID);
                }
                if (v1 == null && v2 == null) {
                    LOGGER.info("Null source and target nodes found for an edge in fragment " + fragmentID);
                }
            }
        }
    }



    /* Build the correct type of Node so we don't loss Information */
    private INode buildNodeByType(final Node node, Canonical canonical) {
        INode result = null;
        if (node.getNodeType() != null) {
            if (node.getNodeType().equals(NodeTypeEnum.MESSAGE)) {
                result = constructMessageNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.TIMER)) {
                result = constructTimerNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.TASK)) {
                result = constructTaskNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.EVENT)) {
                result = constructEventNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.STATE)) {
                result = constructSpecialNode(node, NodeTypeEnum.STATE);
            } else if (node.getNodeType().equals(NodeTypeEnum.ORSPLIT)) {
                result = constructSpecialNode(node, NodeTypeEnum.ORSPLIT);
            } else if (node.getNodeType().equals(NodeTypeEnum.XORSPLIT)) {
                result = constructSpecialNode(node, NodeTypeEnum.XORSPLIT);
            } else if (node.getNodeType().equals(NodeTypeEnum.ANDSPLIT)) {
                result = constructSpecialNode(node, NodeTypeEnum.ANDSPLIT);
            } else if (node.getNodeType().equals(NodeTypeEnum.ORJOIN)) {
                result = constructSpecialNode(node, NodeTypeEnum.ORJOIN);
            } else if (node.getNodeType().equals(NodeTypeEnum.XORJOIN)) {
                result = constructSpecialNode(node, NodeTypeEnum.XORJOIN);
            } else if (node.getNodeType().equals(NodeTypeEnum.ANDJOIN)) {
                result = constructSpecialNode(node, NodeTypeEnum.ANDJOIN);
            } else {
                LOGGER.warn("Unknown Node Type in parsing Node from DB: " + node.getNodeType().value());
            }
        } else {
            result = new CPFNode();
            addNodeDetails(node, result);
        }
        return result;
    }

    /* Populate the Node with the Message Node details. */
    private INode constructMessageNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setNodeType(NodeTypeEnum.MESSAGE);

        addNodeDetails(node, cpfNode);
        addWorkDetails(node, cpfNode, canonical);

        return cpfNode;
    }

    /* Populate the Node with the Event Node details. */
    private INode constructEventNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setNodeType(NodeTypeEnum.EVENT);

        addNodeDetails(node, cpfNode);
        addWorkDetails(node, cpfNode, canonical);

        return cpfNode;
    }

    /* Populate the Node with the Timer Node details. */
    private INode constructTimerNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setNodeType(NodeTypeEnum.TIMER);

        addNodeDetails(node, cpfNode);
        addWorkDetails(node, cpfNode, canonical);

        if (node.getTimeDuration() != null) {
            cpfNode.setTimeDuration(node.getTimeDuration());
        }
//        if (node.getTimeDate() != null) {
//            cpfNode.setTimeDate(node.getTimeDate().toGregorianCalendar());
//        }
        if (node.getTimerExpression() != null) {
            CPFExpression expr = new CPFExpression();
            expr.setDescription(node.getTimerExpression().getDescription());
            expr.setExpression(node.getTimerExpression().getExpression());
            expr.setLanguage(node.getTimerExpression().getLanguage());
            expr.setReturnType(node.getTimerExpression().getReturnType());
            cpfNode.setTimeExpression(expr);
        }

        return cpfNode;
    }

    /* Populate the Node with the Task Node details. */
    private INode constructTaskNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setNodeType(NodeTypeEnum.TASK);

        addNodeDetails(node, cpfNode);
        addWorkDetails(node, cpfNode, canonical);

        if (node.getConfiguration() != null) {
            cpfNode.setConfigurable(node.getConfiguration());
        }
        if (node.getSubProcess() != null) {
            cpfNode.setSubNetId(node.getSubProcess().getId().toString());
            cpfNode.setExternal(true);
        }

        return cpfNode;
    }

    /* Populate the Node with the State Node details. */
    private INode constructSpecialNode(final Node node, final NodeTypeEnum type) {
        INode cpfNode = new CPFNode();
        cpfNode.setNodeType(type);

        addNodeDetails(node, cpfNode);

        return cpfNode;
    }


    /* Add the Node Specific Details to the Node. */
    private void addNodeDetails(final Node node, INode cpfNode) {
        cpfNode.setName(node.getName());
        cpfNode.setId(node.getUri());
        cpfNode.setNetId(node.getNetId());
        cpfNode.setOriginalId(node.getOriginalId());

        addNodeAttributes(node, cpfNode);
    }

    /* Add the Work Node Specific Details to the Node. */
    private void addWorkDetails(final Node node, INode cpfNode, Canonical canonical) {
        if (node.getTeamWork() != null) {
            cpfNode.setTeamWork(node.getTeamWork());
        }
        if (node.getAllocation() != null) {
            cpfNode.setAllocation(node.getAllocation());
        }

        if (node.getResourceDataExpression() != null) {
            CPFExpression resDataExpr = new CPFExpression();
            resDataExpr.setDescription(node.getResourceDataExpression().getDescription());
            resDataExpr.setExpression(node.getResourceDataExpression().getExpression());
            resDataExpr.setLanguage(node.getResourceDataExpression().getLanguage());
            resDataExpr.setReturnType(node.getResourceDataExpression().getReturnType());
            cpfNode.setResourceDataExpr(resDataExpr);
        }

        if (node.getResourceRunExpression() != null) {
            CPFExpression resRunExpr = new CPFExpression();
            resRunExpr.setDescription(node.getResourceRunExpression().getDescription());
            resRunExpr.setExpression(node.getResourceRunExpression().getExpression());
            resRunExpr.setLanguage(node.getResourceRunExpression().getLanguage());
            resRunExpr.setReturnType(node.getResourceRunExpression().getReturnType());
            cpfNode.setResourceDataExpr(resRunExpr);
        }

        addCancelNodes(node, cpfNode);
        addCancelEdges(node, cpfNode);
        addInputExpression(node, cpfNode);
        addOutputExpression(node, cpfNode);
        addObjects(node, cpfNode, canonical);
        addResources(node, cpfNode, canonical);
    }


    /* Adds the Input Expressions to the Node */
    private void addInputExpression(final Node node, INode cpfNode) {
        CPFExpression input;
        for (Expression inExpr : node.getInputExpressions()) {
            input = new CPFExpression();
            input.setExpression(inExpr.getExpression());
            input.setLanguage(inExpr.getLanguage());
            input.setDescription(inExpr.getDescription());
            input.setReturnType(inExpr.getReturnType());
            cpfNode.addInputExpr(input);
        }
    }

    /* Adds the Input Expressions to the Node */
    private void addOutputExpression(final Node node, INode cpfNode) {
        CPFExpression output;
        for (Expression outExpr : node.getOutputExpressions()) {
            output = new CPFExpression();
            output.setExpression(outExpr.getExpression());
            output.setLanguage(outExpr.getLanguage());
            output.setDescription(outExpr.getDescription());
            output.setReturnType(outExpr.getReturnType());
            cpfNode.addOutputExpr(output);
        }
    }

    /* Add the Cancel Nodes. */
    private void addCancelNodes(final Node node, INode cpfNode) {
        if (node.getCancelNodes() != null) {
            for (Node cancelNode : node.getCancelNodes()) {
                cpfNode.getCancelNodes().add(cancelNode.getUri());
            }
        }
    }

    /* Add the Cancel Edges. */
    private void addCancelEdges(final Node node, INode cpfNode) {
        if (node.getCancelEdges() != null) {
            for (Edge cancelEdge : node.getCancelEdges()) {
                cpfNode.getCancelEdges().add(cancelEdge.getUri());
            }
        }
    }

    /* Add the Attributes to the Node. */
    private void addNodeAttributes(final Node node, INode cpfNode) {
        if (node.getAttributes() != null) {
            for (NodeAttribute n : node.getAttributes()) {
                cpfNode.addAttribute(n.getName(), n.getValue());
            }
        }
    }


    /* Add Objects to the Graph, Both to the Objects and Object references. */
    private void addObjects(final Node node, INode cpfNode, Canonical canonical) {
        addObjectReferences(node, cpfNode);

        if (node.getObjectRefs() != null) {
            ICPFObject cpfObject;
            for (ObjectRef objectRef : node.getObjectRefs()) {
                org.apromore.dao.model.Object object = objectRef.getObject();

                if (!canonicalContainsObject(canonical.getObjects(), object)) {
                    cpfObject = new CPFObject();
                    cpfObject.setName(object.getName());
                    cpfObject.setNetId(object.getNetId());
                    cpfObject.setOriginalId(object.getUri());
                    if (object.getUri() != null) {
                        cpfObject.setId(object.getUri());
                    } else {
                        cpfObject.setId(UUID.randomUUID().toString());
                    }
                    if (object.getConfigurable() != null) {
                        cpfObject.setConfigurable(object.getConfigurable());
                    }
                    if (object.getType() != null) {
                        cpfObject.setObjectType(object.getType());
                        if (object.getType().equals(ObjectTypeEnum.SOFT)) {
                            cpfObject.setSoftType(object.getSoftType());
                        }
                    }

                    for (ObjectAttribute attrib : object.getObjectAttributes()) {
                        // TODO: cpfObject.setAttribute(attrib.getName(), attrib.getValue(), attrib.getAny());
                        cpfObject.setAttribute(attrib.getName(), attrib.getValue());
                    }

                    canonical.addObject(cpfObject);
                }
            }
        }
    }

    /* Add Resources to the Graph, Both to the Resources and Resource references. */
    private void addResources(final Node node, INode cpfNode, Canonical canonical) {
        addResourceReferences(node, cpfNode);

        if (node.getResourceRefs() != null) {
            ICPFResource cpfResource;
            for (ResourceRef resourceRef : node.getResourceRefs()) {
                Resource resource = resourceRef.getResource();

                if (!canonicalContainsResource(canonical.getResources(), resource)) {
                    cpfResource = new CPFResource();
                    cpfResource.setName(resource.getName());
                    cpfResource.setOriginalId(resource.getOriginalId());
                    if (resource.getUri() != null) {
                        cpfResource.setId(resource.getUri().toString());
                    } else {
                        cpfResource.setId(UUID.randomUUID().toString());
                    }
                    if (resource.getConfigurable() != null) {
                        cpfResource.setConfigurable(resource.getConfigurable());
                    }
                    if (resource.getType() != null) {
                        cpfResource.setResourceType(resource.getType());
                        if (resource.getType().equals(ResourceTypeEnum.HUMAN)) {
                            cpfResource.setHumanType(HumanTypeEnum.fromValue(resource.getTypeName()));
                        } if (resource.getType().equals(ResourceTypeEnum.NONHUMAN)) {
                            cpfResource.setNonHumanType(NonHumanTypeEnum.fromValue(resource.getTypeName()));
                        }
                    }
                    for (Resource specialRes : resource.getSpecialisations()) {
                        cpfResource.getSpecializationIds().add(specialRes.getUri());
                    }
                    for (ResourceAttribute attrib : resource.getResourceAttributes()) {
                        // TODO: cpfResource.setAttribute(attrib.getName(), attrib.getValue(), attrib.getAny());
                        cpfResource.setAttribute(attrib.getName(), attrib.getValue());
                    }

                    canonical.addResource(cpfResource);
                }
            }
        }
    }

    private void addObjectReferences(final Node node, INode cpfNode) {
        ICPFObjectReference objectReference;
        if (node.getObjectRefs() != null) {
            for (ObjectRef objectRef : node.getObjectRefs()) {
                objectReference = new CPFObjectReference();
                if (objectRef.getId() != null) {
                    objectReference.setId(objectRef.getId().toString());
                } else {
                    objectReference.setId(UUID.randomUUID().toString());
                }
                objectReference.setObjectId(objectRef.getObject().getUri());
                objectReference.setOptional(objectRef.getOptional());
                objectReference.setConsumed(objectRef.getConsumed());
                objectReference.setObjectRefType(objectRef.getType());

                for (ObjectRefAttribute type : objectRef.getObjectRefAttributes()) {
                    // TODO: objectReference.setAttribute(type.getName(), type.getValue(), type.getAny());
                    objectReference.setAttribute(type.getName(), type.getValue());
                }

                cpfNode.addObjectReference(objectReference);
            }
        }
    }

    private void addResourceReferences(final Node node, INode cpfNode) {
        ICPFResourceReference resourceReference;
        if (node.getResourceRefs() != null) {
            for (ResourceRef resourceRef : node.getResourceRefs()) {
                resourceReference = new CPFResourceReference();
                if (resourceRef.getId() != null) {
                    resourceReference.setId(resourceRef.getId().toString());
                } else {
                    resourceReference.setId(UUID.randomUUID().toString());
                }
                resourceReference.setResourceId(resourceRef.getResource().getUri());
                resourceReference.setQualifier(resourceRef.getQualifier());

                for (ResourceRefAttribute type : resourceRef.getResourceRefAttributes()) {
                    // TODO: resourceReference.setAttribute(type.getName(), type.getValue(), type.getAny());
                    resourceReference.setAttribute(type.getName(), type.getValue());
                }

                cpfNode.addResourceReference(resourceReference);
            }
        }
    }


    /* See if the Resource already exists in the Canonical Graph. Don't add duplicates. */
    private boolean canonicalContainsResource(Set<ICPFResource> resources, Resource resource) {
        boolean result = false;
        for (ICPFResource cpfResource : resources) {
            if (cpfResource.getId().equals(resource.getUri())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /* See if the Resource already exists in the Canonical Graph. Don't add duplicates. */
    private boolean canonicalContainsObject(Set<ICPFObject> objects, org.apromore.dao.model.Object object) {
        boolean result = false;
        for (ICPFObject cpfObject : objects) {
            if (cpfObject.getId().equals(object.getUri())) {
                result = true;
                break;
            }
        }
        return result;
    }

}