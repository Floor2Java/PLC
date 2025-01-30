package com.github.floor2java.plc;

import com.github.floor2java.plc.module.macro.DiamondMacro;
import com.github.floor2java.plc.util.RotationUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "plc", useMetadata=true)
public class PLC {

    public static KeyBinding diamondMacro = new KeyBinding("Diamond", Keyboard.KEY_NONE,"! PLC");
    private KeyBinding[] kList = {diamondMacro};

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DiamondMacro());
        MinecraftForge.EVENT_BUS.register(new RotationUtils());

        for (KeyBinding k : kList) {
            ClientRegistry.registerKeyBinding(k);
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e){
        if(diamondMacro.isPressed()){
            DiamondMacro.toggle();
        }
    }
}
