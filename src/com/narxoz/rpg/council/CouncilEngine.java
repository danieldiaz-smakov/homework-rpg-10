package com.narxoz.rpg.council;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.guild.Captain;
import com.narxoz.rpg.guild.GuildHall;
import com.narxoz.rpg.guild.GuildMediator;
import com.narxoz.rpg.guild.GuildMember;
import com.narxoz.rpg.guild.Healer;
import com.narxoz.rpg.guild.Quartermaster;
import com.narxoz.rpg.guild.Scout;
import com.narxoz.rpg.quest.Quest;
import com.narxoz.rpg.quest.QuestIterator;
import com.narxoz.rpg.quest.QuestLog;
import com.narxoz.rpg.quest.QuestPriority;
import java.util.List;

/**
 * Orchestrates a planning session that uses both Iterator and Mediator.
 */
public class CouncilEngine {

    /**
     * Runs the war council using two different quest iterators and mediated guild traffic.
     */
    public CouncilRunResult runCouncil(List<Hero> party, QuestLog questLog, GuildMediator hall) {
        if (!(hall instanceof GuildHall guildHall)) {
            throw new IllegalArgumentException("CouncilEngine requires a GuildHall mediator");
        }
        Captain captain = findMember(guildHall, Captain.class);
        Scout scout = findMember(guildHall, Scout.class);
        Quartermaster quartermaster = findMember(guildHall, Quartermaster.class);
        Healer healer = findMember(guildHall, Healer.class);

        guildHall.resetMetrics();

        int questsTraversed = 0;

        System.out.println("\n--- Iterator pass 1: arrival order (OrderedQuestIterator) ---");
        QuestIterator forward = questLog.ordered();
        while (forward.hasNext()) {
            Quest q = forward.next();
            questsTraversed++;
            System.out.println("  [QuestIterator] " + q);
            printPartyFocus(party, q);
            captain.issueOrder("logistics", "Delegate supplies for: " + q.getTitle());
            scout.reportRoute("tactics", "Intel package for: " + q.getTitle());
            quartermaster.requestSupplies("recon", "Convoy needs eyes on: " + q.getTitle());
            healer.prepareAid("orders", "Sick bay coordination for: " + q.getTitle());
            if (q.getPriority().ordinal() >= QuestPriority.HIGH.ordinal()) {
                captain.issueOrder("medical", "Standby triage — priority " + q.getPriority()
                        + " on " + q.getTitle());
            }
        }

        System.out.println("\n--- Iterator pass 2: reverse arrival (ReverseQuestIterator) ---");
        QuestIterator backward = questLog.reverse();
        while (backward.hasNext()) {
            Quest q = backward.next();
            questsTraversed++;
            System.out.println("  [QuestIterator] " + q);
            captain.issueOrder("medical", "Casualty outlook while revisiting: " + q.getTitle());
            scout.reportRoute("casualties", "Evac paths if things go wrong: " + q.getTitle());
        }

        int messagesRouted = guildHall.getDispatchCalls();
        int membersNotified = guildHall.getMemberNotifications();

        return new CouncilRunResult(questsTraversed, messagesRouted, membersNotified);
    }

    private static <T extends GuildMember> T findMember(GuildHall hall, Class<T> role) {
        for (GuildMember m : hall.getRegisteredMembers()) {
            if (role.isInstance(m)) {
                return role.cast(m);
            }
        }
        throw new IllegalStateException("GuildHall has no registered " + role.getSimpleName());
    }

    private static void printPartyFocus(List<Hero> party, Quest q) {
        if (party == null || party.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder("  [Party] ");
        for (int i = 0; i < party.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(party.get(i).getName());
        }
        sb.append(" review contract: ").append(q.getTitle());
        System.out.println(sb);
    }
}
