# Nodes

## Radio / SourceNode
_Jukebox with a Sculk Shrieker on top of it._

## Speaker / ReceiverNode
_Note Block with a Sculk Sensor on top of it._

## Relay / RelayNode
_Block of Amethyst with a Sculk Sensor on top of it._

## Antenna / AntennaNode
_Block of Amethyst with a Calibrated Sculk Sensor on top of it._

Frequency modes;
- Vanilla: Frequency is determined by the redstone signal strength. 0-15.
- Tuned: When right-clicking the node with an Amethyst Crystal both the node and the crystal will be assigned a random frequency. 
   The crystal can be used to set the frequency of other Antenna nodes by right-clicking them.
- Blocks: The frequency is determined by the four blocks around the Amethyst block.

# Node Registry

A registry per world that stores all nodes, networks, and their connections.

# 3 Layers Model

## 1. Physical Layer
The actual blocks in the world as described in [Nodes](#nodes).

## 2. Nodes Layer
A network of nodes with neighbours connected to each others representing the multiblocks.

## 3. Stream Layer
Streams from source nodes to receiver nodes over relay and antenna nodes.