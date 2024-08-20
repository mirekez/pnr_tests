package PnrTests

import chisel3._
import chisel3.util._
import scala.util.Random
import scala.annotation.unused

class NullModule extends Module {}

class NodeFabric(startWidth: Int) {

  val random = new Random()

  var currWidth = startWidth
  def GenModule(@unused i: Int, m: Int,@unused  n: Int): Module = {

    var nodeType = random.nextInt(6)
    val nodeSubtype = random.nextInt(5)
    if (currWidth < 16) {
      nodeType = 2;
    }
    if (nodeType == 0) {
      val numQueues = nodeSubtype+1
      val queueDepth = random.nextInt(m/currWidth+1)+1
      print(s"[Q${nodeSubtype}.${currWidth}] ");
      Module(new NodeQueue(numQueues, queueDepth, currWidth))
    } else
    if (nodeType == 1) {
      val inWidth = currWidth+1
      val inCtrlWidth = random.nextInt(if (inWidth-1<10) inWidth-1 else 10)+1
      val outCtrlWidth = random.nextInt(inCtrlWidth)+1
      val outWidth = random.nextInt(currWidth/2)+1+outCtrlWidth
      currWidth = outWidth
      print(s"[M${nodeSubtype}.${currWidth}] ");
      Module(new NodeMux(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth))
    } else
    if (nodeType == 2) {
      val inWidth = currWidth+1
      val inCtrlWidth = random.nextInt(if (inWidth-1<10) inWidth-1 else 10)+1
      val outCtrlWidth = random.nextInt(inCtrlWidth)+1
      val outWidth = random.nextInt(if (currWidth*4>m) m else currWidth*4)+1+outCtrlWidth
      currWidth = outWidth
      print(s"[D${nodeSubtype}.${currWidth}] ");
      Module(new NodeDemux(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth))
    } else
    if (nodeType == 3) {
      val inWidth = currWidth+1
      val inCtrlWidth = random.nextInt(if (inWidth-1<10) inWidth-1 else 10)+1
      val outCtrlWidth = random.nextInt(inCtrlWidth)+1
      val outWidth = currWidth
      currWidth = outWidth
      print(s"[MUL${nodeSubtype}.${currWidth}] ");
      Module(new NodeMul(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth))
    } else
    if (nodeType == 4) {
      val inWidth = currWidth+1
      val inCtrlWidth = random.nextInt(if (inWidth-1<10) inWidth-1 else 10)+1
      val outCtrlWidth = random.nextInt(inCtrlWidth)+1
      val outWidth = currWidth
      currWidth = outWidth
      print(s"[DIV${nodeSubtype}.${currWidth}] ");
      Module(new NodeDiv(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth))
    } else
    if (nodeType == 5) {
      val inWidth = currWidth+1
      val inCtrlWidth = random.nextInt(if (inWidth-1<10) inWidth-1 else 10)+1
      val outCtrlWidth = random.nextInt(inCtrlWidth)+1
      val outWidth = currWidth
      currWidth = outWidth
      print(s"[MAP${nodeSubtype}.${currWidth}] ");
      Module(new NodeMap(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth))
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
      case m: NodeDemux =>
        m.in <> in
      case m: NodeMul =>
        m.in <> in
      case m: NodeDiv =>
        m.in <> in
      case m: NodeMap =>
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
            case n: NodeDemux =>
              n.in <> m.out
            case n: NodeMul =>
              n.in <> m.out
            case n: NodeDiv =>
              n.in <> m.out
            case n: NodeMap =>
              n.in <> m.out
        }
        case m: NodeMux =>
          modules(i+1) match {
            case n: NodeQueue =>
              n.in <> m.out
            case n: NodeMux =>
              n.in <> m.out
            case n: NodeDemux =>
              n.in <> m.out
            case n: NodeMul =>
              n.in <> m.out
            case n: NodeDiv =>
              n.in <> m.out
            case n: NodeMap =>
              n.in <> m.out
        }
        case m: NodeDemux =>
          modules(i+1) match {
            case n: NodeQueue =>
              n.in <> m.out
            case n: NodeMux =>
              n.in <> m.out
            case n: NodeDemux =>
              n.in <> m.out
            case n: NodeMul =>
              n.in <> m.out
            case n: NodeDiv =>
              n.in <> m.out
            case n: NodeMap =>
              n.in <> m.out
        }
        case m: NodeMul =>
          modules(i+1) match {
            case n: NodeQueue =>
              n.in <> m.out
            case n: NodeMux =>
              n.in <> m.out
            case n: NodeDemux =>
              n.in <> m.out
            case n: NodeMul =>
              n.in <> m.out
            case n: NodeDiv =>
              n.in <> m.out
            case n: NodeMap =>
              n.in <> m.out
        }
        case m: NodeDiv =>
          modules(i+1) match {
            case n: NodeQueue =>
              n.in <> m.out
            case n: NodeMux =>
              n.in <> m.out
            case n: NodeDemux =>
              n.in <> m.out
            case n: NodeMul =>
              n.in <> m.out
            case n: NodeDiv =>
              n.in <> m.out
            case n: NodeMap =>
              n.in <> m.out
        }
        case m: NodeMap =>
          modules(i+1) match {
            case n: NodeQueue =>
              n.in <> m.out
            case n: NodeMux =>
              n.in <> m.out
            case n: NodeDemux =>
              n.in <> m.out
            case n: NodeMul =>
              n.in <> m.out
            case n: NodeDiv =>
              n.in <> m.out
            case n: NodeMap =>
              n.in <> m.out
        }
      }
    }

    modules(modules.length-1) match {
      case m: NodeQueue =>
        out <> m.out
      case m: NodeMux =>
        out <> m.out
      case m: NodeDemux =>
        out <> m.out
      case m: NodeMul =>
        out <> m.out
      case m: NodeDiv =>
        out <> m.out
      case m: NodeMap =>
        out <> m.out
    }

  }

}

