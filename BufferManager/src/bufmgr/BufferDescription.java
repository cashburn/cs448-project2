package bufmgr;

import diskmgr.*;
import global.Page;
import global.PageId;
//import chainexception.ChainException;
public class BufferDescription {
    public int pageNumber;
    public int pinCount;
    public boolean isDirty;

    public BufferDescription() {
        pageNumber = new PageId().pid;
        pinCount = 0;
        isDirty = false;
    }

    //Test Constructor
    public BufferDescription(int pageNumber, int pinCount, boolean isDirty) {
        this.pageNumber = pageNumber;
        this.pinCount = pinCount;
        this.isDirty = isDirty;
    }

}
