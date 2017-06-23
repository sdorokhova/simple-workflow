package org.camunda.bpm.swf;

import java.util.List;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.swf.plugin.SimpleWorkflowProcessEnginePlugin;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author Svetlana Dorokhova.
 */
public class SimpleWorkflowProcessEnginePluginTest {

  private ProcessEngine processEngine;

  @Before
  public void createEngine() {
    final ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
      .createStandaloneInMemProcessEngineConfiguration();
    processEngineConfiguration
      .getProcessEnginePlugins().add(new SimpleWorkflowProcessEnginePlugin());
    processEngine = processEngineConfiguration.buildProcessEngine();
  }

  @Test
  public void testPlugin() {
    processEngine.getRepositoryService().createDeployment().addClasspathResource("conditionsModel.yaml").deploy();

    assertEquals(1, processEngine.getRepositoryService().createDeploymentQuery().count());
    assertEquals(1, processEngine.getRepositoryService().createProcessDefinitionQuery().count());

    processEngine.getRuntimeService().startProcessInstanceByKey("OrderProcessing", Variables.createVariables().putValue("var", 1));

    List<LockedExternalTask> externalTasks = processEngine.getExternalTaskService().fetchAndLock(1, "worker").topic("collectMoney", 1000).execute();
    assertEquals(1, externalTasks.size());

    processEngine.getExternalTaskService().complete(externalTasks.get(0).getId(), "worker");

    //the process must be moved to FETCH_ITEMS task
    externalTasks = processEngine.getExternalTaskService().fetchAndLock(1, "worker").topic("fetchItems", 1000).execute();
    assertEquals(1, externalTasks.size());

  }

}
