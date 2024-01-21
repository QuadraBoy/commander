package me.quadraboy.commander;

import me.quadraboy.commander.annotations.Command;
import me.quadraboy.commander.annotations.Executor;
import me.quadraboy.commander.annotations.Suggester;
import me.quadraboy.commander.structure.Structure;
import me.quadraboy.commander.structure.handler.arguments.Argument;
import me.quadraboy.commander.structure.handler.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

public class CommandRegistry {
    private final Plugin plugin;
    private final CommandMap commandMap = Bukkit.getServer().getCommandMap();

    public CommandRegistry(@NotNull final Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(@NotNull final Object commandObject) {
        final Class<?> targetClass = commandObject.getClass();

        if(!targetClass.isAnnotationPresent(Command.class)) throw new CommandException("The target class (" + targetClass.getName() + ") is not a command.");

        final Command command = targetClass.getAnnotation(Command.class);

        final Method executorMethod = Arrays.stream(targetClass.getMethods()).filter((method -> method.isAnnotationPresent(Executor.class))).findAny().orElseThrow(() -> new CommandException("The target class (" + targetClass.getName() + ") does not have an Executor method."));
        final Optional<Method> suggesterMethod = Arrays.stream(targetClass.getMethods()).filter((method -> method.isAnnotationPresent(Suggester.class))).findAny();

        final Command.InitiatorType target = command.target();

        final org.bukkit.command.Command baseCommand = new org.bukkit.command.Command(command.name(), command.description(), command.usage(), List.of(command.aliases())) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                final boolean allowPlayer = (target == Command.InitiatorType.PLAYER && sender instanceof Player) ,
                        allowConsole = (target == Command.InitiatorType.CONSOLE && sender instanceof ConsoleCommandSender),
                        allowBoth = (target == Command.InitiatorType.BOTH && ( (sender instanceof Player) || (sender instanceof ConsoleCommandSender) ));

                final MiniMessage miniMessage = MiniMessage.miniMessage();
                final Component formattedUsage = miniMessage.deserialize(getUsage());

                final Argument argument = new Argument(args);
                final Structure executeStructure = new Structure(sender, argument);

                if(allowPlayer || allowConsole || allowBoth) {
                    if(args != null) {

                        if(executorMethod.getAnnotation(Executor.class).preventEmptyArgument() && args.length == 0) {
                            sender.sendMessage(formattedUsage);
                            return false;
                        }

                        try {

                            final Command.Status status = (Command.Status) executorMethod.invoke(commandObject, executeStructure);

                            if(status == Command.Status.FAILED) sender.sendMessage(formattedUsage);

                        } catch (Exception e) {
                            throw new CommandException("An error occurred when running the Executor method: " + e);
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
                final Suggestion suggestion = new Suggestion(args);
                final Structure suggestionStructure = new Structure(sender, suggestion);

                suggesterMethod.ifPresent(method -> {
                    try {
                        method.invoke(commandObject, suggestionStructure);

                        if(method.getAnnotation(Suggester.class).sorted()) Collections.sort(suggestion.getCompletions());
                    } catch (Exception e) {
                        throw new CommandException("An error occurred when running the Suggester method: " + e);
                    }
                });
                return suggestion.getCompletions();
            }
        };

        baseCommand.setPermission(command.permission());

        if(!baseCommand.isRegistered()) commandMap.register(plugin.getName(), baseCommand);
    }

    public void unregisterCommand(@NotNull final Object commandObject) {
        final Class<?> targetClass = commandObject.getClass();

        if(!targetClass.isAnnotationPresent(Command.class)) throw new CommandException("The target class (" + targetClass.getName() + ") is not a command.");

        final Command command = targetClass.getAnnotation(Command.class);

        if(Objects.requireNonNull(commandMap.getCommand(command.name())).isRegistered()) Objects.requireNonNull(commandMap.getCommand(command.name())).unregister(commandMap);
    }

    public void registerCommands(@NotNull final Object... commandObjects) {
        Arrays.stream(commandObjects).forEach(this::registerCommand);
    }

    public void unregisterCommands(@NotNull final Object... commandObjects) {
        Arrays.stream(commandObjects).forEach(this::unregisterCommand);
    }
}