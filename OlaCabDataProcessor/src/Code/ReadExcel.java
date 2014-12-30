package Code;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadExcel {

	private String inputFile;

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public Sheet read() throws IOException {
		File inputWorkbook = new File(inputFile);
		Workbook w;
		Sheet sheet = null;
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			sheet = w.getSheet(0);
		} catch (BiffException e) {
			e.printStackTrace();
		}
		return sheet;
	}
	/*
	 * public static void main(String[] args) throws IOException { ReadExcel
	 * test = new ReadExcel();
	 * test.setInputFile("D:/work/OlaCabs/workspace/ExcelReader/testReaderInput.xls"
	 * ); test.read(); }
	 */

}