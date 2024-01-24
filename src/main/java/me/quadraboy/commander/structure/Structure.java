package me.quadraboy.commander.structure;

import me.quadraboy.commander.structure.handler.StringHandler;
import me.quadraboy.commander.structure.handler.arguments.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Structure<T extends StringHandler> {
    private final CommandSender commandSender;
    private final T handler;

    public Structure(@NotNull final CommandSender commandSender, @NotNull T handler) {
        this.commandSender = commandSender;
        this.handler = handler;
    }

    public CommandSender getSender() {
        return this.commandSender;
    }

    public Player getPlayer() {
        return (Player) this.commandSender;
    }

    public T getHandler() {
        return this.handler;
    }
}