DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
cd "scp-vertx/bpmonitor/"
griffon run-app &
cd $DIR
cd "scp-vertx/insufflationpump/"
griffon run-app &
cd $DIR
cd "scp-vertx/monitor/"
griffon run-app &