package PnrTests

import chisel3._
import chisel3.util._

class NodeDemux(muxType: Int, inCtrl: Int, inWidth: Int, outCtrl: Int, outWidth: Int) extends Module {

  val in = IO(Flipped(Decoupled(UInt(inWidth.W))))
  val out = IO(Decoupled(UInt(outWidth.W)))

  in.ready := true.B
  out.valid := true.B
  out.bits := Cat(in.bits,in.bits)

  if (muxType == 1 && (inWidth-inCtrl)/(outWidth-outCtrl) > 0) {
    out.bits := (in.bits >> inCtrl) << (in.bits(inCtrl-1,0) + outCtrl.U)
  }

  val cntr = Reg(UInt(8.W))
  val vecsize = (outWidth-outCtrl)/(inWidth-inCtrl)+1
  val buffer = Reg(Vec(vecsize, UInt((inWidth-inCtrl).W)))
  val valid = Reg(Bool())
  valid := false.B
  if (muxType == 2 && vecsize > 1) {  // gearbox
    buffer(cntr(log2Ceil(vecsize)-1,0)) := in.bits >> inCtrl
    cntr := cntr + 1.U
    out.valid := valid

    when (cntr(log2Ceil(vecsize)-1,0) === 0.U) {
      out.bits := buffer.asUInt
      valid := true.B
    }
  }
}
