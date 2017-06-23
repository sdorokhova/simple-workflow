package org.camunda.bpm.swf;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.*;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.junit.Before;
import org.junit.Test;

public class ZeebeTransformerTest
{

    public static final String MODEL_FILENAME = "/zeebe/task.yaml";

    private Transformer transformer;

    @Before
    public void prepare() {
      transformer = new Transformer(new ZeebeTaskFactory());
    }

    @Test
    public void testSimpleModel() throws FileNotFoundException {

        final BpmnModelInstance modelInstance = transformer.transform(ZeebeTransformerTest.class.getResourceAsStream(MODEL_FILENAME));

        ServiceTask serviceTask = modelInstance.getModelElementById("TASK_1");

        final ExtensionElements extensionElements = serviceTask.getExtensionElements();
        assertNotNull(extensionElements);

        List<ModelElementInstance> elements = new ArrayList<ModelElementInstance>(extensionElements.getElements());
        assertEquals(1, elements.size());

        ModelElementInstance taskDefinition = elements.get(0);
        assertEquals(ZeebeTaskFactory.TNGP_NAMESPACE, taskDefinition.getDomElement().getNamespaceURI().toString());
        assertEquals("someType", taskDefinition.getAttributeValue("type"));
        assertTrue(taskDefinition.getDomElement().hasAttribute("retries"));
        assertEquals("3", taskDefinition.getAttributeValue("retries"));

        ServiceTask serviceTask2 = modelInstance.getModelElementById("TASK_2");

        elements = new ArrayList<ModelElementInstance>(serviceTask2.getExtensionElements().getElements());
        assertEquals(1, elements.size());

        taskDefinition = elements.get(0);
        assertEquals(ZeebeTaskFactory.TNGP_NAMESPACE, taskDefinition.getDomElement().getNamespaceURI().toString());
        assertEquals("someOtherType", taskDefinition.getAttributeValue("type"));
        assertFalse(taskDefinition.getDomElement().hasAttribute("retries"));

    }

}
