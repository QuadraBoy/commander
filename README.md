# ```commander```  
[![](https://jitpack.io/v/QuadraBoy/commander.svg)](https://jitpack.io/#QuadraBoy/commander)

**commander** (formerly ModernityAPI) is an easy-to-use, annotation-based, Minecraft Command API for Paper plugin developers!

# Features

- Easy-to-use: commander is designed to be easy-to-use for developers.
- Command Utilities: commander provides the developers a wide-range of utilities such as the **ArgumentHandler** and much more.
- No plugin.yml Command Registration: No need to register the command in the ```plugin.yml``` because commander does it for you!
- Null Safety: commander is designed to be null-safe.

# Project Status
As of now, commander is still in **BETA** due to being an early project and still needs some improving. Feel free to report a bug or issue at the [issues](https://github.com/QuadraBoy/commander/issues) section! It helps commander grow :)

# Why commander?
### _With normal bukkit/spigot_
Let's say we make a command for players only with one argument that says hello to the player using the normal way:

```java
public class TestCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            if(strings != null) {
                if(strings.length == 1) {
                    if(strings[0].equalsIgnoreCase("hello")) {
                        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<rainbow>Hello World!"));
                        return true;
                    } 
                }
            }
        } 
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return (strings != null && strings.length == 1) ? Collections.singletonList("hello") : Collections.emptyList();
    }
}
```
Then, we need to register it in the **JavaPlugin** class...

```java
public final class PluginExample extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("test").setExecutor(new TestCommand());
    }
}
```

...and don't forget to register it in **plugin.yml**.

```yaml
name: PluginExample
version: '${project.version}'
main: me.quadraboy.pluginexample.PluginExample
api-version: '1.20'

commands:
  test:
    description: Test Command
    usage: "Usage: /test <hello>"
    permission: plugin.command.test
    aliases:
      - foo
      - bar
```

As you may see, this is quite a hassle.

### _With commander_
Well, with commander, commands are as simple as this:

```java
@Command(name = "test",
        target = Command.InitiatorType.PLAYER,
        usage = "<green>Usage: /test <hello>",
        description = "Test command",
        permission = "plugin.command.test",
        aliases = {"foo", "bar"})
public class TestCommand {

    // The name of the methods don't matter, all that matters is the 2 annotations.

    @Executor(preventEmptyArgument = true)
    public Command.Status onTest(final ExecuteStructure structure) {
        structure.getArgument().create("hello", 0, () -> sender.sendMessage(MiniMessage.miniMessage().deserialize("<rainbow>Hello <arg>!", Placeholder.unparsed("arg", structure.getArgument().getString(1)))));
        return Command.Status.SUCCESS;
    }
    
    @Suggester
    public void onSuggest(final SuggestionStructure structure) {
        // This method is invoked by commander every time Bukkit requests a tab completion list
        // Or, to put it more simply, every time the player types a character in command.
        structure.getSuggestion().create(0, "hello");
        structure.getSuggestion().createIf(() -> suggestion.compare(1, "hello"), 1, Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));
    }
}
```
Well, unfortunately, you'd still need to register the command. But worry not, you only have to do it **once** (continues trash talking Bukkit)!

```java
import me.quadraboy.commander.CommandRegistry;

public final class PluginExample extends JavaPlugin {
    @Override
    public void onEnable() {
        final CommandRegistry registry = new CommandRegistry(this);
        registry.registerCommand(new TestCommand());
    }
}
```

With commander, the code becomes much cleaner and more manageable. That's not all, commander can convert those arguments into numerous data types (e.g., Player, Integer, Double, Material, Entity, etc.)

---

# Using commander in your projects
You can add commander as your project's dependency. Copy and paste the following that corresponds to your build system.

## Maven

### Repository
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

### Dependency
```xml
<dependency>
    <groupId>com.github.QuadraBoy</groupId>
    <artifactId>commander</artifactId>
    <version>BETA-0.7</version>
</dependency>
```

## Gradle (Groovy)

### Repository
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

### Dependency
```groovy
dependencies {
    implementation 'com.github.QuadraBoy:commander:BETA-0.7'
}
```

## Gradle (Kotlin)

### Repository
```kts
repositories {
    maven("https://jitpack.io")
}
```

### Dependency
```kts
dependencies {
    implementation("com.github.QuadraBoy:commander:BETA-0.7")
}
```

# Documentation

You can check the in-progress documentation in the [wiki](https://github.com/QuadraBoy/commander/wiki).

# History

This project is used to be a private Command API that I made for my custom plugins but, I've decided to publish it for everyone that are having troubles creating a command for their plugin.
