FAMILY  = artix7
PART    = xc7a100tcsg324-2
#xc7a200tffg1156
BOARD   = arty
PROJECT = TestPipeline

SCALA_SOURCES= \
../../queue/NodeQueue.scala \
../../mux/NodeMux.scala \
../NodeFabric.scala \
../XDCGen.scala

TestPipeline:
	scala-cli $(SCALA_SOURCES) TestPipeline.scala $< -- $(PROJECT) $(PART) $(PRJXRAY_DB_DIR)/$(FAMILY)/$(PART)/$(xc7a100tcsg324-2) 10 15 10

include ../openXC7.mk
