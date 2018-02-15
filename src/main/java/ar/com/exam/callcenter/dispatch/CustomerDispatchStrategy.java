package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.model.Agent;
import ar.com.exam.callcenter.model.OnHoldIVR;

import java.util.Collection;

public interface CustomerDispatchStrategy {
    /**
     * Finds next available Agent
     *
     * @param agentList available Agents
     * @return Next available agent to take on a call, or null if all Agents are busy
     */
    Agent findEmployee(Collection<Agent> agentList);

    /**
     * Finds next available OnHoldIvr
     *
     * @param onHoldIVRs available IVRs
     * @return Next available onHoldIVR to take on a call, or null if all IVRs are busy
     */
    OnHoldIVR findOnHoldIvr(Collection<OnHoldIVR> onHoldIVRs);
}
