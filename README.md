Ideation Items
---
Plugin support on Discord: https://discord.com/invite/AfCfApsGHq

Ideation Items is the framework of a plugin that you can clone and write locally. It comes pre-loaded with a ton of cool development features to make life easy and get straight to complicated implementations!

This is accomplished through a system I call Blueprints. When a Blueprint is created, it is automatically tracked by the plugin and is able to some wonderful things, including:
- Automatically implement a recipe
- Seamlessly custom event-driven features into the game
- Retroactively update distributed items when they are updated in the codebase

In order to implement these powerful things, slight drawbacks had to be made, mainly that durability has been effectively disabled. Because of this, I do not personally support any other plugins being used alongside this. Luckily, the entire plugin is FOSS and extremely configurable.
Foreword
---
This plugin is not like other plugins on the market; This is a codebase I have written that allows anyone to skip a the heavy lifting and get straight into putting a complex item into the game without worrying about the back end of plugin writing.

* The target audience for this plugin is anybody who wants to make a community plugin and is able to code (or at least copy and edit templates). There are a few included examples in-game, but the intent is to write your own!
* As a general rule, Minecraft is an extremely insecure game. I am absolutely positive that various cracked clients could absolutely shred the integrity of this plugin. This is true of most plugins, but should still be understood by anyone using this.

Version Compatibility
---
Within reason, it is my goal to keep this plugin up-to-date to the latest version of Paper Minecraft. If you wish to downgrade this plugin to an earlier version of Minecraft, be my guest. The current version is `1.20.6`.

Installation
---
*This installation guide has been written with the intent of using IntelliJ*

1. Make sure you have the [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) IntelliJ plugin installed and updated. The project is currently on Minecraft version `1.20.6`. Visit the discord for up-to-date information on when this will be updated to latest.
2. Create a new project from version control, via this github repo.
3. Visit the examples provided in the `CustomMobs` file and `Items` file. Most things are well-documented. 

### Building the plugin
If you need help setting up your build dependencies, I recommend this [old post by mfnalex](https://web.archive.org/web/20250520100129/https://blog.jeff-media.com/nms-use-mojang-mappings-for-your-spigot-plugins/). IntelliJ should automatically set up the Spigot build, but you need to manually set anything for NMS. If its not working I recommend just removing any relevant NMS code because most things can work without it. For convenience, I include [my own pom.xml](https://github.com/CJ-Cate/IdeationItems/blob/main/pom.xml) you can use to build with maven. With a little luck, this should work out-of-the-box for version `1.20.6`. 
* While I recommend [official Paper](https://papermc.io/), I personally use a [fork of paper](https://infernalsuite.com/download/asp/) called ASPaper. This is reflected in the `pom.xml` I provided and may differ slightly from stock Paper, but should be entirely compatible.

Want something pre-made?
---
I have the eventual idea of creating some pre-made versions of this plugin to sell. For a premium, this would include a pre-compiled version of the plugin with some items built in, alongside the raw classes for you to edit. If this interests you, [reach out to me on the discord](https://discord.gg/AfCfApsGHq) where I host my own instance every weekend with my own implementations.
