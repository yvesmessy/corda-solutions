package net.corda.businessnetworks.membership.bno.support

import co.paralleluniverse.fibers.Suspendable
import net.corda.businessnetworks.membership.bno.service.DatabaseService
import net.corda.businessnetworks.membership.common.CounterPartyMembershipNotActive
import net.corda.businessnetworks.membership.common.CounterPartyNotAMemberException
import net.corda.core.flows.FlowSession
import net.corda.core.identity.Party

abstract class BusinessNetworkAwareInitiatedFlow<out T>(val flowSession: FlowSession) : BusinessNetworkAwareFlow<T>() {

    @Suspendable
    override fun call(): T {
        verifyMembership(flowSession.counterparty)
        return onOtherPartyMembershipVerified()
    }

    @Suspendable
    abstract fun onOtherPartyMembershipVerified() : T

    @Suspendable
    private fun verifyMembership(initiator : Party) {
        val databaseService = serviceHub.cordaService(DatabaseService::class.java)
        val membership = databaseService.getMembership(initiator)
        if (membership == null) {
            throw CounterPartyNotAMemberException(initiator)
        } else if (!membership.state.data.isActive()) {
            throw CounterPartyMembershipNotActive(initiator)
        }
    }
}

