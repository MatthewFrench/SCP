export NDDSHOME=$HOME"/RTI/ndds.5.1.0"
export RTI_LICENSE_FILE=$HOME"/RTI/rti_license.dat"
export RTI_EXAMPLE_ARCH=x64Linux2.6gcc4.4.5jdk
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$NDDSHOME/lib/$RTI_EXAMPLE_ARCH

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
cd ..
cd ..
cd "monitor/"
gradle run -PappArgs="['dds']"