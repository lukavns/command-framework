# Usage Guide

This guide covers the common ways to use the framework without repeating every internal detail from the other docs.

Public annotations:

- `@Command`
- `@SubCommand`
- `@OptionalArg`
- `@Remaining`
- `@Suggest`

Use `CommandTarget` inside `@Command` and `@SubCommand` when a command should be limited to players or console.

## 1. Pick The Right Module

- Bukkit, Spigot, Paper: `bukkit`
- BungeeCord: `bungee`
- Velocity: `velocity`

Platform modules already depend on `core`.

## 2. Bootstrap

### Bukkit / Spigot / Paper

```java
public final class AtlasLobbyPlugin extends JavaPlugin {

    private BukkitCommandFramework framework;

    @Override
    public void onEnable() {
        this.framework = BukkitCommandFramework.builder(this).build();
        this.framework.register(new ProfileCommands());
    }
}
```

### BungeeCord

```java
public final class GatewayProxyPlugin extends Plugin {

    private BungeeCommandFramework framework;

    @Override
    public void onEnable() {
        this.framework = BungeeCommandFramework.builder(this).build();
        this.framework.register(new NetworkCommands());
    }
}
```

### Velocity

```java
@Plugin(id = "northwind-network", name = "NorthwindNetwork", version = "1.0.0")
public final class NorthwindVelocityPlugin {

    private final VelocityCommandFramework framework;

    @Inject
    public NorthwindVelocityPlugin(ProxyServer proxyServer) {
        this.framework = VelocityCommandFramework.builder(proxyServer, this).build();
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        this.framework.register(new NetworkCommands());
    }
}
```

## 3. Write Commands

### Grouped Command

This is the default style for command classes with multiple actions.

```java
@Command(
    name = "profile",
    aliases = {"whois"},
    description = "Profile commands.",
    permission = "atlas.profile"
)
public final class ProfileCommands {

    public void root(Context<CommandSender> context) {
        context.sendMessage("Try /profile show <player> or /profile note <player> [text]");
    }

    @SubCommand(name = "show")
    public void show(Context<CommandSender> context, Player target) {
        context.sendMessage("Profile opened for " + target.getName());
    }

    @SubCommand(name = "note")
    public void note(
        Context<CommandSender> context,
        Player target,
        @OptionalArg({"No note"}) @Remaining String text
    ) {
        context.sendMessage("Saved note for " + target.getName() + ": " + text);
    }
}
```

Rules:

- class-level `@Command` defines the root command
- a public method without `@SubCommand` becomes the root handler
- `@SubCommand` creates child paths such as `/profile show`
- if `@SubCommand` has no `permission`, it inherits the permission from `@Command`

### Single-Action Command

If the command only does one thing, you can skip `@SubCommand`.

```java
@Command(
    name = "menu",
    description = "Opens the main server menu.",
    target = CommandTarget.PLAYER
)
public final class MenuCommand {

    public void open(Context<CommandSender> context) {
        Player player = (Player) context.getSender();
        Inventory menu = Bukkit.createInventory(null, 27, "Server Menu");

        player.openInventory(menu);
        context.sendMessage("Opening the server menu.");
    }
}
```

This style works well for menus, toggles, and simple utility commands.

## 4. Arguments And Suggestions

### Optional Values And Remaining Text

`@OptionalArg` provides a fallback when the argument is missing.

`@Remaining` consumes the rest of the command line into the last `String` argument.

```java
@Command(name = "message")
public final class MailboxCommands {

    @SubCommand(name = "send")
    public void send(
        Context<CommandSender> context,
        Player target,
        @OptionalArg({"Hello there"}) @Remaining String content
    ) {
        target.sendMessage(content);
        context.sendMessage("Sent message to " + target.getName());
    }
}
```

Examples:

- `/message send Sofia` uses `Hello there`
- `/message send Sofia Welcome back to the lobby` uses `Welcome back to the lobby`

### Static Suggestions

```java
@Command(name = "timecontrol")
public final class TimeControlCommands {

    @SubCommand(name = "set")
    public void set(Context<CommandSender> context, @Suggest({"day", "night", "noon"}) String value) {
        context.sendMessage("Selected time: " + value);
    }
}
```

