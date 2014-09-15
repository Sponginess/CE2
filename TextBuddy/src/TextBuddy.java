/* TextBuddy.java is a text-based program that takes in user input and modifies a file according to that input.
 * If a file with the name input by the user does not exist in the folder, a new file will be created.
 * Otherwise, if it exists, the program will use that file.
 * 
 * Textbuddy has 5 commands available, add, delete, display, clear and exit.
 * 
 * add: Appends the user input as a line in the file. 
 * delete: Takes in a number input by the user and deletes that line from the file if it exists.
 * display: Outputs all the content in the file.
 * clear: Deletes all the content in the file.
 * exit: Terminates the program.
 *
 * The command format is given by the example interaction below:

		c:> java TextBuddy mytextfile.txt
		Welcome to TextBuddy. mytextfile.txt is ready for use
		command: add little brown fox
		added to mytextfile.txt: “little brown fox”
		command: display
		1. little brown fox
		command: add jumped over the moon
		added to mytextfile.txt: “jumped over the moon”
		command: display
		1. little brown fox
		2. jumped over the moon
		command: delete 2
		deleted from mytextfile.txt: “jumped over the moon”
		command: display
		1. little brown fox
		command: clear
		all content deleted from mytextfile.txt
		command: display
		mytextfile.txt is empty
		command: exit
		c:>
  
 * author: Josephine Kwa Yan Xiu
 * matric no: A0113605U
 */

import java.io.*;
import java.util.*;

public class TextBuddy {
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %s is ready for use\n";
	private static final String MESSAGE_SUCCESSFULLY_ADDED = "added to %s: \"%s\"\n";
	private static final String MESSAGE_SUCCESSFULLY_CLEARED = "all content deleted from %s\n";
	private static final String MESSAGE_SUCESSFULLY_DELETED = "deleted from %s: \"%s\"\n";
	private static final String MESSAGE_UNSUCCESSFUL_DELETE = "error: unable to delete, no such line\n";
	private static final String MESSAGE_EMPTY_FILE = "%s is empty\n";
	private static final String MESSAGE_OUTPUT = "%d. %s\n";
	private static final String MESSAGE_FILE_NAME_PROMPT = "error: please enter a file name: ";
	private static final String COMMAND_PROMPT = "command: ";
	private static final String ERROR_MESSAGE_INVALID_COMMAND = "error: invalid command\n";
	private static final String ERROR_MESSAGE_FILE = "error: file error\n";
	private static final String ERROR_MESSAGE_NOT_A_NUMBER = "error: unable to delete, not a number\n";
	private static final String TEMP_FILE_NAME_TEMPLATE = "%s.tmp";
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_EXIT = "exit";

	static Scanner scanner = new Scanner(System.in);
	static PrintWriter pw;
	static BufferedReader br;
	static String fileName;
	static File file;

	public static void main(String[] args) {
		getFileName(args);
		initialiseFile();
		operateTextBuddy();

	}

	private static void getFileName(String[] args) {
		if (args.length == 0) {
			showToUser(MESSAGE_FILE_NAME_PROMPT);
			fileName = scanner.next();
		} else {
			fileName = args[0];
		}
	}

	private static void initialiseFile() {
		file = new File(fileName);
		if (!file.exists()) {
			createEmptyFile();
		}
		showToUser(MESSAGE_WELCOME, fileName);
	}

	private static void operateTextBuddy() {
		String command;
		boolean inOperation = true;

		while (inOperation) {
			showToUser(COMMAND_PROMPT);
			command = getCommand();
			inOperation = executeCommand(command);
		}
	}

	private static String getCommand() {
		return scanner.next();
	}

	private static boolean executeCommand(String command) {
		switch (command.toLowerCase()) {
			case COMMAND_ADD:
				addLineToFile();
				break;
			case COMMAND_DELETE:
				deleteLineFromFile();
				break;
			case COMMAND_DISPLAY:
				displayFileContent();
				break;
			case COMMAND_CLEAR:
				clearFileContent();
				break;
			case COMMAND_EXIT:
				return false;
			default:
				showToUser(ERROR_MESSAGE_INVALID_COMMAND);
			}
		return true;
	}

	// Command Methods

	private static void addLineToFile() {
		openPrintWriter(true);
		String line = scanner.nextLine().trim();
		printToFile(line);
		closePrintWriter();
		showToUser(MESSAGE_SUCCESSFULLY_ADDED, fileName, line);
	}

