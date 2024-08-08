//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.5.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.5.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage

import PnrTests.NodeFabric
import PnrTests.XDCGen

class TestPipelineIO(startWidth: Int) extends Module {
  val in_valid = IO(Input(Bool()))
  val in_ready = IO(Output(Bool()))
  val in_bits = IO(Input(UInt(startWidth.W)))
  val out_valid = IO(Output(Bool()))
  val out_ready = IO(Input(Bool()))
  val out_bits = IO(Output(UInt(startWidth.W)))

  in_ready := DontCare
  out_valid := DontCare
  out_bits := DontCare
}

class TestPipeline(startWidth: Int) extends TestPipelineIO(startWidth) {

//  val myReg = RegInit(0.U(8.W))

  val in = Wire(Flipped(Decoupled(UInt(startWidth.W))))
  in.valid := in_valid
  in_ready := in.ready
  in.bits := in_bits

  val out = Wire(Decoupled(UInt(startWidth.W)))
  out_valid := out.valid
  out.ready := out_ready
  out_bits := out.bits

  val stages = 9

  val fabric = new NodeFabric(startWidth)
  val modules: Seq[Module] = (0 until stages).map { i => fabric.GenModule(i) }
  fabric.ChainModules(modules, in, out)


//  out := myReg

//  when(a && b && c) {
//    myReg := foo
//  }
//  when(d && e && f) {
//    myReg := bar
//  }
}

object Main extends App {
  ChiselStage.emitSystemVerilog(new XDCGen(() => new TestPipelineIO(64), args(0), args(1), args(2)))
  ChiselStage.emitSystemVerilogFile(new TestPipeline(64), firtoolOpts=Array("--lowering-options=disallowLocalVariables"))
}
