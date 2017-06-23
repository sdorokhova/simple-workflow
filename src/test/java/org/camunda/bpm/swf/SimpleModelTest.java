package org.camunda.bpm.swf;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Svetlana Dorokhova.
 */
public class SimpleModelTest {

  public static final String MODEL_FILENAME = "simpleModel.yaml";

  private Transformer transformer;

  @Before
  public void prepare() {
    transformer = new Transformer();
  }

  @Test
  public void testSimpleModel() throws FileNotFoundException {

    final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(MODEL_FILENAME);

    final BpmnModelInstance modelInstance = transformer.transform(inputStream);

    assertNotNull(modelInstance.getModelElementById("OrderProcessing"));

    assertServiceTask(modelInstance, "COLLECT_MONEY");
    assertServiceTask(modelInstance, "FETCH_ITEMS");

    final Collection<SequenceFlow> flows = modelInstance.getModelElementsByType(SequenceFlow.class);

    assertEquals(1, flows.size());
    assertEquals("COLLECT_MONEY", flows.iterator().next().getSource().getId());
    assertEquals("FETCH_ITEMS", flows.iterator().next().getTarget().getId());
  }

  private void assertServiceTask(BpmnModelInstance modelInstance, String taskId) {
    final ModelElementInstance task = modelInstance.getModelElementById(taskId);
    assertNotNull(task);
    assertTrue(task instanceof ServiceTask);
  }

}