	private static void deleteLineFromFile() {
		int lineNumber;
		try {
			lineNumber = scanner.nextInt();

			String fileNameTemp = String.format(TEMP_FILE_NAME_TEMPLATE, fileName);
			boolean deleted = false;
			deleted = extractUndeletedLinesToTempFile(lineNumber, fileNameTemp);
			if (deleted) {
				overwriteOriginalFileWithTempFile(fileNameTemp);
			} else {
				showToUser(MESSAGE_UNSUCCESSFUL_DELETE);
			}
			File tempFile = new File(fileNameTemp);
			tempFile.delete();
		} catch (InputMismatchException e) {
			showToUser(ERROR_MESSAGE_NOT_A_NUMBER);
			scanner.nextLine();
			return;
		}

	}

	private static void displayFileContent() {
		openBufferedReader();
		readFileAndOutputFileContent();
		closeBufferedReader();

	}

	private static void clearFileContent() {
		createEmptyFile();
		showToUser(MESSAGE_SUCCESSFULLY_CLEARED, fileName);
	}

	// Helper Methods
	private static void overwriteOriginalFileWithTempFile(String fileNameTemp) {
		try {
			BufferedReader brt = new BufferedReader(
					new FileReader(fileNameTemp));
			openPrintWriter(false);
			String line;
			while ((line = brt.readLine()) != null) {
				printToFile(line);
			}
			brt.close();
			closePrintWriter();
		} catch (IOException e) {
			showToUser(ERROR_MESSAGE_FILE);
		}
	}

	private static boolean extractUndeletedLinesToTempFile(int lineNumber,
			String fileNameTemp) {
		try {
			String line;
			boolean deleted = false;
			PrintWriter pwt = new PrintWriter(new BufferedWriter(
					new FileWriter(fileNameTemp)));
			openBufferedReader();
			for (int i = 1; (line = readLineFromFile()) != null; i++) {
				if (i != lineNumber) {
					pwt.println(line);
				} else { // if(i == lineNumber)
					showToUser(MESSAGE_SUCESSFULLY_DELETED, fileName, line);
					deleted = true;
				}
			}
			pwt.close();
			closeBufferedReader();
			return deleted;
		} catch (IOException e) {
			showToUser(ERROR_MESSAGE_FILE);
			return false;
		}
	}

	private static void readFileAndOutputFileContent() { // Prereq: Buffered
														// reader must be open
		String line = readLineFromFile();
		int lineNumber = 1;
		boolean fileEmpty = (line == null);

		if (fileEmpty) {
			showToUser(MESSAGE_EMPTY_FILE, fileName);
		} else {
			showToUser(MESSAGE_OUTPUT, lineNumber, line);
			for (lineNumber = 2; (line = readLineFromFile()) != null; lineNumber++) {
				showToUser(MESSAGE_OUTPUT, lineNumber, line);
			}
		}
	}

	// Writer and Reader Methods
	private static void openPrintWriter(boolean forAppending) {
		try {
			if (forAppending) {
				pw = new PrintWriter(new FileWriter(file, true));
			} else {
				pw = new PrintWriter(new FileWriter(file));
			}
		} catch (IOException e) {
			showToUser(ERROR_MESSAGE_FILE);
		}
	}

	private static void closePrintWriter() {
		pw.close();

	}

	private static void printToFile(String line) {
		pw.println(line);
	}

	private static void openBufferedReader() {
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			showToUser(ERROR_MESSAGE_FILE);
		}
	}

	private static void closeBufferedReader() {
		try {
			br.close();
		} catch (IOException e) {
			showToUser(ERROR_MESSAGE_FILE);
		}
	}

	private static String readLineFromFile() {
		try {
			String line = br.readLine();
			return line;
		} catch (IOException e) {
			showToUser(ERROR_MESSAGE_FILE);
		}
		return null;
	}

	private static void createEmptyFile() {
		try {
			file.delete();
			file.createNewFile();
		} catch (IOException e) {
			showToUser(ERROR_MESSAGE_FILE);

		}
	}

	// Output Methods
	private static void showToUser(String message) {
		System.out.print(message);
	}

	private static void showToUser(String message, Object... args) {
		System.out.print(String.format(message, args));
	}

}
