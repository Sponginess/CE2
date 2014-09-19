import static org.junit.Assert.*;

import org.junit.Test;

import java.io.*;
import java.util.Scanner;
public class SearchTest {
	
	@Test
	public void testSearch() {
		TextBuddy.fileName = "tempTestFile.txt";
		try {
			TextBuddy.initialiseFile();

			PrintWriter pw = new PrintWriter(new FileWriter(TextBuddy.file));
			pw.println("This is a line");
			pw.println("More lines");
			pw.println("Another line");
			pw.println("Lines lines lines");
			pw.close();
			ByteArrayInputStream userInput = new ByteArrayInputStream("line".getBytes());
			TextBuddy.scanner = new Scanner(userInput);

			ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();
			System.setOut(new PrintStream(consoleOutput));
			TextBuddy.executeCommand("search");

			assertEquals("The following lines contain the keyword \"line\":\n"
					+ "1. This is a line\n"
					+ "3. Another line\n", consoleOutput.toString());
			TextBuddy.file.delete();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
