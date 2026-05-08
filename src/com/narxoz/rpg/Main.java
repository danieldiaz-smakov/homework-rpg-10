package com.narxoz.rpg;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.council.CouncilEngine;
import com.narxoz.rpg.council.CouncilRunResult;
import com.narxoz.rpg.guild.Captain;
import com.narxoz.rpg.guild.GuildHall;
import com.narxoz.rpg.guild.Healer;
import com.narxoz.rpg.guild.Loremaster;
import com.narxoz.rpg.guild.Quartermaster;
import com.narxoz.rpg.guild.Scout;
import com.narxoz.rpg.quest.Quest;
import com.narxoz.rpg.quest.QuestIterator;
import com.narxoz.rpg.quest.QuestLog;
import com.narxoz.rpg.quest.QuestPriority;
import java.util.List;

/**
 * Entry point for Homework 10 — The Adventurers' Guild: Iterator + Mediator.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Homework 10 Demo: Iterator + Mediator ===\n");

        Hero bruno = new Hero("Bruno Ironheart", 120, 40, 18, 14, 80);
        Hero liora = new Hero("Liora Swift", 90, 60, 22, 10, 120);

        QuestLog log = new QuestLog();
        log.add(new Quest("Goblin nests near Millbrook", QuestPriority.LOW, 50, false));
        log.add(new Quest("Escort the merchant caravan", QuestPriority.NORMAL, 120, false));
        log.add(new Quest("Cursed barrow — silent bells", QuestPriority.HIGH, 300, false));
        log.add(new Quest("Dragon smoke on Ridgewind Pass", QuestPriority.URGENT, 900, true));
        log.add(new Quest("Lost heir in the Grey Marshes", QuestPriority.NORMAL, 200, false));

        GuildHall hall = new GuildHall();

        new Quartermaster("Marta Coinvault", hall);
        Scout scout = new Scout("Rin Tallgrass", hall);
        new Healer("Sister Ysolde", hall);
        Captain captain = new Captain("Ser Denric", hall);
        new Loremaster("Archivist Vel", hall);

        System.out.println("--- Open/closed (mediator): Loremaster receives lore routed by topic ---");
        captain.issueOrder("lore", "Need chronicler notes on the cursed barrow.");
        scout.reportRoute("curse", "Strange sigils near Ridgewind — old kingdom?");
        System.out.println();

        CouncilEngine engine = new CouncilEngine();
        CouncilRunResult result = engine.runCouncil(List.of(bruno, liora), log, hall);

        System.out.println("\n--- PriorityQuestIterator (priority at least HIGH) ---");
        QuestIterator urgentOrMajor = log.priorityAtLeast(QuestPriority.HIGH);
        while (urgentOrMajor.hasNext()) {
            System.out.println("  [PriorityQuestIterator] " + urgentOrMajor.next());
        }

        System.out.println("\n--- Open/closed (iterator): RewardSortedQuestIterator by descending gold ---");
        QuestIterator byGold = log.byReward();
        while (byGold.hasNext()) {
            System.out.println("  [RewardSortedQuestIterator] " + byGold.next());
        }

        System.out.println("\n--- Final council metrics ---");
        System.out.println(result);

        System.out.println("\n(Demo: traversal uses custom QuestIterator + snapshot; "
                + "coordination uses GuildHall.dispatch with topic subscribers.)");
    }
}
