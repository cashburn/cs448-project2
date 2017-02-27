package heap; 

import global.* ;
import chainexception.ChainException;
import java.util.*;

public class HeapFile{
	protected ArrayList<PageId> pageList;
	protected ArrayList<Integer> pidList;
	protected int count;
	protected HFPage curr;
	protected HFPage first;


	public HeapFile(String name){
		Page p = new Page();
		pageList = new ArrayList<PageId>();
		pidList = new ArrayList<Integer>();

		if(name != null){
			//check to see if exists
			PageId page = Minibase.DiskManager.get_file_entry(name);
			//System.out.println("Page --- " + page);

			if(page == null){
				page = Minibase.BufferManager.newPage(p, 1);

				//write to disk
				Minibase.DiskManager.add_file_entry(name, page);

				//need to unpin the page for now
				Minibase.BufferManager.unpinPage(page, true);
				pageList.add(page);
				pidList.add(page.pid);
				Minibase.BufferManager.pinPage(page, p, false);
				first = curr = new HFPage(p);
				curr.setCurPage(page);
				first.setCurPage(page);
				Minibase.BufferManager.unpinPage(page, true);
				count = 0;

				return;
			}

			//Page already exists
			//pin it
			Minibase.BufferManager.pinPage(page, p, false);

			first = curr = new HFPage(p);
			//System.out.println("GET DATA -> " + p);
			curr.setData(p.getData());
			first.setData(p.getData());
			pageList.add(page);
			pidList.add(page.pid);
			Minibase.BufferManager.unpinPage(page, false);
			RID x = curr.firstRecord();
			while(x != null){
				count++;
				x = curr.nextRecord(x);
			}


			PageId cPage = curr.getNextPage();
			//System.out.println("ALSO HERE: "+ curr.getCurPage().pid);
			//System.out.println("HERE ======= " + cPage.pid);
			while(cPage.pid > 0){
				HFPage y = new HFPage();
				Minibase.BufferManager.pinPage(cPage, y, false);
				pageList.add(cPage);
				pidList.add(cPage.pid);

				x = y.firstRecord();
				// System.out.println("YOO -- " + x);
				while(x != null){
					count++;
					x = y.nextRecord(x);
				}

				Minibase.BufferManager.unpinPage(cPage, false);
				cPage = y.getNextPage();
				//System.out.println("page - " + cPage);
			}
			//System.out.println("Num files - " + pageList.size());
		} else {
			//passed name is null temp file
			PageId page = global.Minibase.DiskManager.get_file_entry(name);
			curr = new HFPage(p);
			curr.setCurPage(page);
			pageList.add(page);
			pidList.add(page.pid);
			Minibase.BufferManager.unpinPage(page, true);
		}
	}

	public RID insertRecord(byte[] record)throws ChainException, SpaceNotAvailableException{
		if(record.length + HFPage.HEADER_SIZE > 1024){
			throw new SpaceNotAvailableException("E");
		}
		int l = record.length; 

		//search for space in an existing page
		for (int i = 0 ;i < pageList.size(); i++){
			PageId page = pageList.get(i);
			Page p = new Page();

			Minibase.BufferManager.pinPage(page, p, false);
			HFPage hf = new HFPage(p);
			hf.setCurPage(page);
			hf.setData(p.getData());
			if(hf.getFreeSpace() > l){
				RID ret = hf.insertRecord(record);
				Minibase.BufferManager.unpinPage(page, true);
				count++;
				return ret;
			}
			Minibase.BufferManager.unpinPage(page, false);
		}

		//No existing page can house our record so make a new one
		Page p = new Page();
		PageId page = Minibase.BufferManager.newPage(p, 1);

		HFPage hf = new HFPage(p);
		hf.initDefaults();
		hf.setCurPage(page);

		RID ret = hf.insertRecord(record);
		//hf.setNextPage(page);
		hf.setPrevPage(curr.getCurPage());
		curr.setNextPage(page);
		curr = hf;

		pageList.add(page);
		pidList.add(page.pid);

		Minibase.BufferManager.unpinPage(page, true);

		count++;
		return ret;
	}

	/*Todo - not sure about this one*/
	public Tuple getRecord(RID rid)throws ChainException{
		// PageId n;
		// RID i = first.firstRecord();
		// System.out.println("First record - " + pageList.size());
		// HFPage t = first;
		// while(true){
		// 	if(i == null){
		// 		if((n = t.getNextPage()) == null){
		// 			break;
		// 		} else {
		// 			if(t != first){
		// 				Minibase.BufferManager.unpinPage(n, false);
		// 			}
		// 			t = new HFPage();
		// 			Minibase.BufferManager.pinPage(n, t, false);
		// 			i = t.firstRecord();
		// 		}
		// 	}
		// 	if(i.equals(rid)){
		// 		break;
		// 	}
		// 	i = t.nextRecord(i);
		// }
		// byte[] ret = t.selectRecord(i);
		// Tuple tup = new Tuple(ret, 0, ret.length);
		try{
		PageId n = rid.pageno;
		Page x = new Page();
		HFPage t = new HFPage();
		Minibase.BufferManager.pinPage(n,t,false);
		RID i = t.firstRecord();
		while(!i.equals(rid)){
			i = t.nextRecord(i);
		}
		Minibase.BufferManager.unpinPage(n, false);
		byte[] ret = t.selectRecord(i);
		Tuple tup = new Tuple(ret, 0, ret.length);
		return tup;
		} catch (Exception e) {
			throw new InvalidUpdateException();
		}
	}

	public boolean updateRecord(RID rid, Tuple newRecord) throws ChainException{
		try{
			PageId page = rid.pageno;
			HFPage p = new HFPage();
			Minibase.BufferManager.pinPage(page, p, false);
			p.updateRecord(rid, newRecord);
			Minibase.BufferManager.unpinPage(page, false);
		} catch (Exception e){
			throw new InvalidUpdateException();
		}
		return true;

	}

	public boolean deleteRecord(RID rid) throws ChainException{
		try{
		PageId page = rid.pageno;
		HFPage p = new HFPage();
		Minibase.BufferManager.pinPage(page, p, false);
		p.deleteRecord(rid);
		Minibase.BufferManager.unpinPage(page, false);
		count--;
		return true;
		} catch (Exception e) {
			throw new InvalidUpdateException();
		}
	}

	public int getRecCnt(){return count;}

	/*Todo - not sure about this one either */
	public HeapScan openScan(){return new HeapScan(this);}
}