package org.camunda.bpm.swf;

import java.util.Map;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ServiceTaskBuilder;

@SuppressWarnings("rawtypes")
public class DefaultTaskFactory implements TaskFactory
{
    @Override
    public ServiceTaskBuilder buildTask(AbstractFlowNodeBuilder builder, Map<String, Object> taskData)
    {
        String taskId = (String) taskData.get("id");
        String taskName = (String) taskData.get("name");

        if (taskName == null) {
            taskName = taskId;
        }

        final ServiceTaskBuilder taskBuilder = builder.serviceTask(taskId);

        taskBuilder.name(taskName);

        return taskBuilder;
    }

}
