# pnr_tests
Random Verilog/RTL code generator designed for testing purposes for Synthesis and Place and Routing tools

Uses scala-cli for Chisel HDL

Took from here: https://scala-cli.virtuslab.org/install/

Also requires PIN-description file.
Uses nextpnr (https://github.com/openXC7/nextpnr-xilinx) to compile design.

# development
Currently tested only under Ubuntu 22.04

# license
This software is distributed under GPLv3.

# architecture
There are 3 different topologies made in pnr_tests and more topologies can be added.
Each topology is build from Nodes, each Node is logic combinational function (no registers inside it yet).
One array of data is collected from random inputs, passes all pipeline, and goes to random output pins.
Small random number of data bits considered as control and connected to logic function control pins and to pins which control the flow.
The simplest topology is PIPELINE - it is a linear sequence of Nodes (currently registers between Nodes appear randomly so the generator gives a lot of very long chains, there is a small patch to force adding registers between Nodes which makes chains shorter).
The next topology is MESH - is it X*Y 2-D array of Nodes. Signal arrives to all rows simultaneously. Each 2 neighbor rows swap random part of their data between each Node. This makes a lot of diagonal data paths between them (including control).
The last topology is STAR. It is very similar to PIPELINE but control from first Node goes to all other Nodes. This makes randomly high fanout number involved in processing data. It is a good idea to combine different topologies in one design which in not difficult to implement.
Each Node randomly infers MUX/DEMUX, QUEUE, MATH(MUL/DIV), ENCODER/DECODER and may be more functions to this moment.
