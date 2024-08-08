FAMILY  = artix7
PART    = xc7a100tcsg324-2
#xc7a200tffg1156
BOARD   = arty
PROJECT = TestPipeline
CHIPDB  = ${ARTIX7_CHIPDB}

ADDITIONAL_SCALA_SOURCES= \
../../queue/NodeQueue.scala \
../../mux/NodeMux.scala \
../NodeFabric.scala \
../XDCGen.scala

TOP_VERILOG=$(PROJECT).sv
include ../openXC7.mk

%.sv: %.scala
	scala-cli $(ADDITIONAL_SCALA_SOURCES) $< -- $(PROJECT) $(PART) $(PRJXRAY_DB_DIR)/$(FAMILY)/$(PART)/$(xc7a100tcsg324-2)
