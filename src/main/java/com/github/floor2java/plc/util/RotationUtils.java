package com.github.floor2java.plc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class RotationUtils {
    private static float pitchDifference;
    public static float yawDifference;
    private static int ticks = -1;
    private static int tickCounter = 0;
    private static Runnable callback = null;

    public static class Rotation {
        public float pitch;
        public float yaw;

        public Rotation(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }

    private static double wrapAngleTo180(double angle) {
        return angle - Math.floor(angle / 360 + 0.5) * 360;
    }

    private static float wrapAngleTo180(float angle) {
        return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
    }

    public static BlockPos closestRotation(ArrayList<Vec3> blocks) {
        BlockPos closest = null;
        float smallestAngle = Float.MAX_VALUE;

        for (Vec3 v : blocks) {
            float angleDiff = getAngleDifference(v);
            if (angleDiff < smallestAngle) {
                smallestAngle = angleDiff;
                closest = new BlockPos(v);
            }
        }

        return closest;
    }

    private static float getAngleDifference(Vec3 vec) {
        Minecraft mc = Minecraft.getMinecraft();

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double playerZ = mc.thePlayer.posZ;

        double diffX = vec.xCoord - playerX;
        double diffY = vec.yCoord - playerY;
        double diffZ = vec.zCoord - playerZ;

        double horizontalDist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, horizontalDist));
        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90;

        float playerPitch = mc.thePlayer.rotationPitch;
        float playerYaw = mc.thePlayer.rotationYaw;

        float pitchDiff = wrapAngleTo1802(targetPitch - playerPitch);
        float yawDiff = wrapAngleTo1802(targetYaw - playerYaw);

        return (pitchDiff * pitchDiff) + (yawDiff * yawDiff);
    }

    private static float wrapAngleTo1802(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }
        if (angle < -180.0F) {
            angle += 360.0F;
        }
        return angle;
    }

    public static Rotation vec3ToRotation(Vec3 vec) {
        Minecraft mc = Minecraft.getMinecraft();
        double diffX = vec.xCoord - mc.thePlayer.posX;
        double diffY = vec.yCoord - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double diffZ = vec.zCoord - mc.thePlayer.posZ;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90) * -1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }

    public static Rotation getRotationToBlock(BlockPos block) {
        double diffX = block.getX() - Minecraft.getMinecraft().thePlayer.posX + 0.5;
        double diffY = block.getY() - Minecraft.getMinecraft().thePlayer.posY + 0.5
                - Minecraft.getMinecraft().thePlayer.getEyeHeight();
        double diffZ = block.getZ() - Minecraft.getMinecraft().thePlayer.posZ + 0.5;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90) * -1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }

    public static void smoothLook(Rotation rotation, int ticks, Runnable callback) {
        if (ticks == 0) {
            look(rotation);
            if (callback != null)
                callback.run();
            return;
        }

        RotationUtils.callback = callback;

        pitchDifference = wrapAngleTo180(rotation.pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
        yawDifference = wrapAngleTo180(rotation.yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);

        RotationUtils.ticks = ticks * 20;
        RotationUtils.tickCounter = 0;
    }

    public static void smartLook(Rotation rotation, int ticksPer180, Runnable callback) {
        float rotationDifference = Math.max(Math.abs(rotation.pitch - Minecraft.getMinecraft().thePlayer.rotationPitch),
                Math.abs(rotation.yaw - Minecraft.getMinecraft().thePlayer.rotationYaw));
        smoothLook(rotation, (int) (rotationDifference / 180 * ticksPer180), callback);
    }

    public static void look(Rotation rotation) {
        Minecraft.getMinecraft().thePlayer.rotationPitch = rotation.pitch;
        Minecraft.getMinecraft().thePlayer.rotationYaw = rotation.yaw;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (tickCounter < ticks) {
            Minecraft.getMinecraft().thePlayer.rotationPitch += pitchDifference / ticks;
            Minecraft.getMinecraft().thePlayer.rotationYaw += yawDifference / ticks;
            tickCounter++;
        } else if (callback != null) {
            callback.run();
            callback = null;
        }
    }
}
