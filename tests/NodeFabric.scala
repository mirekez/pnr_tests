package PnrTests

import chisel3._
import chisel3.util._
import scala.util.Random
import scala.annotation.unused

class NullModule extends Module {}

class NodeFabric(startWidth: Int) {

  val random = new Random()

  var currWidth = startWidth
  def GenModule(maxWidth: Int, minWidth: Int, complexity: Int): Module = {

    var nodeType = random.nextInt(7)
    val nodeSubtype = random.nextInt(6)
    if (currWidth < 16) {
      nodeType = 2;
    }
    if (nodeType == 0) {
      val inWidth = currWidth
      val inCtrlWidth = minWidth max random.nextInt(inWidth/2)
      val outWidth = currWidth
      val outCtrlWidth = inCtrlWidth
      currWidth = outWidth
      print(s"[Q${nodeSubtype}.${currWidth}] ");
      Module(new NodeQueue(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth, complexity))
    } else
    if (nodeType == 1) {
      val inWidth = currWidth
      val inCtrlWidth = minWidth max random.nextInt(inWidth/2)
      val outWidth = minWidth*2 max random.nextInt(currWidth/2)
      val outCtrlWidth = minWidth max (outWidth/2 min random.nextInt(inCtrlWidth*2))
      currWidth = outWidth
      print(s"[M${nodeSubtype}.${currWidth}-${outCtrlWidth}] ");
      Module(new NodeMux(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth, complexity))
    } else
    if (nodeType == 2) {
      val inWidth = currWidth
      val inCtrlWidth = minWidth max random.nextInt(inWidth/2)
      val outWidth = minWidth*2 max (maxWidth min random.nextInt(currWidth*4))
      val outCtrlWidth = minWidth max (outWidth/2 min random.nextInt(inCtrlWidth*2))
      currWidth = outWidth
      print(s"[D${nodeSubtype}.${currWidth}-${outCtrlWidth}] ");
      Module(new NodeDemux(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth, complexity))
    } else
    if (nodeType == 3) {
      val inWidth = currWidth
      val inCtrlWidth = minWidth max random.nextInt(inWidth/2)
      val outWidth = currWidth
      val outCtrlWidth = minWidth max (outWidth/2 min random.nextInt(inCtrlWidth*2))
      currWidth = outWidth
      print(s"[MUL${nodeSubtype}.${currWidth}-${outCtrlWidth}] ");
      Module(new NodeMul(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth, complexity))
    } else
    if (nodeType == 4) {
      val inWidth = currWidth
      val inCtrlWidth = minWidth max random.nextInt(inWidth/2)
      val outWidth = currWidth
      val outCtrlWidth = minWidth max (outWidth/2 min random.nextInt(inCtrlWidth*2))
      currWidth = outWidth
      print(s"[DIV${nodeSubtype}.${currWidth}-${outCtrlWidth}] ");
      Module(new NodeDiv(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth, complexity))
    } else
    if (nodeType == 5) {
      val inWidth = currWidth
      val inCtrlWidth = minWidth max random.nextInt(inWidth/2)
      val outWidth = currWidth
      val outCtrlWidth = minWidth max (outWidth/2 min random.nextInt(inCtrlWidth*2))
      currWidth = outWidth
      print(s"[MAP${nodeSubtype}.${currWidth}-${outCtrlWidth}] ");
      Module(new NodeMap(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth, complexity))
    } else
    if (nodeType == 6) {
      val inWidth = currWidth
      val inCtrlWidth = minWidth max random.nextInt(inWidth/2)
      val outWidth = currWidth
      val outCtrlWidth = minWidth max (outWidth/2 min random.nextInt(inCtrlWidth*2))
      currWidth = outWidth
      print(s"[MEM${nodeSubtype}.${currWidth}-${outCtrlWidth}] ");
      Module(new NodeMemory(nodeSubtype, inCtrlWidth, inWidth, outCtrlWidth, outWidth, complexity))
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
      case m: NodeMemory =>
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
            case n: NodeMemory =>
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
            case n: NodeMemory =>
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
            case n: NodeMemory =>
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
            case n: NodeMemory =>
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
            case n: NodeMemory =>
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
            case n: NodeMemory =>
              n.in <> m.out
        }
        case m: NodeMemory =>
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
            case n: NodeMemory =>
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
      case m: NodeMemory =>
        out <> m.out
    }

  }

}

