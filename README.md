![A jukebox with a sculk shrieker and 2 noteblocks with sculk sensors overlayed with a 3D logo of the mod name](https://cdn.modrinth.com/data/cached_images/cf8b119a3adb67eccd127c24128d79a55c75d89a.png)

<center>
Turn jukeboxes into radios and note blocks into speakers to build the ultimate sound system.
</center>

<br>

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
- The max signal 'depth' is 8, which means a signal can be repeated 8 times making for a total max range of 128 blocks.
- Radios and repeaters can each send to 8 receivers (speakers and/or repeaters).
- Audio coming out of a speaker has the same distance and volume as if it came out of a jukebox.
- Sculk blocks will not activate when they are part of a radio, speaker or repeater. Outside of these 'multiblocks' they have their vanilla behavior.

## Mod compatibilities
Sculk Radio has native support for [AudioPlayer](https://modrinth.com/mod/audioplayer) by [henkelmax](https://modrinth.com/user/henkelmax). Custom music discs can be used on a radio and will act the same as vanilla discs.

Mods that add new music discs and resource packs that change vanilla discs should also work natively. But be sure to create an issue on the Github if there are incompatibilities.

If you're a mod developer and you want to use Sculk Radio for your own projects, the source code is available on Github and the mod has an API. I will try to make it available through maven soon. For the time being, hit me up on Discord if you need help using it.

## Demo
<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/SkuYxBhD4Fc" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>