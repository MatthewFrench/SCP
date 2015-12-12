export NDDSHOME=$HOME"/RTI/ndds.5.1.0"
export RTI_LICENSE_FILE=$HOME"/RTI/rti_license.dat"
export RTI_EXAMPLE_ARCH=x64Linux2.6gcc4.4.5jdk
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$NDDSHOME/lib/$RTI_EXAMPLE_ARCH

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
cd ..
cd ..
cd "infusionpump/"
export NDDSHOME=/Applications/rti_connext_dds-5.2.0
export RTI_EXAMPLE_ARCH=x64Darwin12clang4.1
export RTI_JAVA_OPTION=-d64
export DYLD_LIBRARY_PATH=${NDDSHOME}/lib/${RTI_EXAMPLE_ARCH}
gradle run -PdyldLibraryPath="$DYLD_LIBRARY_PATH"