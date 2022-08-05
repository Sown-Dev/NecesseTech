package techmod.mobs;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class MachinistSettler extends Settler {
    public MachinistSettler() {
        super("machinisthuman");
    }

    public boolean isAvailableForClient(SettlementLevelData settlement, PlayerStats stats) {
        return super.isAvailableForClient(settlement, stats) && stats.mob_kills.getKills("evilsprotector") > 0;
    }

    public GameMessage getAcquireTip() {
        return new LocalMessage("settlement", "foundinvillagetip");
    }

    public void addNewRecruitSettler(SettlementLevelData data, boolean isRandomEvent, TicketSystemList<Supplier<HumanMob>> ticketSystem) {
        if ((isRandomEvent || !this.doesSettlementHaveThisSettler(data)) && data.hasCompletedQuestTier("evilsprotector")) {
            ticketSystem.addObject(110, this.getNewRecruitMob(data));
        }

    }
}
