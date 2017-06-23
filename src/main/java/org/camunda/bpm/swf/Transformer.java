package org.camunda.bpm.swf;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Svetlana Dorokhova.
 */
public class Transformer
{

    public BpmnModelInstance transform(InputStream inputStream)
    {

        final Map<String, Object> yaml = (Map<String, Object>) new Yaml().load(new InputStreamReader(inputStream, Charset.forName("utf-8")));

        Map<String, List<String>> incommingFlows = new HashMap<String, List<String>>();
        Map<String, List<String>> outgoingFlows = new HashMap<String, List<String>>();

        List<Map<String, Object>> flow = (List<Map<String, Object>>) yaml.get("flow");

        if (flow != null)
        {
            for (Map<String, Object> sequeceFlow : flow)
            {
                final String sourceTask = (String) sequeceFlow.get("from");
                final String targetTask = (String) sequeceFlow.get("to");

                List<String> outgoingFlowsForTask = outgoingFlows.get(sourceTask);
                if (outgoingFlowsForTask == null)
                {
                    outgoingFlowsForTask = new ArrayList<String>();
                    outgoingFlows.put(sourceTask, outgoingFlowsForTask);
                }
                outgoingFlowsForTask.add(targetTask);


                List<String> incomingFlowsForTask = incommingFlows.get(targetTask);
                if (incomingFlowsForTask == null)
                {
                    incomingFlowsForTask = new ArrayList<String>();
                    incommingFlows.put(targetTask, incomingFlowsForTask);
                }
                incomingFlowsForTask.add(sourceTask);
            }
        }

        List<Map<String, Object>> tasks = (List<Map<String, Object>>) yaml.get("tasks");

        final Map<String, Map<String, Object>> taskMap = new HashMap<String, Map<String,Object>>();

        if (tasks != null)
        {
            for (Map<String, Object> task : tasks)
            {
                taskMap.put((String) task.get("id"), task);
            }
        }
        else
        {
            // throw exception
        }

        String processId = (String) yaml.get("name");

        AbstractFlowNodeBuilder builder = Bpmn.createExecutableProcess(processId)
            .startEvent();

        builder = builder.endEvent();

        return builder.done();
    }

}
