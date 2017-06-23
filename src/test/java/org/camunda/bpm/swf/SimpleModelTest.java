package org.camunda.bpm.swf;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.camunda.bpm.model.bpmn.Bpmn;
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
  public static final String MODEL_WITH_CONDITIONS_FILENAME = "conditionsModel.yaml";

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

//    assertEquals(3, flows.size());

    System.out.println(Bpmn.convertToString(modelInstance));

    //TODO assert flows
  }

  private void assertServiceTask(BpmnModelInstance modelInstance, String taskId) {
    final ModelElementInstance task = modelInstance.getModelElementById(taskId);
    assertNotNull(task);
    assertTrue(task instanceof ServiceTask);
  }

  @Test
  public void testSequenceFlowConditions() throws FileNotFoundException {

    final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(MODEL_WITH_CONDITIONS_FILENAME);

    final BpmnModelInstance modelInstance = transformer.transform(inputStream);

    assertNotNull(modelInstance.getModelElementById("OrderProcessing"));

    assertServiceTask(modelInstance, "COLLECT_MONEY");
    assertServiceTask(modelInstance, "FETCH_ITEMS");

    final Collection<SequenceFlow> flows = modelInstance.getModelElementsByType(SequenceFlow.class);

    //    assertEquals(3, flows.size());
    final Iterator<SequenceFlow> modelSequenceFlowIterator = modelInstance.getModelElementsByType(SequenceFlow.class).iterator();
    while (modelSequenceFlowIterator.hasNext()) {
      final SequenceFlow sequenceFlow = modelSequenceFlowIterator.next();
      if (sequenceFlow.getTarget().getId().equals("FETCH_ITEMS")) {
        assertEquals("${expression1}", sequenceFlow.getConditionExpression().getTextContent());
      } else if (sequenceFlow.getTarget().getId().equals("DELIVER_ITEMS")) {
        assertEquals("${expression2}", sequenceFlow.getConditionExpression().getTextContent());
      }

    }

    System.out.println(Bpmn.convertToString(modelInstance));
  }

}
