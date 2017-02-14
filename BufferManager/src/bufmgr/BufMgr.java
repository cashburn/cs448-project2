package bufmgr;

import global.Page;
import global.PageId;
import chainexception.ChainException;
public class BufMgr {
	public BufMgr(int numbufs, int lookAheadSize, String replacementPolicy) {

	}
	public void pinPage(PageId pageno, Page page, boolean emptyPage)  throws ChainException {
		//throw new DiskMgrException(new Exception("Hello"), "Test");
	}

	public void unpinPage(PageId pageno, boolean dirty) throws ChainException {

	} 
/**
* Allocate new pages.
* Call DB object to allocate a run of new pages and
* find a frame in the buffer pool for the first page
* and pin it. (This call allows a client of the Buffer Manager
* to allocate pages on disk.) If buffer is full, i.e., you
* can't find a frame for the first page, ask DB to deallocate
* all these pages, and return null.
*
* @param firstpage the address of the first page.
* @param howmany total number of allocated new pages.
*
* @return the first page id of the new pages.__ null, if error.
*/
public PageId newPage(Page firstpage, int howmany) {
	return new PageId();
}
public void freePage(PageId globalPageId) throws ChainException {}
/**
* Used to flush a particular page of the buffer pool to disk.
* This method calls the write_page method of the diskmgr package.
*
* @param pageid the page number in the database.
*/
public void flushPage(PageId pageid) {}
/**
* Used to flush all dirty pages in the buffer pool to disk
*
*/
public
void flushAllPages() {}
/**
* Returns the total number of buffer frames.
*/
public int getNumBuffers() {
	return 0;
}
public int getNumUnpinned() {
	return 0;
}
}