package org.camunda.bpm.swf.plugin;

import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.persistence.deploy.Deployer;

/**
 * @author Svetlana Dorokhova.
 */
public class SimpleWorkflowProcessEnginePlugin extends AbstractProcessEnginePlugin {

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    super.preInit(processEngineConfiguration);
    final List<Deployer> customPostDeployers = processEngineConfiguration.getCustomPostDeployers();
    if (customPostDeployers == null) {
      processEngineConfiguration.setCustomPostDeployers(new ArrayList<Deployer>());
    }
    processEngineConfiguration.getCustomPostDeployers().add(new YamlBpmnDeployer());
  }
}
