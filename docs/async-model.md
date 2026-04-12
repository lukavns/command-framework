# Async Model

Async execution is explicit.

## Rules

- a command runs asynchronously only when `async = true`
- the platform bootstrap must be configured with an executor
- if no executor is configured, async dispatch fails fast
- the framework does not silently move commands off-thread

## Why

Minecraft platform APIs are not uniformly thread-safe. The framework should not guess when work is safe to run outside the main thread.

## Recommendation

Use async only for work that is known to be safe off-thread, such as database access or remote I/O. If you need to touch Bukkit, BungeeCord, or Velocity APIs that expect the main thread, schedule the result back on the platform yourself.
