# Architecture Overview

The framework is built around a small set of explicit responsibilities.

## Flow

1. `CommandScanner` reads annotated classes.
2. Scanning produces immutable `CommandDefinition` and `ParameterDefinition` objects.
3. The platform bootstrap wires resolvers, suggestion providers, and provided values.
4. `CommandDispatcher` matches a command path and validates sender and permission rules.
5. `ArgumentParser` resolves method arguments.
6. The method handler is invoked.
7. The platform layer turns framework exceptions into sender-facing messages.

## Why It Looks This Way

The original project centered almost everything around `CommandFrame`.

That was convenient, but it also mixed together:

- scanning
- parsing
- invocation
- suggestions
- messaging
- platform registration

This rewrite keeps command authoring simple, but moves those responsibilities into smaller parts that are easier to test and maintain.

## Package Boundaries

- `api`: public contracts
- `core`: scanning, parsing, dispatch, suggestions
- `bukkit`: Bukkit, Spigot, and Paper bridge
- `bungee`: BungeeCord bridge
- `velocity`: Velocity bridge
