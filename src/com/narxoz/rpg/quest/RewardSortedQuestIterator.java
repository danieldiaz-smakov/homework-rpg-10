package com.narxoz.rpg.quest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Traverses quests sorted by reward (highest gold first) using a snapshot of the log.
 */
public class RewardSortedQuestIterator implements QuestIterator {

    private final List<Quest> snapshot;
    private int cursor;

    public RewardSortedQuestIterator(QuestLog questLog) {
        this.snapshot = new ArrayList<>(questLog.snapshot());
        this.snapshot.sort(Comparator.comparingInt(Quest::getRewardGold).reversed());
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < snapshot.size();
    }

    @Override
    public Quest next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more quests");
        }
        Quest q = snapshot.get(cursor);
        cursor++;
        return q;
    }
}
