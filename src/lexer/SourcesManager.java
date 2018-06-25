package lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import errors.EndOfFileException;
import interpretator.Interpretator;

public class SourcesManager {
	
	private Reader reader;
	private Integer numberLine;
	private Integer numberCharInLine;

	
	public void setFile(String path) throws FileNotFoundException, IOException 
	{
		numberLine = 1;
		numberCharInLine = 0;
		Charset encoding = Charset.defaultCharset();
		InputStream in = new FileInputStream(new File(path));
		reader = new InputStreamReader(in, encoding);
	}
	
	public char getNextChar() throws EndOfFileException
	{
		try {
			char ch;
			int r;

			do
			{
				r = reader.read();
				if(r == -1)
					throw new EndOfFileException();
				ch = (char) r;
			}while(ch == '\r');
				
			if(ch == '\n')
			{
				numberLine++;
				numberCharInLine = 0;
			}
			else
				numberCharInLine++;
			return (char) r;
		} catch (IOException e) {
			Interpretator.printLine("Blad IO", false);
			//e.printStackTrace();
		} 
		
		return 0;
	}
	
	public Integer getNumberLine() {
		return numberLine;
	}

	public Integer getNumberCharInLine() {
		return numberCharInLine;
	}
}
