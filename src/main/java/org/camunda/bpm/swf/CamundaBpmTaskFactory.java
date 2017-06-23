package org.camunda.bpm.swf;

import java.util.Map;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.AbstractTaskBuilder;
import org.camunda.bpm.model.bpmn.builder.ServiceTaskBuilder;

public class CamundaBpmTaskFactory extends DefaultTaskFactory
{
    @Override
    public AbstractTaskBuilder buildTask(AbstractFlowNodeBuilder builder, Map<String, Object> taskData)
    {
        final AbstractTaskBuilder taskBuilder = super.buildTask(builder, taskData);

        final String taskType = (String) taskData.get("taskType");
        if (taskType != null && taskType.equals("userTask")) {

        } else {

            String topicName = (String) taskData.get("topic");
            Integer priority = (Integer) taskData.get("priority");
            ServiceTaskBuilder serviceTaskBuilder = (ServiceTaskBuilder) taskBuilder;
            serviceTaskBuilder.camundaTopic(topicName);
            if (priority != null) {
                serviceTaskBuilder.camundaTaskPriority(priority.toString());
            }
            serviceTaskBuilder.camundaType("external");
        }
        return taskBuilder;
    }
}
