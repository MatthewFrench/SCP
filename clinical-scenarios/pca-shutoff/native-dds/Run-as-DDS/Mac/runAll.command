DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
open -a Terminal.app runCapnometer.command & open -a Terminal.app  ./runInfusionPump.command & open -a Terminal.app  ./runPulseOximeter.command & open -a Terminal.app  ./runPCAMonitor.command