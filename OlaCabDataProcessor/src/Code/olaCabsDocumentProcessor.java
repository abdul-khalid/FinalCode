package Code;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

public class olaCabsDocumentProcessor {

	private JFrame frame;
	private JLabel lblExcelFileLocation;
	private JLabel labelInputFile;
	private JLabel lblOutputFolderLocation;
	private JTextField excelFilLocationTextField;
	private JTextField inputFileLocationTextField;
	private JTextField outputFileLocationTextField;
	private JButton btnNewButton;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					olaCabsDocumentProcessor window = new olaCabsDocumentProcessor();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public olaCabsDocumentProcessor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 586, 269);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		excelFilLocationTextField = new JTextField();
		excelFilLocationTextField.setBounds(238, 38, 309, 20);
		excelFilLocationTextField.setColumns(10);
		frame.getContentPane().add(excelFilLocationTextField);

		btnNewButton = new JButton("Click me to process files");
		btnNewButton.setBounds(238, 164, 309, 36);
		frame.getContentPane().add(btnNewButton);

		lblExcelFileLocation = new JLabel(
				"Excel File to process with location:");
		lblExcelFileLocation.setBounds(28, 41, 200, 14);
		frame.getContentPane().add(lblExcelFileLocation);

		inputFileLocationTextField = new JTextField();
		inputFileLocationTextField.setBounds(238, 79, 309, 20);
		inputFileLocationTextField.setColumns(10);
		frame.getContentPane().add(inputFileLocationTextField);


		labelInputFile = new JLabel("Input Folder Location:");
		labelInputFile.setBounds(28, 82, 164, 14);
		frame.getContentPane().add(labelInputFile);

		lblOutputFolderLocation = new JLabel("Output Folder Location:");
		lblOutputFolderLocation.setBounds(28, 118, 164, 14);
		frame.getContentPane().add(lblOutputFolderLocation);

		outputFileLocationTextField = new JTextField();
		outputFileLocationTextField.setBounds(238, 115, 309, 20);
		outputFileLocationTextField.setColumns(10);
		frame.getContentPane().add(outputFileLocationTextField);
		
		/*Functin to process files*/
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String excelFileAddress = excelFilLocationTextField.getText()
						.trim();
				String inputFileFileAddress = inputFileLocationTextField
						.getText().trim();
				String outputFileFileAddress = outputFileLocationTextField
						.getText().trim();
				try {
					/*Object of ReadExcel class*/
					ReadExcel test = new ReadExcel();
					test.setInputFile(excelFileAddress);
					Sheet s1 = test.read();

					String[] columnNameArr = new String[s1.getColumns()];
					String[] carNumArr = new String[s1.getRows() - 1];
					String[] fileNumberNameArr = new String[s1.getRows() - 1];
					String[] operatorIdArr = new String[s1.getRows() - 1];
					String[] driverIdArr = new String[s1.getRows() - 1];

					int excelColumnIterator;
					for (excelColumnIterator = 0; excelColumnIterator < s1.getColumns(); excelColumnIterator++) {
						columnNameArr[excelColumnIterator] = s1.getCell(excelColumnIterator, 0).getContents();
					}
					for (excelColumnIterator = 1; excelColumnIterator < s1.getRows(); excelColumnIterator++) {
						carNumArr[excelColumnIterator - 1] = s1.getCell(1, excelColumnIterator).getContents();
						fileNumberNameArr[excelColumnIterator - 1] = s1.getCell(0, excelColumnIterator)
								.getContents() + ".pdf";
						operatorIdArr[excelColumnIterator - 1] = s1.getCell(2, excelColumnIterator).getContents();
						driverIdArr[excelColumnIterator - 1] = s1.getCell(12, excelColumnIterator).getContents();
					}
					String inputFolderLocation = inputFileFileAddress;
					String outputFolderLocation = outputFileFileAddress;

					for (int excelRowIterator = 1; excelRowIterator < s1.getRows(); excelRowIterator++) {
						String carNum = carNumArr[excelRowIterator - 1];
						String operatorId = operatorIdArr[excelRowIterator - 1];
						String driverId = driverIdArr[excelRowIterator - 1];
						int[] splittedPageSizeArr = new int[s1.getColumns() - 3];

						for (excelColumnIterator = 3; excelColumnIterator < s1.getColumns(); excelColumnIterator++) {
							Cell cell = s1.getCell(excelColumnIterator, excelRowIterator);
							CellType type = cell.getType();
							int x = 0;
							if (type != CellType.EMPTY) {
								x = Integer.parseInt(cell.getContents()
										.toString());
							}
							splittedPageSizeArr[excelColumnIterator - 3] = x;
						}

						String baseInputFileLocation = inputFolderLocation;
						inputFolderLocation = inputFolderLocation
								+ fileNumberNameArr[excelRowIterator - 1];
						
						/*Call to splitPDFFile function*/
						splitPDFFile(inputFolderLocation, splittedPageSizeArr,
								carNum, operatorId, driverId,
								outputFolderLocation);
						
						inputFolderLocation = baseInputFileLocation;
					}
					JOptionPane.showMessageDialog(null,
							"File processing is complete... :)");
				} catch (Exception error) {
					JOptionPane
							.showMessageDialog(
									null,
									"Champ something wrong happened, please refer documentation to use this application");
					System.out.println("Error: " + error);
				}

			}
		});
	}

	// Helper function{Start}
	/**
	 * @param inputFileNameWithLocation
	 *            : PDF file that has to be splitted
	 * @param splittedPageSize
	 *            : Page size of each splitted files
	 */
	public static void splitPDFFile(String inputFileNameWithLocation,
			int[] splittedPageSizeArr1, String carNum, String operatorId,
			String driverId, String outputLocation) {
		try {
			// System.out.println("Called splitPdf method.");
			/**
			 * Read the input PDF file
			 */
			PdfReader reader = new PdfReader(inputFileNameWithLocation);
			System.out.println("Successfully read input file: "
					+ inputFileNameWithLocation + "\n");

			int totalPages = reader.getNumberOfPages();
			int split = 0;

			/**
			 * Note: Page numbers start from 1 to n (not 0 to n-1)
			 */
			for (int pageNum = 1; pageNum <= totalPages;) {
				// Name of each output file
				System.out.println("split is=" + split
						+ " and splittedPageSizeArr1[" + split + "]="
						+ splittedPageSizeArr1[split] + " pageNum=" + pageNum);

				if (splittedPageSizeArr1[split] > 0 && split != 9) {
					String docType = "Others" + split;
					String folderType = "others";

					// creating a 2-d array to determine the foldertype and
					// doctype.
					String[][] arr = {
							{ "rc", "insurance", "fitness", "touristpermit",
									"contractcarriagepermit", "puc", "tax_hr",
									"tax_up", "tax_mcd", "none", "dl", "badge",
									"additionaaddress", "police_verification",
									"agreement", "pancard", "passbook",
									"cancelcheque" },
							{ "car", "car", "car", "car", "car", "drivers",
									"car", "car", "car", "none", "drivers",
									"drivers", "drivers", "drivers", "car",
									"operator", "operator", "operator" } };
					if (split >= 0 && split <= 17) {
						docType = arr[0][split];
						folderType = arr[1][split];
					}					

					/*Setting name of output file*/
					String outputFileName = null;
					if (folderType.equals("car")) {
						new File(outputLocation + "car/" + carNum).mkdir();
						outputFileName = outputLocation + folderType + "/"
								+ carNum + "/" + operatorId + "_" + carNum
								+ "_" + docType + ".pdf";
					}
					if (folderType.equals("operator")) {
						new File(outputLocation + "operator/" + operatorId)
								.mkdir();
						outputFileName = outputLocation + folderType + "/"
								+ operatorId + "/" + operatorId + "_" + docType
								+ ".pdf";
					}
					if (folderType.equals("drivers")) {
						new File(outputLocation + "drivers/" + driverId)
								.mkdir();
						outputFileName = outputLocation + folderType + "/"
								+ driverId + "/" + operatorId + "_" + driverId
								+ "_" + docType + ".pdf";
					}

					Document document = new Document(
							reader.getPageSizeWithRotation(1));

					PdfCopy writer = new PdfCopy(document,
							new FileOutputStream(outputFileName));

					document.open();

					int tempPageCount = 0;

					for (int offset = 0; offset < splittedPageSizeArr1[split]
							&& (pageNum + offset) <= totalPages; offset++) {
						PdfImportedPage page = writer.getImportedPage(reader,
								pageNum + offset);

						writer.addPage(page);
						tempPageCount++;
					}

					document.close();
					/**
					 * The following will trigger the PDF file being written to
					 * the system
					 **/
					writer.close();
					System.out.println("Split: [" + tempPageCount + " page]: "
							+ outputFileName);
					pageNum += splittedPageSizeArr1[split];
				}
				split++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Helper function{END}.

}
