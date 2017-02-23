package bufmgr;

import java.util.HashMap; //TODO: Remove and implement

//import diskmgr.DiskMgr;
import diskmgr.*;
import global.Page;
import global.PageId;
import chainexception.ChainException;
public class BufMgr {

	private Page[] frames;
	private HashMap<PageId, Integer> pageFrame;
	private String policy;
	private int numFilled;
	private int pinnedPages;
	private DiskMgr disk;

	/**
	* Create the BufMgr object.
	* Allocate pages (frames) for the buffer pool in main memory and
	* make the buffer manage aware that the replacement policy is
	* specified by replacerArg (e.g., LH, Clock, LRU, MRU, LFU, etc.).
	*
	* @param numbufs number of buffers in the buffer pool
	* @param lookAheadSize: Please ignore this parameter
	* @param replacementPolicy Name of the replacement policy, that parameter will be set to "MRU" (you can safely ignore this parameter as you will implement only one policy)
	*/
	public BufMgr(int numbufs, int lookAheadSize, String replacementPolicy) {
		this.frames = new Page[numbufs];
		disk = new DiskMgr();
		this.policy = "MRU";
		pageFrame = new HashMap<PageId, Integer>();
		pinnedPages = 0;
		numFilled = 0;
	}

	/**
	* Pin a page.
	* First check if this page is already in the buffer pool.
	* If it is, increment the pin_count and return a pointer to this
	* page.
	* If the pin_count was 0 before the call, the page was a
	* replacement candidate, but is no longer a candidate.
	* If the page is not in the pool, choose a frame (from the
	* set of replacement candidates) to hold this page, read the
	* page (using the appropriate method from {\em diskmgr} package) and pin it.
	* Also, must write out the old page in chosen frame if it is dirty
	* before reading new page.__ (You can assume that emptyPage==false for
	* this assignment.)
	*
	* @param pageno page number in the Minibase.
	* @param page the pointer point to the page.
	* @param emptyPage true (empty page); false (non-empty page)
	*/
	public void pinPage(PageId pageno, Page page, boolean emptyPage)  throws ChainException {
		//throw new DiskMgrException(new Exception("Hello"), "Test");
	}

	/**
	* Unpin a page specified by a pageId.
	* This method should be called with dirty==true if the client has
	* modified the page.
	* If so, this call should set the dirty bit
	* for this frame.
	* Further, if pin_count>0, this method should
	* decrement it.
	*If pin_count=0 before this call, throw an exception
	* to report error.
	*(For testing purposes, we ask you to throw
	* an exception named PageUnpinnedException in case of error.)
	*
	* @param pageno page number in the Minibase.
	* @param dirty the dirty bit of the frame
	*/
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
	public PageId newPage(Page firstPage, int howmany) {
		return new PageId();
	}
	public void freePage(PageId globalPageId) throws ChainException {}
	/**
	* Used to flush a particular page of the buffer pool to disk.
	* This method calls the write_page method of the diskmgr package.
	*
	* @param pageid the page number in the database.
	*/
	public void flushPage(PageId pageid) {
		int f = pageFrame.get(pageid);
		try {
			disk.write_page(pageid, frames[f]);
		} catch (Exception e) {
			System.err.println(e);
		}


	}
	/**
	* Used to flush all dirty pages in the buffer pool to disk
	*
	*/
	public void flushAllPages() {}
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
