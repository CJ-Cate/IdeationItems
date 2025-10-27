Foreword
---
This plugin is not like other plugins on the market; This is a codebase I have written that allows anyone to skip a the heavy lifting and get straight into putting a complex item into the game without worrying about the backend.

The target audience for this plugin is anybody who wants to make a plugin and is able to code. Upon installation this plugin is quite bare and only includes examples of what can be done. 

Disclaimers
---
#### Blueprints
This plugin runs on a system I call Blueprints. When a Blueprint is created, it is tracked by the plugin and is able to some wonderful things, including:
- Automatically implement a recipe
- Implement custom events right into the game
- Retroactively update itself if it is updated in the codebase

In order to implement these powerful things, slight drawbacks had to be made, mainly that durability has been effectively disabled. Because of this, I do not personally support any other plugins being used alongside this. Luckily, the entire plugin is FOSS and extremely configurable.

#### Version Compatibility

Within reason, it is my goal to keep this plugin up-to-date to the latest version of Paper Minecraft. If you wish to downgrade this plugin to an earlier version of Minecraft, be my guest. 

Installation
---
*This installation guide has been written with the intent of using IntelliJ*

1. Make sure you have the [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) IntelliJ plugin installed and updated
2. Create a new project from version control, via this github repo.
3. Visit the examples provided in the `CustomMobs` file and `Items` file. Most things are well-documented.

If you need help setting up your build dependencies, I recommend this [old post by mfnalex](https://web.archive.org/web/20250520100129/https://blog.jeff-media.com/nms-use-mojang-mappings-for-your-spigot-plugins/). IntelliJ should automatically set the spigot build, but you need to manually set anything for NMS. If its not working I recommend just removing any relevant NMS code because most things can work without it.

Want something pre-made?
---
I have the eventual idea of creating some pre-made versions of this plugin to sell. For a premium, it would include a pre-compiled version for current latest and the raw class. If this interests you then reach out
