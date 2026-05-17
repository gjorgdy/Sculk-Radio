# Nodes

## Radio
_Jukebox with a Sculk Shrieker on top of it._

## Speaker
_Note Block with a Sculk Sensor on top of it._

## Relay
_Block of Amethyst with a Sculk Sensor on top of it._

## Antenna
_Block of Amethyst with a Calibrated Sculk Sensor on top of it._

Frequency modes;
- Vanilla: Frequency is determined by the redstone signal strength. 0-15.
- Tuned: When right-clicking the node with an Amethyst Crystal both the node and the crystal will be assigned a random frequency. 
   The crystal can be used to set the frequency of other Antenna nodes by right-clicking them.
- Blocks: The frequency is determined by the four blocks around the Amethyst block.

# Node Registry

A registry per world that stores all nodes, networks, and their connections.

# Sculk Subnetworks

A node always has to be part of a subnetwork.

When a node is placed it starts of with a singular subnetwork. When it connects to another node, 
the two subnetworks merge into one. 

When a node is removed, the subnetwork it was part of is split into multiple subnetworks.

# Sculk Network

If an antenna is part of a subnetwork, that subnetwork is also part of the larger Sculk Network.

A subnetwork connects to any other subnetwork that has an antenna with the same frequency.

