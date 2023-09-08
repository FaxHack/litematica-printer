package me.aleksilassila.litematica.printer.v1_20.actions;

import me.aleksilassila.litematica.printer.v1_20.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_20.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_20.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class PrepareAction extends Action {
//    public final Direction lookDirection;
//    public final boolean requireSneaking;
//    public final Item item;

//    public PrepareAction(Direction lookDirection, boolean requireSneaking, Item item) {
//        this.lookDirection = lookDirection;
//        this.requireSneaking = requireSneaking;
//        this.item = item;
//    }
//
//    public PrepareAction(Direction lookDirection, boolean requireSneaking, BlockState requiredState) {
//        this(lookDirection, requireSneaking, requiredState.getBlock().asItem());
//    }

    public final PrinterPlacementContext context;

    public boolean modifyYaw = true;
    public boolean modifyPitch = true;
    public float yaw = 0;
    public float pitch = 0;

    public PrepareAction(PrinterPlacementContext context) {
        this.context = context;

        @Nullable
        Direction lookDirection = context.lookDirection;

        if (lookDirection != null && lookDirection.getAxis().isHorizontal()) {
            this.yaw = lookDirection.asRotation();
        } else {
            this.modifyYaw = false;
        }

        if (lookDirection == Direction.UP) {
            this.pitch = -90;
        } else if (lookDirection == Direction.DOWN) {
            this.pitch = 90;
        } else if (lookDirection != null) {
            this.pitch = 0;
        } else {
            this.modifyPitch = false;
        }
    }

    public PrepareAction(PrinterPlacementContext context, float yaw, float pitch) {
        this.context = context;

        this.yaw = yaw;
        this.pitch = pitch;
    }

    static float[] getNeededRotations(ClientPlayerEntity player, Vec3d vec) {
        Vec3d eyesPos = player.getEyePos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double r = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        double yaw = -Math.atan2(diffX, diffZ) / Math.PI * 180;

        double pitch = -Math.asin(diffY / r) / Math.PI * 180;

//        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
//
//        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
//        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
//        return new float[]{player.getYaw() + MathHelper.wrapDegrees(yaw - player.getYaw()), player.getPitch() + MathHelper.wrapDegrees(pitch - player.getPitch())
//        };
        return new float[]{(float) yaw, (float) pitch};
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float deltaYaw (float yaw1, float yaw2) {
        final float PI_2 = (float) (Math.PI * 2);
        float dYaw = (yaw1 - yaw2) % PI_2;
        if (dYaw < -Math.PI) dYaw += PI_2;
        else if (dYaw > Math.PI) dYaw -= PI_2;

        return dYaw;
    }

    @Override
    public void send(MinecraftClient client, ClientPlayerEntity player) {
        ItemStack itemStack = context.getStack();
        int slot = context.requiredItemSlot;

        if (itemStack != null) {
            PlayerInventory inventory = player.getInventory();

            // This thing is straight from MinecraftClient#doItemPick()
            if (player.getAbilities().creativeMode) {
                inventory.addPickBlock(itemStack);
                client.interactionManager.clickCreativeStack(player.getStackInHand(Hand.MAIN_HAND), 36 + inventory.selectedSlot);
            } else if (slot != -1) {
                if (PlayerInventory.isValidHotbarIndex(slot)) {
                    inventory.selectedSlot = slot;
                } else {
                    client.interactionManager.pickFromInventory(slot);
                }
            }
        }

        if (context.canStealth) {
            float[] targetRot = getNeededRotations(player, context.getHitPos());
            System.out.println("Sending yaw for stealth 1: " + targetRot[0] + ", pitch: " + targetRot[1]);
            PlayerMoveC2SPacket.Full packet = new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), targetRot[0], targetRot[1], player.isOnGround());

            this.yaw = targetRot[0];
            this.pitch = targetRot[1];
            if (PrinterConfig.DEBUG_MODE.getBooleanValue()) {
                player.setYaw(targetRot[0]);
                player.setPitch(targetRot[1]);
            }
            player.networkHandler.sendPacket(packet);
        } else if (modifyPitch || modifyYaw) {
            float yaw = modifyYaw ? this.yaw : player.getYaw();
            float pitch = modifyPitch ? this.pitch : player.getPitch();

            System.out.println("Sending yaw for modified yaw: " + yaw + ", pitch: " + pitch);
            PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), yaw, pitch, player.isOnGround());

            player.networkHandler.sendPacket(packet);
        }

