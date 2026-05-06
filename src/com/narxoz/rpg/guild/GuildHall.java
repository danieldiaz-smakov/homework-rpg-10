package com.narxoz.rpg.guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Topic-based mediator for the Adventurers' Guild war council.
 */
public class GuildHall implements GuildMediator {

    private final Map<String, List<GuildMember>> membersByTopic = new HashMap<>();
    private final List<GuildMember> registeredMembers = new ArrayList<>();

    private int dispatchCalls;
    private int memberNotifications;

    /**
     * Registration order; used by orchestration code that must reach concrete roles
     * without storing colleague references outside the mediator setup.
     */
    public List<GuildMember> getRegisteredMembers() {
        return List.copyOf(registeredMembers);
    }

    /**
     * Clears counters before a council run so metrics reflect only that session.
     */
    public void resetMetrics() {
        dispatchCalls = 0;
        memberNotifications = 0;
    }

    public int getDispatchCalls() {
        return dispatchCalls;
    }

    public int getMemberNotifications() {
        return memberNotifications;
    }

    @Override
    public void register(GuildMember member) {
        if (member instanceof Quartermaster) {
            addSubscriber("supplies", member);
            addSubscriber("logistics", member);
            addSubscriber("rewards", member);
        } else if (member instanceof Scout) {
            addSubscriber("recon", member);
            addSubscriber("route", member);
        } else if (member instanceof Healer) {
            addSubscriber("medical", member);
            addSubscriber("casualties", member);
        } else if (member instanceof Captain) {
            addSubscriber("orders", member);
            addSubscriber("tactics", member);
        } else if (member instanceof Loremaster) {
            addSubscriber("lore", member);
            addSubscriber("curse", member);
            addSubscriber("history", member);
        }
        registeredMembers.add(member);
    }

    @Override
    public void dispatch(String topic, GuildMember from, String payload) {
        if (topic == null) {
            return;
        }
        dispatchCalls++;
        for (GuildMember member : subscribersFor(topic)) {
            if (member != from) {
                member.receive(topic, from, payload);
                memberNotifications++;
            }
        }
    }

    protected void addSubscriber(String topic, GuildMember member) {
        membersByTopic.computeIfAbsent(topic, key -> new ArrayList<>()).add(member);
    }

    protected List<GuildMember> subscribersFor(String topic) {
        return membersByTopic.getOrDefault(topic, List.of());
    }
}
