package moe.pxe.macecompanion.mixin;

import moe.pxe.macecompanion.util.SubtitleCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class SubtitlePacketMixin {

    @Inject(method = "onSubtitle", at=@At("HEAD"), cancellable = true)
    private void onSubtitle(SubtitleS2CPacket packet, CallbackInfo ci) {
        MinecraftClient.getInstance().execute(() -> {
            ActionResult result = SubtitleCallback.Companion.getEVENT().invoker().onSubtitle(packet);

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        });
    }
}
