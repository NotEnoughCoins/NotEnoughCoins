package me.mindlessly.notenoughcoins.mixins;

import me.mindlessly.notenoughcoins.hooks.GuiContainerHook;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {

    @Shadow
    public Container inventorySlots;

    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void drawSlot(Slot slotIn, CallbackInfo ci) {
        GuiContainerHook.drawSlot(inventorySlots, slotIn);
    }
}
