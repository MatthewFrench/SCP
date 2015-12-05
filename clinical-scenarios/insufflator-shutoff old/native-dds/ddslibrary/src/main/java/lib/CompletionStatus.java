
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package lib;
        

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;



public class CompletionStatus extends Enum
{

    public static final CompletionStatus SUCCESS = new CompletionStatus("SUCCESS", 0);
    public static final int _SUCCESS = 0;
    
    public static final CompletionStatus FAILURE = new CompletionStatus("FAILURE", 1);
    public static final int _FAILURE = 1;
    


    public static CompletionStatus valueOf(int ordinal) {
        switch(ordinal) {
            
              case 0: return CompletionStatus.SUCCESS;
            
              case 1: return CompletionStatus.FAILURE;
            

        }
        return null;
    }

    public static CompletionStatus from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[2];
        
        
        values[i] = SUCCESS.ordinal();
        i++;
        
        values[i] = FAILURE.ordinal();
        i++;
        

        Arrays.sort(values);
        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
     * Create a default instance
     */  
    public static CompletionStatus create() {
        

        return valueOf(0);
    }
    
    /**
     * Print Method
     */     
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();

        CdrHelper.printIndent(strBuffer, indent);
            
        if (desc != null) {
            strBuffer.append(desc).append(": ");
        }
        
        strBuffer.append(this);
        strBuffer.append("\n");              
        return strBuffer.toString();
    }

    private Object readResolve() throws ObjectStreamException {
        return valueOf(ordinal());
    }

    private CompletionStatus(String name, int ordinal) {
        super(name, ordinal);
    }
}

