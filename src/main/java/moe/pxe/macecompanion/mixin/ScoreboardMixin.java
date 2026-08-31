package moe.pxe.macecompanion.mixin;

import com.google.gson.JsonElement;
import dev.isxander.yacl3.config.v3.KotlinExtsKt;
import moe.pxe.macecompanion.config.Config;
import moe.pxe.macecompanion.stateManagers.PlotManager;
import moe.pxe.macecompanion.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(Gui.class)
public class ScoreboardMixin {
    @Unique
    Pattern timePattern = Pattern.compile("in 0:(\\d+)");

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void getAndHideSidebar(final GuiGraphics graphics, final Objective objective, CallbackInfo ci) {
        if (PlotManager.INSTANCE.getOnMaceRoulette() && KotlinExtsKt.getValue(Config.INSTANCE.getDisplayNewRoundInActionbar())) {
            Scoreboard scoreboard = objective.getScoreboard();
            Collection<ScoreHolder> scoreHolders = scoreboard.getTrackedPlayers();
            AtomicInteger timeLeft = new AtomicInteger(-1);
            AtomicInteger mainColor = new AtomicInteger(-1);
            AtomicInteger timeColor = new AtomicInteger(-1);
            StringBuilder type = new StringBuilder();
            scoreHolders.forEach(scoreHolder -> {
                String scoreboardName = scoreHolder.getScoreboardName();
                PlayerTeam team = scoreboard.getPlayersTeam(scoreboardName);
                if (team != null) {
                    String fullLine = team.getPlayerPrefix().getString();
                    JsonElement prefixJson = TextUtils.INSTANCE.messageToJson(team.getPlayerPrefix());
                    Integer parsedTimeColor = TextUtils.INSTANCE.findTextColorInJson(prefixJson, "0:");
                    if (parsedTimeColor != null) timeColor.set(parsedTimeColor);
                    Matcher timeMatcher = timePattern.matcher(fullLine);
                    if (timeMatcher.find()) timeLeft.set(Integer.parseInt(timeMatcher.group(1)));
                    if (fullLine.contains("Round")){
                        type.append("Round");
                        Integer parsedMainColor = TextUtils.INSTANCE.findTextColorInJson(prefixJson, "Round");
                        if (parsedMainColor != null) mainColor.set(parsedMainColor);
                    }
                    if (fullLine.contains("Game")) {
                        type.append("Game");
                        Integer parsedMainColor = TextUtils.INSTANCE.findTextColorInJson(prefixJson, "Game");
                        if (parsedMainColor != null) mainColor.set(parsedMainColor);
                    }
                }
            });
            LocalPlayer player = Minecraft.getInstance().player;
            Component text = TextUtils.INSTANCE.getNewRoundOrGameText(type.toString(), timeLeft.get(), mainColor.get(), timeColor.get());
            if (!text.equals(Component.empty()) && player != null) player.displayClientMessage(text, true);
            ci.cancel();
        }
    }
}