//        if (context.canStealth) {
//            ArrayList<PlayerMoveC2SPacket.Full> packets = new ArrayList<PlayerMoveC2SPacket.Full>();
//            float[] targetRot = getNeededRotations(player, context.getHitPos());
//            float[] lastRot = new float[]{player.getYaw(), player.getPitch()};
//            double maxDeltaYaw = PrinterConfig.INTERPOLATE_LOOK_MAX_ANGLE.getDoubleValue();
//            double maxDeltaPitch = PrinterConfig.INTERPOLATE_LOOK_MAX_ANGLE.getDoubleValue();
//            boolean rotationDone = false;
//            int currentLoop = 0;
//            while (!rotationDone) {
//                if (currentLoop++ > 5) {
//                    System.out.println("To many loops " + currentLoop + " breaking");
//                    break;
//                }
//
//                float yawDelta = deltaYaw(lastRot[0], targetRot[0]);
//                float pitchDelta = Math.abs(targetRot[1] - lastRot[1]);
//                if (PrinterConfig.INTERPOLATE_LOOK.getBooleanValue()) {
//                    rotationDone = true;
//                    if (yawDelta > maxDeltaYaw) {
//                        lastRot[0] += clamp(targetRot[0], (float) -maxDeltaYaw, (float) maxDeltaYaw);
//                        rotationDone = false;
//                    }
//                    if (pitchDelta > maxDeltaPitch) {
//                        lastRot[1] += clamp(targetRot[1], (float) -maxDeltaPitch, (float) maxDeltaPitch);
//                        rotationDone = false;
//                    }
//                } else {
//                    rotationDone = true;
//                }
//
//                System.out.println("Sending yaw for stealth 1: " + lastRot[0] + ", pitch: " + lastRot[1]);
//                PlayerMoveC2SPacket.Full packet = new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), lastRot[0], lastRot[1], player.isOnGround());
//
//                if (PrinterConfig.DEBUG_MODE.getBooleanValue()) {
//                    player.setYaw(lastRot[0]);
//                    player.setPitch(lastRot[1]);
//                }
//                packets.add(packet);
//            }
//
//            for (PlayerMoveC2SPacket.Full packet : packets) {
//                this.yaw = packet.getYaw(player.getYaw());
//                this.pitch = packet.getPitch(player.getPitch());
//                player.networkHandler.sendPacket(packet);
//            }
//
//             PlayerMoveC2SPacket.Full lastPacket = packets.get(packets.size() - 1);
//            float randomnessDistance = 5f;
//            float yawRandomness = (float) (Math.random() * randomnessDistance * 2 - randomnessDistance);
//            float pitchRandomness = (float) (Math.random() * randomnessDistance * 2 - randomnessDistance);
//            float yaw = lastPacket.getYaw(player.getYaw()) + yawRandomness;
//            float pitch = lastPacket.getPitch(player.getPitch()) + pitchRandomness;
//
//            this.yaw = yaw;
//            this.pitch = pitch;
//            player.networkHandler.sendPacket(lastPacket);
        // }

        if (context.shouldSneak) {
            player.input.sneaking = true;
            player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        } else {
            player.input.sneaking = false;
            player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
    }

    @Override
    public String toString() {
        return "PrepareAction{" +
                "context=" + context +
                '}';
    }
}
