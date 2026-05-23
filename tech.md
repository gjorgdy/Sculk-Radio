# Nodes

## Radio
_Jukebox with a Sculk Shrieker on top of it._

## Speaker
_Note Block with a Sculk Sensor on top of it._

## Relay
_Block of Amethyst with a Sculk Sensor on top of it._

## Antenna
_Block of Amethyst with a Calibrated Sculk Sensor on top of it._

Should be placed above a relay (at least 16 blocks above) and can only connect to the relay below it.

Frequency modes;
- Vanilla: Frequency is determined by the redstone signal strength. 0-15.
- Tuned: When right-clicking the node with an Amethyst Crystal both the node and the crystal will be assigned a random frequency. 
   The crystal can be used to set the frequency of other Antenna nodes by right-clicking them.
- Blocks: The frequency is determined by the four blocks around the Amethyst block.

# Node Registry

A registry per world that stores all nodes, networks, and their connections.

# Sculk Cluster

A node always has to be part of a cluster.

When a node is placed it starts of with a singular cluster. When it connects to another node, 
the two clusters merge into one. 

When a node is removed, the cluster it was part may split into multiple clusters.

# Frequencies

If an antenna is part of a cluster and has a frequency, its cluster will be assigned to the channel with that frequency.

