package org.camunda.bpm.swf;

import java.util.Map;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.AbstractTaskBuilder;

public interface TaskFactory {

    AbstractTaskBuilder buildTask(AbstractFlowNodeBuilder builder, Map<String, Object> taskData);
}
