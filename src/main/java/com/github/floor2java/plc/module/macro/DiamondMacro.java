package com.github.floor2java.plc.module.macro;

import com.github.floor2java.plc.module.macro.enums.DiamondState;
import com.github.floor2java.plc.util.ChatUtils;
import com.github.floor2java.plc.util.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DiamondMacro {

    Minecraft mc;
    static DiamondState state = DiamondState.setup;
    static boolean enable = false;
    BlockPos[] route = {new BlockPos(146.5, 128, 44.5), new BlockPos(153.5, 127, 39.5),
            new BlockPos(148.5, 134, 37.5), new BlockPos(161.5, 141, 38.5), new BlockPos(155.5, 143, 36.5)};

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!isEnable()) return;
        mc = Minecraft.getMinecraft();
        if (state == DiamondState.setup) {
            RotationUtils.smoothLook(RotationUtils.getRotationToBlock(route[0]), 4, rcPacket);
        }
    }

    Runnable rcPacket = new Runnable() {
        @Override
        public void run() {
             mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    route[0],
                    EnumFacing.DOWN));
            ChatUtils.clientMessage("Nwig");
            state = DiamondState.rotating;
        }
    };

    private static void onEnable() {
        state = DiamondState.setup;
        setEnable(true);
    }

    public static void toggle() {
        if (isEnable()) onDisable();
        else onEnable();
    }

    private static void onDisable() {
        setEnable(false);
    }

    public static boolean isEnable() {
        return enable;
    }

    public static void setEnable(boolean enable) {
        DiamondMacro.enable = enable;
    }
}
