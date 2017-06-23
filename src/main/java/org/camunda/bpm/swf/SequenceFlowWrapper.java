package org.camunda.bpm.swf;

import java.util.Map;

/**
 * @author Svetlana Dorokhova.
 */
public class SequenceFlowWrapper {

  private Map<String, Object> sequenceFlowData;

  public SequenceFlowWrapper(Map<String, Object> sequenceFlowData) {
    this.sequenceFlowData = sequenceFlowData;
  }

  public String getFrom() {
    return (String) sequenceFlowData.get("from");
  }

  public String getTo() {
    return (String) sequenceFlowData.get("to");
  }

  public String getCondition() {
    return (String) sequenceFlowData.get("condition");
  }

  public String getField(String fieldName) {
    return (String)sequenceFlowData.get(fieldName);
  }
}
