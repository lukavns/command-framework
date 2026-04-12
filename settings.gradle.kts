rootProject.name = "command-framework"

include("core")
include("bukkit")
include("bungee")
include("velocity")
include("bukkit-example")
include("bungee-example")
include("velocity-example")

project(":bukkit-example").projectDir = file("examples/bukkit-example")
project(":bungee-example").projectDir = file("examples/bungee-example")
project(":velocity-example").projectDir = file("examples/velocity-example")
