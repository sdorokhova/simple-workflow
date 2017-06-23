package org.camunda.bpm.swf;

import java.util.Map;

import org.camunda.bpm.model.bpmn.builder.AbstractBaseElementBuilder;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.AbstractTaskBuilder;
import org.camunda.bpm.model.bpmn.builder.ServiceTaskBuilder;

@SuppressWarnings("rawtypes")
public class DefaultTaskFactory implements TaskFactory
{
    @Override
    public AbstractTaskBuilder buildTask(AbstractFlowNodeBuilder builder, Map<String, Object> taskData)
    {
        String taskId = (String) taskData.get("id");
        String taskName = (String) taskData.get("name");

        if (taskName == null) {
            taskName = taskId;
        }

        AbstractTaskBuilder taskBuilder = createTaskBuilder(builder, (String)taskData.get("taskType"));
        taskBuilder.id(taskId);
        taskBuilder.name(taskName);

        return taskBuilder;
    }

    public AbstractTaskBuilder createTaskBuilder(AbstractFlowNodeBuilder builder, String taskType) {
        if (taskType != null && taskType == "userTask") {
            return builder.userTask();
        }
        return builder.serviceTask();
    }

}
