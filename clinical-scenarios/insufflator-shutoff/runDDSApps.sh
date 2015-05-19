DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
cd "scp/bpmonitor/"
griffon run-app dds &
cd $DIR
cd "scp/insufflationpump/"
griffon run-app dds &
cd $DIR
cd "scp/monitor/"
griffon run-app dds &