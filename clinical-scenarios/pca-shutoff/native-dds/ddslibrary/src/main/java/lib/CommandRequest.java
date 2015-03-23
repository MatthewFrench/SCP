
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


public class CommandRequest implements Copyable, Serializable
{

    public lib.Command cmd = (lib.Command) lib.Command.create();


    public CommandRequest() {

    }


    public CommandRequest(CommandRequest other) {

        this();
        copy_from(other);
    }



    public static Object create() {
        CommandRequest self;
        self = new CommandRequest();
         
        self.clear();
        
        return self;
    }

    public void clear() {
        
        cmd = lib.Command.create();
            
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        CommandRequest otherObj = (CommandRequest)o;



        if(!cmd.equals(otherObj.cmd)) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += cmd.hashCode();
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>CommandRequestTypeSupport</code>
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
        

        CommandRequest typedSrc = (CommandRequest) src;
        CommandRequest typedDst = this;

        typedDst.cmd = (lib.Command) typedDst.cmd.copy_from(typedSrc.cmd);
            
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
        
        
        strBuffer.append(cmd.toString("cmd ", indent+1));
            
        return strBuffer.toString();
    }
    
}

