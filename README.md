Ideation Items
---
Plugin support on Discord: https://discord.com/invite/AfCfApsGHq

Ideation Items is the framework of a plugin that you can clone and write locally. It comes pre-loaded with a ton of cool development features to make life easy and get straight to complicated implementations!

This is accomplished through a system I call Blueprints. When a Blueprint is created, it is automatically tracked by the plugin and is able to some wonderful things, including:
- Automatically implement a recipe
- Seamlessly custom event-driven features into the game
- Retroactively update distributed items when they are updated in the codebase

In order to implement these powerful things, slight drawbacks had to be made, mainly that durability has been effectively disabled. Because of this I do not personally support any other plugins being used alongside this. Luckily, the entire plugin is FOSS and extremely configurable.

Want something pre-made?
---
I have the eventual idea of creating some pre-made versions of this plugin to sell. For a premium, this would include a pre-compiled version of the plugin with some items built in, alongside the raw classes for you to edit. If this interests you, [reach out to me on the discord](https://discord.gg/AfCfApsGHq) where I host my own instance every weekend with my own implementations.

Foreword
---
This plugin is not like other plugins on the market; This is a codebase I have written that allows anyone to skip a the heavy lifting and get straight into putting a complex item into the game without worrying about the back end of plugin writing.

* The target audience for this plugin is anybody who wants to make a community plugin and is able to code (or at least copy and edit templates). There are a few included examples in-game, but the intent is to write your own!
* As a general rule, Minecraft is an extremely insecure game. I am absolutely positive that various cracked clients could absolutely shred the integrity of this plugin. This is true of most plugins, but should still be understood by anyone using this.

Within reason, it is my goal to keep this plugin up-to-date to the latest version of Paper Minecraft. If you wish to downgrade this plugin to an earlier version of Minecraft, there should be no problem. The current version is `1.21.10`.

Installation
---
*This installation guide has been written with the intent of using IntelliJ*

1. Make sure you have the [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) IntelliJ plugin installed and updated. The project is currently on Minecraft version `1.21.10`
2. Create a new project via version control
3. Ensure you have [BuildTools](https://www.spigotmc.org/wiki/buildtools/) installed for your appropriate version with the `--remapped` flag (or appropriate GUI option) enabled. The following are the repository and dependencies that you need in your `pom.xml` (if you are using maven, of course.)
	1. If the `${remapped.version}` is error-red after you reload maven then you need to re-run BuildTools with the flag
```xml
<!--    Spigot    -->  
<repository>  
	<id>spigotmc-repo</id>  
	<url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
</repository>
```
```xml
<!--    NMS (Keep ABOVE spigot dependency)   -->  
<dependency>  
	<groupId>org.spigotmc</groupId>  
	<artifactId>spigot</artifactId>  
	<version>${remapped.version}</version>  
	<scope>provided</scope>  
	<classifier>remapped-mojang</classifier>  
</dependency>  
  
<!--    Spigot    -->  
<dependency>  
	<groupId>org.spigotmc</groupId>  
	<artifactId>spigot-api</artifactId>  
	<version>1.21.10-R0.1-SNAPSHOT</version>  
	<scope>provided</scope>  
</dependency>
```

### Building the plugin
If you need help setting up your build dependencies, I recommend this [archived post by mfnalex](https://web.archive.org/web/20250520100129/https://blog.jeff-media.com/nms-use-mojang-mappings-for-your-spigot-plugins/). IntelliJ should automatically set up the Spigot build, but you need to manually set anything for NMS. If its not working I recommend just removing any relevant NMS code because most things can work without it. For convenience, I include [my own pom.xml](https://github.com/CJ-Cate/IdeationItems/blob/main/pom.xml) you can use to build with maven. With a little luck, this should work out-of-the-box for version `1.21.10`. 

For ease of development, I would recommend a maven plugin like as follows to automatically place the built plugin in a proper location so that you only have to reload your server:
```xml
<plugin>  
    <groupId>org.apache.maven.plugins</groupId>  
    <artifactId>maven-jar-plugin</artifactId>  
    <version>3.3.0</version>  
	    <configuration>        <outputDirectory>/your/directory/server/plugins</outputDirectory></configuration>  
</plugin>
```

Remember to use some sort of re-obfuscation when you are shipping your plugin. You are *technically legally required* to re-obfuscate your build away from mojang's mappings when you build your plugin with intent to distribute. I am not a lawyer, but I recommend using the following plugin (written by mfnalex) in your `pom.xml`:
```xml
<plugin>  
    <groupId>net.md-5</groupId>  
    <artifactId>specialsource-maven-plugin</artifactId>  
    <version>2.0.2</version>  
    <executions>        <execution>  
            <phase>package</phase>  
            <goals>                <goal>remap</goal>  
            </goals>  
            <id>remap-obf</id>  
            <configuration>                <srgIn>org.spigotmc:minecraft-server:${remapped.version}:txt:maps-mojang</srgIn>  
                <reverse>true</reverse>  
                <useProjectDependencies>false</useProjectDependencies>  
                <remappedDependencies>org.spigotmc:spigot:${remapped.version}:jar:remapped-mojang</remappedDependencies>  
                <remappedArtifactAttached>true</remappedArtifactAttached>  
                <remappedClassifierName>remapped-obf</remappedClassifierName>  
            </configuration>  
        </execution>  
        <execution>            <phase>package</phase>  
            <goals>                <goal>remap</goal>  
            </goals>  
            <id>remap-spigot</id>  
            <configuration>                <useProjectDependencies>false</useProjectDependencies>  
                <inputFile>${project.build.directory}/${project.artifactId}-${project.version}-remapped-obf.jar</inputFile>  
                <srgIn>org.spigotmc:minecraft-server:${remapped.version}:csrg:maps-spigot</srgIn>  
                <remappedDependencies>org.spigotmc:spigot:${remapped.version}:jar:remapped-obf  
                </remappedDependencies>  
            </configuration>  
        </execution>  
    </executions>  
</plugin>
```
