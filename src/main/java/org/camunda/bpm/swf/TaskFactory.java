package org.camunda.bpm.swf;

import java.util.Map;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

public interface TaskFactory {

    public AbstractFlowNodeBuilder buildTask(AbstractFlowNodeBuilder builder, Map<String, Object> taskData);
}
