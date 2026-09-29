package net.bleaktorium.black_tongue.farming.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.MortarAndPestleBlockEntity;
import net.bleaktorium.black_tongue.farming.MortarGrindPacket;
import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID, value = Dist.CLIENT)
public class MortarGrindClientHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.screen != null) return;
        if (!mc.player.getMainHandItem().isEmpty()) return;

        boolean rightHeld = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (!rightHeld) return;

        if (mc.hitResult instanceof BlockHitResult blockHit && blockHit.getType() == HitResult.Type.BLOCK) {
            if (mc.level.getBlockEntity(blockHit.getBlockPos()) instanceof MortarAndPestleBlockEntity) {
                ModMessages.sendToServer(new MortarGrindPacket(blockHit.getBlockPos()));
            }
        }
    }
}