package org.camunda.bpm.swf;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Svetlana Dorokhova.
 */
public class Transformer {

  private TaskFactory taskFactory;

  public Transformer(TaskFactory taskFactory) {
    this.taskFactory = taskFactory;
  }

  public Transformer() {
    this(new DefaultTaskFactory());
  }

  public BpmnModelInstance transform(InputStream inputStream) {

    final Map<String, Object> yaml = (Map<String, Object>) new Yaml().load(new InputStreamReader(inputStream, Charset.forName("utf-8")));

    Map<String, List<String>> incomingFlows = new HashMap<String, List<String>>();
    Map<String, List<String>> outgoingFlows = new HashMap<String, List<String>>();

    List<Map<String, Object>> flow = (List<Map<String, Object>>) yaml.get("flow");

    populateFlowMaps(incomingFlows, outgoingFlows, flow);

    final Map<String, Map<String, Object>> taskMap = getTaskMap(yaml);

    String processId = sanitizeId((String) yaml.get("name"));

    final BpmnModelInstance modelInstance = createBpmnModelInstance(incomingFlows, outgoingFlows, taskMap, processId);

    applySequenceFlowConditions(modelInstance, flow);

    return modelInstance;
  }

  private void populateFlowMaps(Map<String, List<String>> incomingFlows, Map<String, List<String>> outgoingFlows, List<Map<String, Object>> flow) {
    if (flow != null) {
      for (Map<String, Object> sequeceFlow : flow) {
        final String sourceTask = (String) sequeceFlow.get("from");
        final String targetTask = (String) sequeceFlow.get("to");

        List<String> outgoingFlowsForTask = outgoingFlows.get(sourceTask);
        if (outgoingFlowsForTask == null) {
          outgoingFlowsForTask = new ArrayList<String>();
          outgoingFlows.put(sourceTask, outgoingFlowsForTask);
        }
        outgoingFlowsForTask.add(targetTask);

        List<String> incomingFlowsForTask = incomingFlows.get(targetTask);
        if (incomingFlowsForTask == null) {
          incomingFlowsForTask = new ArrayList<String>();
          incomingFlows.put(targetTask, incomingFlowsForTask);
        }
        incomingFlowsForTask.add(sourceTask);
      }
    }
  }

  private BpmnModelInstance createBpmnModelInstance(Map<String, List<String>> incomingFlows, Map<String, List<String>> outgoingFlows,
    Map<String, Map<String, Object>> taskMap, String processId) {
    String firstTaskId = findFirstTask(taskMap.keySet(), incomingFlows);

    AbstractFlowNodeBuilder builder = Bpmn.createExecutableProcess(processId).startEvent();
    builder = taskFactory.buildTask(builder, taskMap.get(firstTaskId));

    builder = buildFlow(firstTaskId, builder, outgoingFlows, taskMap);

    return builder.done();
  }

  private Map<String, Map<String, Object>> getTaskMap(Map<String, Object> yaml) {
    List<Map<String, Object>> tasks = (List<Map<String, Object>>) yaml.get("tasks");

    final Map<String, Map<String, Object>> taskMap = new HashMap<String, Map<String, Object>>();

    if (tasks == null) {
      // throw exception
    }
    for (Map<String, Object> task : tasks) {
      taskMap.put((String) task.get("id"), task);
    }
    return taskMap;
  }

  private void applySequenceFlowConditions(BpmnModelInstance modelInstance, List<Map<String, Object>> flows) {

    if (flows == null) {
      return;
    }

    Map<String, List<SequenceFlowWrapper>> inputSequenceFlows = new HashMap<String, List<SequenceFlowWrapper>>();
    for (Map<String, Object> flow: flows) {
      SequenceFlowWrapper seqFlow = new SequenceFlowWrapper(flow);
      if (inputSequenceFlows.get(seqFlow.getFrom()) == null) {
        inputSequenceFlows.put(seqFlow.getFrom(), new ArrayList<SequenceFlowWrapper>());
      }
      inputSequenceFlows.get(seqFlow.getFrom()).add(seqFlow);
    }

    final Iterator<SequenceFlow> modelSequenceFlowIterator = modelInstance.getModelElementsByType(SequenceFlow.class).iterator();
    while (modelSequenceFlowIterator.hasNext()) {
      final SequenceFlow sequenceFlow = modelSequenceFlowIterator.next();
      final String sourceId = sequenceFlow.getSource().getId();
      if (inputSequenceFlows.get(sourceId) != null) {
        final Iterator<SequenceFlowWrapper> iterator = inputSequenceFlows.get(sourceId).iterator();
        if (iterator.hasNext()) {
          final SequenceFlowWrapper flowDefinition = iterator.next();
          if (flowDefinition.getCondition() != null) {
            final ConditionExpression conditionExpression = modelInstance.newInstance(ConditionExpression.class);
            conditionExpression.setTextContent(flowDefinition.getCondition());
            sequenceFlow.setConditionExpression(conditionExpression);
          }
          iterator.remove();
        }
      }
    }
  }

  private String sanitizeId(String processId) {
    return processId.replaceAll(" ", "");
  }

  private AbstractFlowNodeBuilder buildFlow(String fromTaskId, AbstractFlowNodeBuilder builder, Map<String, List<String>> outgoingFlows, Map<String, Map<String, Object>> taskMap) {
    final List<String> outcomes = outgoingFlows.get(fromTaskId);
    if (outcomes != null)
    {
        for (String toTaskId: outcomes) {
          builder = taskFactory.buildTask(builder, taskMap.get(toTaskId));
          if (outgoingFlows.get(toTaskId) != null) {
            buildFlow(toTaskId, builder, outgoingFlows, taskMap);
          } else {
            builder = builder.endEvent();
          }
          builder = builder.moveToNode(fromTaskId);
        }
    }
    return builder;
  }

  private String findFirstTask(Set<String> taskIds, Map<String, List<String>> incomingFlows) {
    String firstTaskId = null;
    for (String taskId: taskIds) {
      if (incomingFlows.get(taskId) == null) {
        if (firstTaskId == null) {
          firstTaskId = taskId;
        } else {
          //TODO throw exception more than one 1st task
        }
      }
    }
    if (firstTaskId == null) {
      //TODO throw exception no 1st task found
    }
    return firstTaskId;
  }

}
