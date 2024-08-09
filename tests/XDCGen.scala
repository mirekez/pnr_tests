package PnrTests

import chisel3._

import chisel3.reflect.DataMirror
import java.io.{BufferedWriter, FileWriter, File}
import scala.io.Source
import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.annotation.unused

class XDCGen[T <: Module](moduleFactory: () => T, outdir: String, project: String, @unused part: String, xrayPath: String) extends Module {

  val test = Module(moduleFactory())

  val pins = ListBuffer[String]()
  val sourceCSV = Source.fromFile(s"$xrayPath/package_pins.csv")
  sourceCSV.getLines().next()
  for (line <- sourceCSV.getLines()) {
    val columns = line.split(",")
    if (columns.length >= 4 && !columns(3).contains("GTP") && !columns(3).contains("MONITOR")) {
      pins += columns(0)
    }
  }
  val random = new Random()
  val fileXDC = new File(s"$outdir/$project.xdc")
  fileXDC.createNewFile()
  val writerXDC = new BufferedWriter(new FileWriter(fileXDC))
  DataMirror.modulePorts(test).foreach { case (name, port) => {
    port := DontCare
    if (port.getWidth > 1) {
      for (i <- 0 to port.getWidth) {
        assert(pins.length > 0, "no so many pins in device")
        val randomIndex = random.nextInt(pins.length)
        writerXDC.write(s"set_property IOSTANDARD LVCMOS33 [get_ports $name[$i]]\n")
        writerXDC.write(s"set_property PACKAGE_PIN ${pins(randomIndex)} [get_ports $name[$i]]\n")
        pins.remove(randomIndex)
      }
    } else {
      val randomIndex = random.nextInt(pins.length)
      writerXDC.write(s"set_property IOSTANDARD LVCMOS33 [get_ports $name]\n")
      writerXDC.write(s"set_property PACKAGE_PIN ${pins(randomIndex)} [get_ports $name]\n")
      pins.remove(randomIndex)
    }
  }}
  writerXDC.close()
}
