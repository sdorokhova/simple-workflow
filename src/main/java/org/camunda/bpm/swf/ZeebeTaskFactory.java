package org.camunda.bpm.swf;

import java.util.Map;

import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.AbstractTaskBuilder;
import org.camunda.bpm.model.bpmn.builder.ServiceTaskBuilder;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class ZeebeTaskFactory extends DefaultTaskFactory
{

    public static final String TNGP_NAMESPACE = "http://camunda.org/schema/tngp/1.0";

    public static final String IO_MAPPING_ELEMENT = "ioMapping";
    public static final String INPUT_MAPPING_ELEMENT = "input";
    public static final String OUTPUT_MAPPING_ELEMENT = "output";
    public static final String MAPPING_ATTRIBUTE_SOURCE = "source";
    public static final String MAPPING_ATTRIBUTE_TARGET = "target";

    public static final String TASK_DEFINITION_ELEMENT = "taskDefinition";
    public static final String TASK_HEADERS_ELEMENT = "taskHeaders";
    public static final String TASK_HEADER_ELEMENT = "header";

    public static final String TASK_TYPE_ATTRIBUTE = "type";
    public static final String TASK_RETRIES_ATTRIBUTE = "retries";
    public static final String TASK_HEADER_KEY_ATTRIBUTE = "key";
    public static final String TASK_HEADER_VALUE_ATTRIBUTE = "value";


    @Override
    public AbstractTaskBuilder buildTask(AbstractFlowNodeBuilder builder, Map<String, Object> taskData)
    {
        final AbstractTaskBuilder taskBuilder = super.buildTask(builder, taskData);

        if (! (taskBuilder instanceof ServiceTaskBuilder)) {
            throw new RuntimeException("only service tasks are supported");
        }

        ServiceTaskBuilder serviceTaskBuilder = (ServiceTaskBuilder) taskBuilder;

        final String taskType = (String) taskData.get("type");
        final Integer retries = (Integer) taskData.get("retries");

        final ServiceTask serviceTask = serviceTaskBuilder.getElement();
        final ExtensionElements extensionElements = serviceTask.getModelInstance().newInstance(ExtensionElements.class);

        final ModelElementInstance taskDefinition = extensionElements.addExtensionElement(TNGP_NAMESPACE, TASK_DEFINITION_ELEMENT);

        taskDefinition.setAttributeValue(TASK_TYPE_ATTRIBUTE, taskType);

        if (retries != null)
        {
            taskDefinition.setAttributeValue(TASK_RETRIES_ATTRIBUTE, String.valueOf(retries));
        }

        serviceTask.setExtensionElements(extensionElements);

        return serviceTaskBuilder;
    }

}
