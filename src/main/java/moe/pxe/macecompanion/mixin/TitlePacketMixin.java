package moe.pxe.macecompanion.mixin;

import moe.pxe.macecompanion.util.TitleCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class TitlePacketMixin {

    @Inject(method = "setTitleText", at = @At("HEAD"), cancellable = true)
    private void setTitleText(ClientboundSetTitleTextPacket packet, CallbackInfo ci) {
        Minecraft.getInstance().execute(() -> {
            InteractionResult result = TitleCallback.Companion.getEVENT().invoker().setTitleText(packet);

            if (result == InteractionResult.FAIL) {
                ci.cancel();
            }
        });
    }
}

