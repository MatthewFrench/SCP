
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Connext distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Connext manual.
*/
    
package lib;
        

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;

import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;


public class SimpleValue implements Copyable, Serializable
{

    public short srcId = 0;
    public short trgId = 0;
    public short value = 0;


    public SimpleValue() {

    }


    public SimpleValue(SimpleValue other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        SimpleValue self;
        self = new SimpleValue();
         
        self.clear();
        
        return self;
    }

    public void clear() {
        
        srcId = 0;
            
        trgId = 0;
            
        value = 0;
            
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        SimpleValue otherObj = (SimpleValue)o;



        if(srcId != otherObj.srcId) {
            return false;
        }
            
        if(trgId != otherObj.trgId) {
            return false;
        }
            
        if(value != otherObj.value) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += (int)srcId;
                
        __result += (int)trgId;
                
        __result += (int)value;
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>SimpleValueTypeSupport</code>
     * rather than here by using the <code>-noCopyable</code> option
     * to rtiddsgen.
     * 
     * @param src The Object which contains the data to be copied.
     * @return Returns <code>this</code>.
     * @exception NullPointerException If <code>src</code> is null.
     * @exception ClassCastException If <code>src</code> is not the 
     * same type as <code>this</code>.
     * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
     */
    public Object copy_from(Object src) {
        

        SimpleValue typedSrc = (SimpleValue) src;
        SimpleValue typedDst = this;

        typedDst.srcId = typedSrc.srcId;
            
        typedDst.trgId = typedSrc.trgId;
            
        typedDst.value = typedSrc.value;
            
        return this;
    }


    
    public String toString(){
        return toString("", 0);
    }
        
    
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        
                        
        
        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }
        
        
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("srcId: ").append(srcId).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("trgId: ").append(trgId).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("value: ").append(value).append("\n");
            
        return strBuffer.toString();
    }
    
}

