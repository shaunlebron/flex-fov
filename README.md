# Blinky for Minecraft

Porting [blinky] features to [this minecraft mod] to demonstrate peripheral
vision in a modern game.

[blinky]:https://github.com/shaunlebron/blinky
[this minecraft mod]:https://github.com/18107/MC-Render360

## Quick Start

__This currently only works for Windows__.  Run the following to start a game with
the mod installed.

```
gradlew.bat runClient
```

> __NOTE__: Prior to running this, I had Minecraft and Minecraft Forge installed (both 1.11.2).
> But I am not actually sure if this is required, since I believe this gradle
> setup is building the entire game itself from decompiled sources?

## Deploying

This will build a jar file that users can install.  (Not currently working
when I try to install to a 1.11.2 forge mods folder.)

```sh
gradlew.bat build
# outputs a jar to build/libs
```

## Dev Setup

- [I setup IntelliJ][intellij] to have autocomplete for exploring the API.
- You can refer to this [older Forge javadoc][javadoc] if you're using an editor without autocomplete.

[intellij]:http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/2714237-forge-1-11-1-10-setting-up-mod-environment-with
[javadoc]:http://takahikokawasaki.github.io/minecraft-resources/javadoc/forge/1.8-11.14.1.1320/
