//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.5.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.5.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import java.nio.file.{Files, Paths}
import java.io.{BufferedWriter, FileWriter, PrintWriter, File}
import scala.sys.process._
import scala.io.Source
import scala.collection.mutable.Queue

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

class TestPipeline(startWidth: Int, l: Int, m: Int, n: Int) extends TestPipelineIO(startWidth) {

//  val myReg = RegInit(0.U(8.W))

  val in = Wire(Flipped(Decoupled(UInt(startWidth.W))))
  in.valid := in_valid
  in_ready := in.ready
  in.bits := in_bits

  val out = Wire(Decoupled(UInt(startWidth.W)))
  out_valid := out.valid
  out.ready := out_ready
  out_bits := out.bits

  val stages = l

  val fabric = new NodeFabric(startWidth)
  val modules: Seq[Module] = (0 until stages).map { i => fabric.GenModule(i, m, n) }
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
  val project = args(0)
  val part = args(1)
  val partpath = args(2)
  val l = args(3).toInt
  val m = args(4).toInt
  val n = args(5).toInt

  for (i <- 0 until n) {
    val outdir = s"gen_${l}_${m}_${i}"
    val path = Paths.get(outdir)
    if (!Files.exists(path)) {
      Files.createDirectories(path)
    }

    val fileMakefile = new File(s"$outdir/Makefile")
    fileMakefile.createNewFile()
    val writerMakefile = new BufferedWriter(new FileWriter(fileMakefile))

    writerMakefile.write(s"""
FAMILY  = artix7
PART    = $part
BOARD   = arty
PROJECT = $project
CHIPDB  = ../../../chipdb/$${ARTIX7_CHIPDB}
TOP_VERILOG=$$(PROJECT).sv
#SYNTH_OPTS = -nodsp
include ../../openXC7.mk
""")
    writerMakefile.close()

    ChiselStage.emitFIRRTLDialect(new TestPipeline(64, l, m, n))
    ChiselStage.emitSystemVerilog(new XDCGen(() => new TestPipelineIO(64), outdir, project, part, partpath))
    ChiselStage.emitSystemVerilogFile(new TestPipeline(64, l, m, n), Array("--target-dir", outdir), firtoolOpts=Array("--lowering-options=disallowLocalVariables,disallowPackedArrays"))

    val stdoutFile = new File(s"$outdir/stdout.log")
    val stdoutWriter = new PrintWriter(new FileWriter(stdoutFile))
    val stderrFile = new File(s"$outdir/stderr.log")
    val stderrWriter = new PrintWriter(new FileWriter(stderrFile))

    print(s"Running $outdir... ")
    val process = Process("make", new java.io.File(outdir))
    val logger = ProcessLogger(
      line => {stdoutWriter.println(line); stdoutWriter.flush()},
      line => {stderrWriter.println(line); stderrWriter.flush()}
    )
    val exitCode = process ! logger
    stdoutWriter.close()
    stderrWriter.close()

    val source = Source.fromFile(s"$outdir/stderr.log")
    var info = ""
    for (line <- source.getLines()) {
      if (line.contains("SLICE_LUTX:")) {
        val pattern = """.*(SLICE_LUTX:\s+\d+/\d+).*""".r
        info = line match {
          case pattern(extracted) => extracted
          case _ => ""
        }
      }
    }
    source.close()
    print(info)

    if (exitCode != 0) {
      val source = Source.fromFile(s"$outdir/stderr.log")
      val queue = new Queue[String]()
      for (line <- source.getLines()) {
        if (queue.size >= 3) queue.dequeue()
        queue.enqueue(line)
      }
      source.close()
      val errstr = queue.toList
      print("ERROR: ")
      println(errstr)
    }
    else {
      println(s": OK")
    }
  }
}
