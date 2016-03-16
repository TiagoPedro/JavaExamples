package pcd.gameengine;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class TestCell {
	private class SnakeDummy extends Thread{
		private Board board;
		private Cell cell;
		
		private SnakeDummy(Board board, Cell cell){
			this.board = board;
			this.cell = cell;
		}
		
		private boolean checkIfCellIsEmpty(Cell cell){
			if(cell.isCellEmpty()){
				return true;
			}
			return false;
		}
		
		@Override
		public void run() {
			while(true){
				checkIfCellIsEmpty(cell);
				checkIfCellIsEmpty(cell);
			}
		}
	}
	
	Board board;
	Cell cell;
	int x;
	int y;
	SnakeDummy dummy;
	
	@Before
	public void setUpTest() {
		x = new Random().nextInt(30);
		y = new Random().nextInt(30);
		cell = new Cell(x, y);
		board = new Board(Math.max(x, y), 1, 0);
		dummy = new SnakeDummy(board, cell);
	}
	
	@Test
	public void testCoordinateGetters(){
		assertEquals(x, cell.getX());
		assertEquals(y, cell.getY());
	}

	@Test
	public void testIfCellIsEmpty() throws InterruptedException {
		assertTrue(cell.isCellEmpty());
	}
	
	@Test
	public void testIfCellIsNotEmpty() {
		cell.setEmpty(false);
			
		dummy.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dummy.interrupt();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized(cell){
			cell.notifyAll();
		}
		assertFalse(cell.getEmptyState());
	}
}
