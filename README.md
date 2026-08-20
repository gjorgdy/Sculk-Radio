![A jukebox with a sculk shrieker and 2 noteblocks with sculk sensors overlayed with a 3D logo of the mod name](https://cdn.modrinth.com/data/cached_images/cf8b119a3adb67eccd127c24128d79a55c75d89a.png)

<center>
Build networks to stream music, redstone, and voice chat across your world.
</center>

<br>

## Sculk Networks
A network is built out of 'nodes', which connect with other nodes in range (16 blocks by default).
These nodes are _multiblocks_ with a Sculk block as top.

### _Connection_

To connect all other nodes together to create and interconnect networks.

![Connection Nodes](https://cdn.modrinth.com/data/cached_images/9a2e43b24bbca88901466062f93d015ef9aea0ee.webp)

<details>
<summary>Relay - sculk sensor on top of an amethyst block</summary>

A relay acts as a repeater of a stream, connecting source and receiver nodes together.

</details>

<details>
<summary>Antenna - calibrated sculk sensor on top of an amethyst block</summary>

![Antenna Setup](https://cdn.modrinth.com/data/cached_images/754ceb526c5f3b4785c132741221eadfaa8cc809.webp)

An antenna can connect different networks together over a frequency.\
For an antenna to function, it needs to be placed directly **above** a relay with a **minimum** distance instead of a maximum (16 blocks by default).
> An _amethyst shard_ can be used to tune an antenna by right-clicking it.\
- If either the shard or the antenna has a frequency, it will be copied over to the other.\
- If neither have a frequency, a random new frequency will be tuned to both.

> An analog redstone signal can be input into the _calibrated sculk sensor_ to set that as frequency.

> Can be disabled in config file

</details>

### _Audio_

Stream vanilla or voice chat audio across sculk networks.

![Audio Nodes](https://cdn.modrinth.com/data/cached_images/5e0a5250e20897d3d98e2d1b98732650b16b8b72.webp)

<details>
<summary>Radio - sculk shrieker on top of a jukebox</summary>

A radio will stream the audio of any music disc into an _audio stream_ instead of playing it themselves.
> A radio also streams the redstone signals it outputs, both normal and analog strengths.

</details>

<details>
<summary>Microphone - sculk shrieker on top of a sculk catalyst</summary> 

``requires Simple Voice Chat``

A microphone will stream the voice chat of any player
within range (8 blocks by default) into an _audio stream_ as long as it is powered by redstone.
> A microphone streams a redstone signal of 15 while active, and an analog redstone signal based on the amount of people are speaking in range.

> Can be disabled in config file

</details>

<details>
<summary>Speaker - sculk sensor on top of a note block</summary>

A speaker outputs all audio from _audio streams_ it receives.
> Outputs the highest redstone signal from the streams it's playing.

</details>

### _Redstone_

Stream redstone signals across sculk networks.

![Redstone Nodes](https://cdn.modrinth.com/data/cached_images/2aebed624e0bd86ac7fc00d41cee9f30049d96d8.webp)

<details>
<summary>Redstone Transmitter - sculk shrieker on top of a target block</summary>

A redstone transmitter will stream the highest redstone
signal going into the target block into a _redstone stream_.
> Can be disabled in config file

</details>

<details>
<summary>Redstone Receiver - sculk sensor on top of a target block</summary>

A redstone receiver outputs the strongest redstone power from receiving _redstone streams_.
> Can be disabled in config file

</details>

### _Teleportation_ ``disabled by default``

Stream entities across sculk networks for teleportation.

![Teleport Nodes](https://cdn.modrinth.com/data/cached_images/6328ded3eb4a609834f0be443818c21ed7eb63db.webp)

<details>
<summary>Ender Transmitter - sculk shrieker on top of a purpur block</summary>

An ender transmitter will stream any entities in range (2 blocks) into a _teleport stream_
as long as it is powered by redstone.
> Can be enabled in config file

</details>

<details>
<summary>Ender Receiver - sculk sensor on top of a purpur block</summary>

An ender receiver will teleport any entity from a _teleport stream_ towards itself.
> Can be enabled in config file

</details>

---

## Things to Know
- Vanilla music discs will only be able to be heard if the player is in range of the speaker when it starts playing.
- Audio coming out of a speaker has the same distance and volume as if it came out of a jukebox.
- Sculk blocks will not activate when they are part of a nodes. Outside of these 'multiblocks' they have their vanilla behavior.
- Right-clicking a node with an empty hand will show which nodes are in range using vibration particles.

## Voice Chat
While it works without it, it is recommended to use _Sculk Radio_ alongside
[Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) and [AudioPlayer](https://modrinth.com/mod/audioplayer) by [henkelmax](https://modrinth.com/user/henkelmax)
to get the most use out of its features.

## Other Mod Support
Explicit support has been added for [More Jukebox/Noteblock Variants](https://modrinth.com/mod/more-jukebox-noteblock-variants) by [LieOn Studios](https://modrinth.com/organization/lieonstudios)

Music Discs added by mods _should_ work exactly the same as any vanilla disc.

## Configuration

On its own, the mod will not create a config file.
To change settings, you need to install [Fzzy Config](https://modrinth.com/mod/fzzy-config).

To load changes to the config file, you can use the vanilla ``/reload`` command.
> Some config options are labelled with ``[Requires restart]``, these can not be reloaded using the command.

## Demo
<iframe width="1439" height="1261" src="https://www.youtube.com/embed/6YeBJH30QTU" title="Sculk Radio Mod Showcase - Copper Golem Tavern" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>