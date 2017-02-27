package heap;
import java.util.*;
import global.* ;
import chainexception.ChainException;
public class HeapScan{
    HeapFile heapfile;
    HFPage curr;
    RID ourRid;
    Iterator<PageId> pageList;

    protected HeapScan(HeapFile hf){
        heapfile = hf;
        //System.out.println("SIZE => " + hf.pageList.size());
        pageList = hf.pageList.iterator();
        Page p = new Page();
        Minibase.BufferManager.pinPage(pageList.next(), p, false);
        curr = new HFPage(p);
        ourRid = curr.firstRecord();
    }

    protected void finalize() throws Throwable{
        //heapfile = null;
        pageList = null;
        ourRid = null;
        curr = null;
    }

    public void close()throws ChainException{
        try{
            finalize();
        } catch (Throwable e) {
            System.out.println("it me");
        }
        heapfile = null;
    }

    public boolean hasNext(){
        return pageList.hasNext();
    }

    public Tuple getNext(RID rid){
        // System.out.println("IT MEEE +++++ => " + heapfile);
        if(ourRid == null){
            if(pageList.hasNext()){
                //System.out.println("HAS NEXT");
                Minibase.BufferManager.unpinPage(curr.getCurPage(), false);
                PageId x = pageList.next();
                // System.out.println(curr + " SIZE");
                Minibase.BufferManager.pinPage(x, curr, false);
                // System.out.println(curr + " SIZE 2");
                ourRid = curr.firstRecord();
                if(ourRid == null){
                    Minibase.BufferManager.unpinPage(x, false);
                }
            } else {
                Minibase.BufferManager.unpinPage(curr.getCurPage(), false);
            }
        } 
        if(ourRid != null)
        {
            rid.copyRID(ourRid);
            ourRid = curr.nextRecord(ourRid);
            Tuple ret = new Tuple(curr.selectRecord(rid), 0, curr.selectRecord(rid).length);
            return ret;
        }

        return null;
    }
}