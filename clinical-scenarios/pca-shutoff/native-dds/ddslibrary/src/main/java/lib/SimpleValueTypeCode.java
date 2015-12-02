
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package lib;
        
import com.rti.dds.typecode.*;


public class SimpleValueTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[3];

        sm[i]=new StructMember("srcId",false,(short)-1,false,(TypeCode)TypeCode.TC_SHORT,0,false); i++;
        sm[i]=new StructMember("trgId",false,(short)-1,false,(TypeCode)TypeCode.TC_SHORT,1,false); i++;
        sm[i]=new StructMember("value",false,(short)-1,false,(TypeCode)TypeCode.TC_SHORT,2,false); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("SimpleValue",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,sm);
        return tc;
    }
}
