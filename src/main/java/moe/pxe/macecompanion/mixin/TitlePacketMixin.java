package moe.pxe.macecompanion.mixin;

import moe.pxe.macecompanion.util.TitleCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class TitlePacketMixin {

    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
        MinecraftClient.getInstance().execute(() -> {
            ActionResult result = TitleCallback.Companion.getEVENT().invoker().onTitle(packet);

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        });
    }
}

