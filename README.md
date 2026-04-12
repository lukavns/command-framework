# Command Framework

`Command Framework` is a public, fork-inspired successor to the original [`saiintbrisson/command-framework`](https://github.com/saiintbrisson/command-framework).

Repository reference:

- current repository: `lukavns/command-framework`
- original repository: [`saiintbrisson/command-framework`](https://github.com/saiintbrisson/command-framework)

The original project is no longer maintained by its original author. This rewrite keeps the idea of annotation-based commands, but narrows the scope, cleans up the API, and modernizes the internals.

## Scope

Supported platforms:

- BungeeCord
- Bukkit
- Spigot
- Paper
- Velocity

Not supported:

- Waterfall
- Sponge

## Compatibility

- `core`: Java 8
- `bukkit`: Java 8
- `bungee`: Java 8
- `velocity`: Java 17
- one public main branch
- no separate compatibility branch

## Modules

- `core`
- `bukkit`
- `bungee`
- `velocity`
- `examples/bukkit-example`
- `examples/bungee-example`
- `examples/velocity-example`

## Features

- annotation-based commands
- grouped subcommands
- typed argument resolvers
- optional arguments
- remaining-string arguments
- static and dynamic suggestions
- configurable sender-facing messages
- explicit async execution

## Installation

This repository is prepared for JitPack distribution. For this multi-module project, prefer the individual platform artifacts:

- `com.github.lukavns.command-framework:core:<tag>`
- `com.github.lukavns.command-framework:bukkit:<tag>`
- `com.github.lukavns.command-framework:bungee:<tag>`
- `com.github.lukavns.command-framework:velocity:<tag>`

### Gradle Kotlin DSL

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

```kotlin
dependencies {
    implementation("com.github.lukavns.command-framework:bukkit:<tag>")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.lukavns.command-framework</groupId>
    <artifactId>bukkit</artifactId>
    <version>TAG</version>
</dependency>
```

Use the platform module that matches your runtime. Platform modules already depend on `core`.

## Documentation

- usage guide: [USAGE.md](USAGE.md)
- architecture notes: [docs/architecture.md](docs/architecture.md)
- async model: [docs/async-model.md](docs/async-model.md)

## Migration Summary

The rewrite is intentionally not source-compatible with the old frame-centered architecture.

Main changes:

- `CommandFrame` became a platform bootstrap around scanner, dispatcher, resolver registry, and registrar
- adapters became `ArgumentResolver<T>`
- `@Completer` became `@Suggest` plus registered suggestion providers
- metadata such as aliases, permission, usage, description, and target now lives inside `@Command` and `@SubCommand`

## License And Attribution

This repository is a derivative rewrite of the original `saiintbrisson/command-framework`.

- upstream license obligations remain Apache-2.0
- attribution is preserved in this repository
- see [LICENSE](LICENSE) and [NOTICE](NOTICE)
