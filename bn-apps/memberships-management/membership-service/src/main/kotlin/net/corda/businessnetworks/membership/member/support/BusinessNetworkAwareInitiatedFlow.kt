package net.corda.businessnetworks.membership.member.support

import co.paralleluniverse.fibers.Suspendable
import net.corda.businessnetworks.membership.common.NotAMemberException
import net.corda.businessnetworks.membership.member.GetMembershipsFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.identity.Party

abstract class BusinessNetworkAwareInitiatedFlow<out T>(val flowSession: FlowSession) : FlowLogic<T>() {

    @Suspendable
    override fun call(): T {
        verifyMembership(flowSession.counterparty)
        return onOtherPartyMembershipVerified()
    }

    @Suspendable
    abstract fun onOtherPartyMembershipVerified() : T

    @Suspendable
    private fun verifyMembership(initiator : Party) {
        val memberships = subFlow(GetMembershipsFlow())
        if(memberships[initiator] == null) {
            throw NotAMemberException(initiator)
        }
    }
}

