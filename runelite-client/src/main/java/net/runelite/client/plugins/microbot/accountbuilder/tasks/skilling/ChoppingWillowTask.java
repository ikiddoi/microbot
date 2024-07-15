package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.helper.equipment.EquipmentHelper;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.woodcutting.AutoWoodcuttingConfig;
import net.runelite.client.plugins.microbot.woodcutting.AutoWoodcuttingScript;
import net.runelite.client.plugins.microbot.woodcutting.enums.WoodcuttingResetOptions;
import net.runelite.client.plugins.microbot.woodcutting.enums.WoodcuttingTree;

public class ChoppingWillowTask extends AccountBuilderTask {
    AutoWoodcuttingScript script = new AutoWoodcuttingScript();

    AutoWoodcuttingConfig configFiremaking = new AutoWoodcuttingConfig() {
        @Override
        public WoodcuttingTree TREE() {
            return WoodcuttingTree.WILLOW;
        }

        @Override
        public WoodcuttingResetOptions resetOptions() {
            return WoodcuttingResetOptions.FIREMAKE;
        }
    };

    public ChoppingWillowTask(){
        skill = Skill.FIREMAKING;
        minLevel = 30;
        maxLevel = 50;
        memberOnly = false;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 15
                && Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING) >= 30;
    }

    @Override
    public String getName() {
        return "Woodcutting: Willow";
    }

    @Override
    public boolean doTaskPreparations() {
        if (!EquipmentHelper.getBestSkillingEquipment(skill))
            return false;

        if (!Rs2Inventory.hasItem("tinderbox")){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank())
                return false;

            Rs2Bank.depositAll();

            if (Rs2Bank.hasItem("tinderbox"))
                Rs2Bank.withdrawOne("tinderbox");
            else
                cancel();

            return false;
        }

        return Rs2Walker.walkTo(new WorldArea(3081, 3229, 8, 7, 0), 5);
    }

    @Override
    public void run() {
        script.run(configFiremaking);
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        script.shutdown();
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE && chatMessage.getMessage().equals("You can't light a fire here.")) {
            script.cannotLightFire = true;
        }
    }
}
