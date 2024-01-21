package me.quadraboy.commander.structure;

import me.quadraboy.commander.structure.handler.StringHandler;
import me.quadraboy.commander.structure.handler.arguments.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Structure {
    private final CommandSender commandSender;
    private final StringHandler handler;

    public Structure(@NotNull final CommandSender commandSender, @NotNull StringHandler handler) {
        this.commandSender = commandSender;
        this.handler = handler;
    }

    public CommandSender getSender() {
        return this.commandSender;
    }

    public Player getPlayer() {
        return (Player) this.commandSender;
    }

    public StringHandler getHandler() {
        return this.handler;
    }
}