package PnrTests

import chisel3._
import chisel3.util._

class NodeQueue(numQueues: Int, queueDepth: Int, queueWidth: Int) extends Module {

  val in = IO(Flipped(Decoupled(UInt(queueWidth.W))))
  val out = IO(Decoupled(UInt(queueWidth.W)))

  val queues = Array.fill(numQueues)(Module(new Queue(UInt(queueWidth.W), queueDepth)))

  queues(0).io.enq <> in
  for (i <- 0 until queues.length-1) {
    queues(i+1).io.enq <> queues(i).io.deq
  }
  out <> queues(numQueues-1).io.deq

}
