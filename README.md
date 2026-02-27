![A jukebox with a sculk shrieker and 2 noteblocks with sculk sensors overlayed with a 3D logo of the mod name](https://cdn.modrinth.com/data/cached_images/cf8b119a3adb67eccd127c24128d79a55c75d89a.png)

<center>
Use the magic of Sculk to connect Note Blocks to Jukeboxes as speakers.
</center>

<br>

## Custom discs
Sculk Radio has native support for [AudioPlayer](https://modrinth.com/mod/audioplayer) by [henkelmax](https://modrinth.com/user/henkelmax).
Custom music discs from this mod can be used on a radio and will act the same as vanilla discs.

## How to use
To start of, place a _shrieker_ on a _jukebox_, to turn it into a radio.
When a jukebox is used as a radio it will not give off any audio itself meaning it can be hidden within redstone contraptions.

To 'receive' the signal sent by a _radio_, you need a _speaker_.
A speaker is made by placing a _sculk sensor_ on top of a _note block_.
The maximum distance a speaker can receive a signal from is 16 blocks.

To expand the range of a signal you can repeat it using a _signal repeater_.
A single repeater is made by placing a _sculk sensor_ on top of a _block of amethyst_.
The maximum distance for receiving and repeating a signal is 16 blocks. Which means a single repeater can already double the range of a speaker.

## Things to know
- Speakers and repeaters give of a redstone signal when playing/repeating audio. This means you can use them in redstone contraptions to trigger other things.
- The max signal 'depth' is 8, which means a signal can be repeated 8 times making for a total max range of 128 blocks.
- Radios and repeaters can each send to 8 receivers (speakers and/or repeaters).
- Audio coming out of a speaker has the same distance and volume as if it came out of a jukebox.
- Sculk blocks will not activate when they are part of a radio, speaker or repeater. Outside of these 'multiblocks' they have their vanilla behavior.

## Other Mod compatibilities
Mods that add new music discs and resource packs that change vanilla discs should also work natively. But be sure to create an issue on the Github if there are incompatibilities.

If you're a mod developer, and you want to use Sculk Radio for your own projects, you can use the API provided by the mod.

Maven
```xml
<repository>
  <id>modrinth-repo</id>
  <url>https://api.modrinth.com/maven/</url>
</repository>

<dependency>
  <groupId>maven.modrinth</groupId>
  <artifactId>${mod_id}</artifactId>
  <version>${mod_version}</version>
  <scope>provided</scope>
</dependency>
```

Gradle
```gradle
repositories {
    exclusiveContent {
        forRepository { maven { url = "https://api.modrinth.com/maven" } }
        filter { includeGroup "maven.modrinth" }
    }
}

dependencies {
    compileOnly 'maven.modrinth:${mod_id}:${mod_version}'
}
```

You can call the API like this:
```java
import nl.gjorgdy.sculk_radio.SculkRadio;

SculkRadio.api();
```

## Demo
<iframe width="1439" height="1261" src="https://www.youtube.com/embed/6YeBJH30QTU" title="Sculk Radio Mod Showcase - Copper Golem Tavern" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>