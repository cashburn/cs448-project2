package bufmgr;

import java.util.HashMap; //TODO: Remove and implement
import java.util.LinkedList;
import java.io.IOException;
//import diskmgr.DiskMgr;
import diskmgr.*;
import global.*;
import chainexception.ChainException;
public class BufMgr {
	//TODO: change to private
	public Page[] frames;
	public HashMap<PageId, Integer> pageFrame;
	public String policy;
	public int numFilled;
	public int pinnedPages;
	public DiskMgr disk;
	public BufferDescription[] descriptions;
	public LinkedList<Integer> replaceList;

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
		frames = new Page[numbufs];
		disk = new DiskMgr();
		replaceList = new LinkedList<Integer>();
		descriptions = new BufferDescription[numbufs];
		policy = "MRU";
		pageFrame = new HashMap<PageId, Integer>();
		pinnedPages = 0;
		numFilled = 0;

		for (int i = 0; i < numbufs; i++) {
			descriptions[i] = new BufferDescription();
		}
		for (int i = 0; i < numbufs; i++) {
			replaceList.addLast(i);
		}
		for (int i = 0; i < numbufs; i++) {
			frames[i] = new Page();
		}
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
	public void pinPage(PageId pageno, Page page, boolean emptyPage)  throws ChainException, IOException {
		//System.out.println("pinPage2");
		Integer frame = pageFrame.get(pageno);
		if (frame != null) {
			if (descriptions[frame].pinCount == 0)
				replaceList.remove(frame);
			descriptions[frame].pinCount++;
			page.setpage(frames[frame].getpage());
		}
		else {
			if (replaceList.size() - 1 == 0) {
				throw new BufferPoolExceededException(new Exception(), "Buffer Full");
			}

			frame = replaceList.pollFirst();
			//System.out.printf("Page %d, Frame %d\n", pageno.pid, frame);
			PageId oldPageId = new PageId(descriptions[frame].pageNumber);
			if (descriptions[frame].isDirty) {
				Minibase.DiskManager.write_page(oldPageId, frames[frame]);
			}
			pageFrame.remove(oldPageId);
			Minibase.DiskManager.read_page(pageno, page);
			pageFrame.put(new PageId(pageno.pid), frame);
			//System.out.println(pageFrame);
			frames[frame].setpage(page.getpage());
			descriptions[frame] = new BufferDescription(pageno.pid, 1, false);
			//System.out.println("End of pinPage");
		}
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
		//System.out.printf("Unpin page: %d", pageno.pid);
		Integer pageNumber = pageFrame.get(pageno);
		if (pageNumber == null) {
			throw new HashEntryNotFoundException(new Exception(), "HashEntryNotFoundException");
		}
		else {
			if (descriptions[pageNumber].pinCount <= 0) {
				throw new ChainException(new Exception(), "PageUnpinnedException");
			}
			else {
				descriptions[pageNumber].isDirty = dirty;
				descriptions[pageNumber].pinCount--;

				if (descriptions[pageNumber].pinCount == 0) {
					if (!replaceList.contains(pageNumber)) {
						replaceList.addFirst(pageNumber);
					}
				}
			}

		}
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
		PageId newPid = new PageId();
		try {
			Minibase.DiskManager.allocate_page(newPid, howmany);
			pinPage(newPid, firstPage, false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ChainException e) {
			e.printStackTrace();
		}
		return newPid;
	}
	/**
	* This method should be called to delete a page that is on disk.
	* This routine must call the method in diskmgr package to
	* deallocate the page.
	*
	* @param globalPageId the page number in the data base.
	*/
	public void freePage(PageId globalPageId) throws ChainException {
		Integer frame = pageFrame.get(globalPageId);
		if (frame != null) {
			if (descriptions[frame].pinCount > 1) {
				throw new PagePinnedException(new Exception(), "Page still pinned");
			}
			else {
				if (descriptions[frame].pinCount == 1)
					unpinPage(globalPageId, false);
				try {
					Minibase.DiskManager.deallocate_page(globalPageId);
				} catch (ChainException e) {
					e.printStackTrace();
				}

				descriptions[frame] = new BufferDescription();
				pageFrame.remove(globalPageId);
				replaceList.remove(frame);
			}
		}
	}
	/**
	* Used to flush a particular page of the buffer pool to disk.
	* This method calls the write_page method of the diskmgr package.
	*
	* @param pageid the page number in the database.
	*/
	public void flushPage(PageId pageid) {
		int f = pageFrame.get(pageid);
		try {
			Minibase.DiskManager.write_page(pageid, frames[f]);
		} catch (Exception e) {
			System.err.println(e);
		}


	}
	/**
	* Used to flush all dirty pages in the buffer pool to disk
	*
	*/
	public void flushAllPages() {
		int len = descriptions.length;
		for (int i = 0; i < len; i++) {
			if (descriptions[i].isDirty)
				flushPage(new PageId(i));
		}
	}
	/**
	* Returns the total number of buffer frames.
	*/
	public int getNumBuffers() {
		return frames.length;
	}
	public int getNumUnpinned() {
		int count = -1;
		for (int i = 0; i < descriptions.length; i++) {
			if (descriptions[i].pinCount == 0)
				count++;
		}
		return count;
	}
	private boolean isFull() {
		for (int i = 0; i < descriptions.length; i++) {
			if (descriptions[i].pinCount == 0)
				return false;
		}
		return true;
	}
}
