install_name_tool -change libnddsc.dylib @loader_path/libnddsc.dylib $DYLD_LIBRARY_PATH/libnddsjava.dylib
install_name_tool -change libnddscore.dylib @loader_path/libnddscore.dylib $DYLD_LIBRARY_PATH/libnddsjava.dylib
install_name_tool -change libnddscore.dylib @loader_path/libnddscore.dylib $DYLD_LIBRARY_PATH/libnddsc.dylib