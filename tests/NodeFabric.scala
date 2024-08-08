package PnrTests

import chisel3._
import chisel3.util._
import scala.util.Random
import scala.annotation.unused

class NullModule extends Module {}

class NodeFabric(startWidth: Int) {

  val random = new Random()

  var currWidth = startWidth
  def GenModule(@unused i: Int): Module = {

    val nodeType = random.nextInt(2)
    println(s"Generating: $nodeType");

    if (nodeType == 0) {
      val numQueues = random.nextInt(32)+1
      val queueDepth = random.nextInt(256)+1
      Module(new NodeQueue(numQueues, queueDepth, currWidth))
    } else
    if (nodeType == 1) {
      val inCtrlWidth = random.nextInt(10)+1
      val outCtrlWidth = random.nextInt(inCtrlWidth)+1
      val outWidth = random.nextInt(currWidth)+outCtrlWidth
      val inWidth = currWidth
      currWidth = outWidth
      Module(new NodeMux(0, inCtrlWidth, inWidth, outCtrlWidth, outWidth))
    }
    else {
      Module(new NullModule)
    }
  }

  def ChainModules(modules: Seq[Module], in: DecoupledIO[UInt], out: DecoupledIO[UInt]): Unit = {

    modules(0) match {
      case m: NodeQueue =>
        m.in <> in
      case m: NodeMux =>
        m.in <> in
    }

    for (i <- 0 until modules.length-1) {
      modules(i) match {
        case m: NodeQueue =>
          modules(i+1) match {
            case n: NodeQueue =>
              n.in <> m.out
            case n: NodeMux =>
              n.in <> m.out
        }
        case m: NodeMux =>
          modules(i+1) match {
            case n: NodeQueue =>
              n.in <> m.out
            case n: NodeMux =>
              n.in <> m.out
        }
      }
    }

    modules(modules.length-1) match {
      case m: NodeQueue =>
        out <> m.out
      case m: NodeMux =>
        out <> m.out
    }

  }

}

