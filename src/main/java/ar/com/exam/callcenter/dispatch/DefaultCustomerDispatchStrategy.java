package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.model.Agent;
import ar.com.exam.callcenter.model.AgentType;
import ar.com.exam.callcenter.model.AgentStatus;
import ar.com.exam.callcenter.model.OnHoldIVR;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultCustomerDispatchStrategy implements CustomerDispatchStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCustomerDispatchStrategy.class);

    @Override
    public synchronized Agent findEmployee(Collection<Agent> agentList) {
        Validate.notNull(agentList);
        List<Agent> availableAgents = agentList.stream().filter(agent -> agent.getAgentStatus().equals(AgentStatus.AVAILABLE)).collect(Collectors.toList());

        Optional<Agent> availableAgentOptional = Optional.empty();
        if(!availableAgents.isEmpty()){

            logger.info("Available Agents: " + availableAgents.size());
            availableAgentOptional = availableAgents.stream().filter(agent -> agent.getAgentType().equals(AgentType.OPERATOR)).findAny();
            if (!availableAgentOptional.isPresent()) {
                logger.info("No available operators found");
                availableAgentOptional = availableAgents.stream().filter(agent -> agent.getAgentType().equals(AgentType.SUPERVISOR)).findAny();
            }
            if (!availableAgentOptional.isPresent()) {
                logger.info("No available supervisors found");
                availableAgentOptional = availableAgents.stream().filter(agent -> agent.getAgentType().equals(AgentType.DIRECTOR)).findAny();
            }
            if (!availableAgentOptional.isPresent()) {
                logger.info("No available directors found");
            }
            if(availableAgentOptional.isPresent()){
                logger.info("Agent with role " + availableAgentOptional.get().getAgentType().getDescription() + " found");
            }

        }
        return availableAgentOptional.orElse(null);

    }

    @Override
    public OnHoldIVR findOnHoldIvr(Collection<OnHoldIVR> onHoldIVRs) {
        Validate.notNull(onHoldIVRs);
        List<OnHoldIVR> availableOnHoldIVRs = onHoldIVRs.stream().filter(onHoldIVR -> onHoldIVR.getAgentStatus().equals(AgentStatus.AVAILABLE)).collect(Collectors.toList());

        Optional<OnHoldIVR> availableOnHoldIVROptional = Optional.empty();

        if(!availableOnHoldIVRs.isEmpty()){
            logger.info("Available OnHoldIVRs: " + availableOnHoldIVRs.size());
            availableOnHoldIVROptional = availableOnHoldIVRs.stream().findFirst();
        }
        return availableOnHoldIVROptional.orElse(null);
    }
}
