package me.aleksilassila.litematica.printer.v1_20.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public abstract class Action {
    abstract public void send(MinecraftClient client, ClientPlayerEntity player);

    public boolean isSync = false;
}
