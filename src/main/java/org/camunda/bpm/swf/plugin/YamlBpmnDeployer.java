package org.camunda.bpm.swf.plugin;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.bpmn.deployer.BpmnDeployer;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.deploy.Deployer;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.swf.CamundaBpmTaskFactory;
import org.camunda.bpm.swf.Transformer;

/**
 * @author Svetlana Dorokhova.
 */
public class YamlBpmnDeployer extends BpmnDeployer {

  public static final String[] YAML_RESOURCE_SUFFIXES = new String[] { "yaml" };

  private Transformer transformer = new Transformer(new CamundaBpmTaskFactory());

  @Override
  protected String[] getResourcesSuffixes() {
    return YAML_RESOURCE_SUFFIXES;
  }

  @Override
  public void deploy(DeploymentEntity deployment) {
    for (ResourceEntity resource : deployment.getResources().values()) {
      if (isResourceHandled(resource)) {
        byte[] bytes = resource.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        final BpmnModelInstance modelInstance = transformer.transform(inputStream);

        final byte[] modelBytes = Bpmn.convertToString(modelInstance).getBytes();

        resource.setBytes(modelBytes);

        final int i = resource.getName().indexOf(".yaml");
        resource.setName(resource.getName().substring(0, i) + ".bpmn");
      }
    }
    getBpmnDeployer().deploy(deployment);
  }

  public BpmnDeployer getBpmnDeployer() {
    BpmnDeployer bpmnDeployer = null;
    final List<Deployer> deployers = Context.getProcessEngineConfiguration().getDeployers();
    for (Deployer deployer: deployers) {
      if (deployer instanceof BpmnDeployer && !(deployer instanceof YamlBpmnDeployer)) {
        bpmnDeployer = (BpmnDeployer) deployer;
      }
    }
    if (bpmnDeployer == null) {
      throw new ProcessEngineException("No BpmnDeployer was found.");
    }
    return bpmnDeployer;
  }
}
