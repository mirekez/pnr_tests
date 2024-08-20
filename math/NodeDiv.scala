package PnrTests

import chisel3._
import chisel3.util._

import chisel3.experimental.ChiselAnnotation
import chisel3.experimental.annotate

/*
case class AttributeAnnotation(target: ReferenceTarget) extends SingleTargetAnnotation[firrtl.annotations.ReferenceTarget] {
  def targets = Seq(target)
  def duplicate(n: ReferenceTarget) = this.copy(n)
}*/
/*
case class DontTouchAnnotation(target: firrtl.annotations.ReferenceTarget) extends firrtl.annotations.SingleTargetAnnotation[firrtl.annotations.ReferenceTarget] {
  def targets = Seq(target)
  def duplicate(n: firrtl.annotations.ReferenceTarget) = this.copy(n)
}*/


class NodeDiv(divType: Int, inCtrl: Int, inWidth: Int, outCtrl: Int, outWidth: Int) extends Module {

  val in = IO(Flipped(Decoupled(UInt(inWidth.W))))
  val out = IO(Decoupled(UInt(outWidth.W)))

  val in_reg = Reg(UInt(inWidth.W))
  val out_reg = Reg(UInt(outWidth.W))

  if (divType > 3) {
    annotate(new ChiselAnnotation {
        def toFirrtl = firrtl.AttributeAnnotation(in_reg.toTarget, "use_dsp = \"no\"")
      })
    annotate(new ChiselAnnotation {
        def toFirrtl = firrtl.AttributeAnnotation(out_reg.toTarget, "use_dsp = \"no\"")
      })
  }

  out.bits := out_reg  // using regs to avoid long math chains
  in_reg := in.bits

  in.ready := true.B
  out.valid := true.B
  out_reg := Cat((in_reg>>inCtrl)/(in_reg(inCtrl/2-1,0)), in_reg(outCtrl-1,0))

  if (divType > 1) {
    val data = (
    for (i <- 0 until divType) yield {
      val region_beg = inCtrl+i*(inWidth-inCtrl)/divType
      val region_end = inCtrl+(i+1)*(inWidth-inCtrl)/divType
      in_reg(region_end-1,region_beg)/in_reg(inCtrl/2-1,0)
    }).toVector
    out_reg := Cat(data).asUInt
  }

  out.bits := out_reg  // using regs to avoid long math chains

}
