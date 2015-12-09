package clinicalscenario.monitor

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import lib.Command
import lib.CompletionStatus
import lib.Constants
import lib.DDS
import lib.SimpleValue
import lib.SimpleValueHandlerWrapper
import lib.SimpleValueTypeSupport
import lib.Topics
import com.rti.dds.subscription.DataReaderAdapter
import com.rti.dds.subscription.SampleInfo
import javax.swing.JOptionPane
import com.rti.dds.type.builtin.StringTypeSupport

@ArtifactProviderFor(GriffonController)
class MonitorController {
    MonitorModel model

    def dds = new DDS()

  private allhookedup = 0

  void mvcGroupInit(Map args) {
    dds.initializeCommandSendCapability(Constants.INSUFFLATION_PUMP)
    def tmp1 = new SimpleValueHandlerWrapper(this.&onSYSTOLIC)
    dds.subscribeTo(
        Topics.SYSTOLIC,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onDIASTOLIC)
    dds.subscribeTo(
        Topics.DIASTOLIC,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onPRESSURE)
    dds.subscribeTo(
        Topics.PRESSURE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onPulseRate)
    dds.subscribeTo(
        Topics.PULSE_RATE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
	tmp1 = new SimpleValueHandlerWrapper(this.&onDeviceState)
    dds.subscribeTo(
        Topics.DEVICE_STATE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
	tmp1 = new SimpleValueHandlerWrapper(this.&onSeconds)
    dds.subscribeTo(
        Topics.SECONDS,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
	dds.publishOn(
        Topics.BPFREQUENCY, 
        SimpleValueTypeSupport.get_type_name())
  }

  void mvcGroupDestroy() {
    println('destroying')
    dds.destroy()
  }
  
  def onSeconds(seconds) { 
    //edt { 
    	model.seconds = seconds //}
  }

  def onDeviceState(state) { 
    //edt {
     model.state = state?"Active":"Inactive"// }
	if (state == 1) {
		def tmp1 = new SimpleValue()
			tmp1.value = 1
			dds.publish(Topics.BPFREQUENCY, tmp1)
	} else {
		def tmp1 = new SimpleValue()
			tmp1.value = 0
			dds.publish(Topics.BPFREQUENCY, tmp1)
	}
  }
  
  def onSYSTOLIC(systolic) { 
    //edt {
     model.systolic = systolic// }
    allhookedup |= 0x1
    if (!model.needToStop && allhookedup == 0xF) {
      //app.event "Update"
      onUpdate();
    }
  }

  def onDIASTOLIC(diastolic) {
    //edt { 
    	model.diastolic = diastolic //}
    allhookedup |= 0x2
    if (!model.needToStop && allhookedup == 0xF) {
      //app.event "Update"
      onUpdate();
    }
  }

  def onPRESSURE(pressure) {
    //edt {
     model.pressure = pressure //}
    allhookedup |= 0x4
    if (!model.needToStop && allhookedup == 0xF) {
      //app.event "Update"
      onUpdate();
    }
  }

  def onPulseRate(pr) {
    //edt { 
    	model.pulseRate = pr //}
    allhookedup |= 0x8
    if (!model.needToStop && allhookedup == 0xF) {
      //app.event "Update"
      onUpdate();
    }
  }

  def onUpdate() {
    println("updating $allhookedup")
    def needToStop = model.systolic < 96 || 
      model.diastolic < 50 || model.pulseRate < 50

    if (needToStop) {
      println("stop pump ${model.systolic} ${model.diastolic} ${model.pressure} ${model.pulseRate}")
      CompletionStatus reply = dds.issueCommandTo(Command.STOP,  Constants.INSUFFLATION_PUMP)

      if (reply == CompletionStatus.SUCCESS) {
        //edt {
          JOptionPane.showMessageDialog(null,
              """ALERT: Insufflation pump stopped due to bad vitals.
              systolic: ${model.systolic}
              diastolic: ${model.diastolic}
              pulse rate: ${model.pulseRate}
              pressure: ${model.pressure}
              """, 
              'ALERT', 
              JOptionPane.WARNING_MESSAGE)
        //}
        //edt {
         model.needToStop = true //}
      } else {
        //edt {
          JOptionPane.showMessageDialog(null,
              """PANIC: Insufflation pump non-responsive to stop command.
              systolic: ${model.systolic}
              diastolic: ${model.diastolic}
              pulse rate: ${model.pulseRate}
              pressure: ${model.pressure}
              """,
              'PANIC', 
              JOptionPane.ERROR_MESSAGE)
        //}
      }
    }
  }
}