import static org.junit.Assert.*;
import java.io.*;

import org.junit.Test;


public class SortTest {
	//Checks if sort function works
	@Test
	public void testSortFunction() {
		TextBuddy.fileName = "tempTestFile.txt";

		try {
			TextBuddy.initialiseFile();
			PrintWriter pw = new PrintWriter(new FileWriter(TextBuddy.file));
			pw.println("This is a line");
			pw.println("More lines");
			pw.println("Another line");
			pw.println("Lines lines lines");
			pw.close();
			TextBuddy.executeCommand("sort");
			BufferedReader br = new BufferedReader(new FileReader(TextBuddy.file));
			String line;
			line = br.readLine();
			assertEquals("Another line", line);
			line = br.readLine();
			assertEquals("Lines lines lines", line);
			line = br.readLine();
			assertEquals("More lines", line);
			line = br.readLine();
			assertEquals("This is a line", line);
			line = br.readLine();
			assertEquals(null, line);
			br.close();
			TextBuddy.file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