### Dynamic Suggestions

Register a named provider and point `@Suggest` to it.

```java
framework.suggestions().register("online-players", context -> {
    List<String> names = new ArrayList<String>();
    for (Player player : Bukkit.getOnlinePlayers()) {
        names.add(player.getName());
    }
    return names;
});
```

```java
@Command(name = "teleport")
public final class TeleportCommands {

    @SubCommand(name = "to")
    public void to(Context<CommandSender> context, @Suggest(provider = "online-players") Player target) {
        context.sendMessage("Teleport target: " + target.getName());
    }
}
```

## 5. Resolvers And Provided Values

### Custom Resolver

Use a resolver when a plain string should become one of your own types.

```java
public final class CustomerRecord {

    private final UUID id;

    public CustomerRecord(UUID id) {
        this.id = id;
    }

    public UUID id() {
        return this.id;
    }
}
```

```java
framework.resolvers().register(CustomerRecord.class, (context, parameter, input) -> {
    try {
        return new CustomerRecord(UUID.fromString(input));
    } catch (IllegalArgumentException exception) {
        throw new ArgumentParseException("Invalid customer id: " + input);
    }
});
```

```java
@Command(name = "customer")
public final class CustomerCommands {

    @SubCommand(name = "lookup")
    public void lookup(Context<CommandSender> context, CustomerRecord record) {
        context.sendMessage("Loaded customer " + record.id());
    }
}
```

### Provided Values

Platform bootstraps already register a few runtime types.

- Bukkit: `Plugin`, `Server`
- BungeeCord: `Plugin`, `ProxyServer`
- Velocity: `ProxyServer`, your plugin main class

That means these values can be injected directly:

```java
@Command(name = "system")
public final class SystemCommands {

    @SubCommand(name = "info")
    public void info(Context<CommandSender> context, Plugin plugin, Server server) {
        context.sendMessage("Plugin: " + plugin.getName());
        context.sendMessage("Server: " + server.getName());
    }
}
```

You can register your own provided values too:

```java
framework.providedValues().register(MyService.class, new ProvidedValueFactory<MyService>() {
    @Override
    public Class<MyService> type() {
        return MyService.class;
    }

    @Override
    public MyService provide(CommandContext<?> context) {
        return myService;
    }
});
```

## 6. Messages And Async

### Built-In Messages

For simple customization, use the message holder:

```java
framework.messageHolder()
    .setMessage(CommandMessageType.INCORRECT_USAGE, "§cIncorrect usage, try: /{usage}")
    .setMessage(CommandMessageType.NO_PERMISSION, "§cYou do not have permission to use this command.")
    .setMessage(CommandMessageType.INCORRECT_TARGET, "§cThis command can only be used by {target}.")
    .setMessage(CommandMessageType.ERROR, "§cInternal server error, please contact an administrator.");
```

Available placeholders:

- `{usage}`
- `{permission}`
- `{target}`
- `{error}`

If you need full control, configure `messageResolver(...)` in the builder.

### Async Commands

Async must be explicit in both places:

- provide an executor in the bootstrap
- mark the command or subcommand with `async = true`

```java
Executor executor = Executors.newFixedThreadPool(2);

BukkitCommandFramework framework = BukkitCommandFramework.builder(plugin)
    .asyncExecutor(executor)
    .build();
```

```java
@Command(name = "lookup")
public final class LookupCommands {

    @SubCommand(name = "user", async = true)
    public void user(Context<CommandSender> context, String username) {
        context.sendMessage("Looking up " + username + " off-thread.");
    }
}
```

Use async only for work that is safe outside the main thread.

## 7. Platform Notes

The command model stays the same across platforms. What changes is the sender type and the platform-specific argument types.

### Bukkit

```java
public void inspect(Context<CommandSender> context, Player target) {
    context.sendMessage("Inspecting " + target.getName());
}
```

### BungeeCord

```java
public void inspect(Context<CommandSender> context, ProxiedPlayer target) {
    context.sendMessage("Inspecting " + target.getName());
}
```

### Velocity

```java
public void inspect(Context<CommandSource> context, Player target) {
    context.sendMessage("Inspecting " + target.getUsername());
}
```
