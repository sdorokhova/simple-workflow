package org.camunda.bpm.swf;

import java.util.Map;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ServiceTaskBuilder;

public class CamundaBpmTaskFactory extends DefaultTaskFactory
{
    @Override
    public ServiceTaskBuilder buildTask(AbstractFlowNodeBuilder builder, Map<String, Object> taskData)
    {
        final ServiceTaskBuilder serviceTaskBuilder = super.buildTask(builder, taskData);

        String topicName = (String) taskData.get("topic");
        Integer priority = (Integer) taskData.get("priority");

        serviceTaskBuilder.camundaTopic(topicName);
        serviceTaskBuilder.camundaTaskPriority(priority.toString());

        return serviceTaskBuilder;
    }
}
