DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
cd ..
cd ..
cd "monitor/"
gradle run -PappArgs="['vertx']"