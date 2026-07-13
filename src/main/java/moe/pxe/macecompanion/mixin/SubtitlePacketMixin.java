package moe.pxe.macecompanion.mixin;

import moe.pxe.macecompanion.util.SubtitleCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class SubtitlePacketMixin {

    @Inject(method = "setSubtitleText", at=@At("HEAD"), cancellable = true)
    private void setSubtitleText(ClientboundSetSubtitleTextPacket packet, CallbackInfo ci) {
        Minecraft.getInstance().execute(() -> {
            InteractionResult result = SubtitleCallback.Companion.getEVENT().invoker().setSubtitleText(packet);

            if (result == InteractionResult.FAIL) {
                ci.cancel();
            }
        });
    }
}
