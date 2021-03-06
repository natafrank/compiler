package core;

import java.awt.TextArea;
import java.util.ArrayList;

import codegen.CodeGenerator;
import error.Error;
import lexical.Scanner;
import parser.Parser;
import symtable.SymbolTableElement;
import symtable.SymbolTableManager;

/**
 * @author natafrank
 *
 * It's in charge of synchronization between all the elements in the compiler.
 */
public class Core
{
	private String fileName;
	private TextArea txtStatus;
	private ArrayList<String> parsingErrors;
	private ArrayList<String> semanticErrors;
	private ArrayList<String> codeGenErrors;
	private SymbolTableManager symTable;
	private boolean readyToGenerate;
	
	/**
	 * Constructor for terminal execution.
	 * @param fileName File to compile.
	 */
	public Core(String fileName)
	{
		this.fileName = fileName;
		txtStatus = null;
		parsingErrors = new ArrayList<String>();
		semanticErrors = new ArrayList<String>();
		codeGenErrors = new ArrayList<String>();
		symTable = new SymbolTableManager(this);
		readyToGenerate = true;
	}
	
	/**
	 * Constructor with GUI.
	 * @param txtStatus
	 * @param fileName
	 */
	public Core(TextArea txtStatus, String fileName)
	{
		this(fileName);
		this.txtStatus = txtStatus;
	}
	
	/**
	 * Inicializes the compiling process.
	 */
	public void compile()
	{
		Scanner scanner = new Scanner(fileName);
		if(scanner.getStatus() == Scanner.STATUS_GOOD)
		{
			CodeGenerator codeGen = new CodeGenerator(this);
			Parser parser = new Parser(this, scanner, codeGen);
			int parsingResult = parser.startParsing();
			print("/************** PARSER ****************/");
			switch(parsingResult)
			{
				case Parser.RESULT_OK:
					print("Parsing OK.");
					break;
				case Parser.RESULT_ERROR:
					int errorsLength = parsingErrors.size();
					for(int i = 0; i < errorsLength; i++)
					{
						print(parsingErrors.get(i));
					}
					readyToGenerate = false;
					break;
				default:
					print("Unexpected Result.");
			}
			print("/************** SEMANTIC ****************/");
			if(!symTable.hasError())
				print("Semantic OK.");				
			else
			{
				for(int i = 0; i < semanticErrors.size(); i++)
					print(semanticErrors.get(i));
				readyToGenerate = false;
			}
			print("/************** CODE GENERATION ****************/");
			if(!codeGen.hasError())
				print("Code generation OK.");
			else
			{
				int codeGenErrorsSize = codeGenErrors.size();
				for(int i = 0; i < codeGenErrorsSize; i++)
					print(codeGenErrors.get(i));
				readyToGenerate = false;
			}
			//print(symTable.getPrintableTable());
			if(readyToGenerate)
			{
				if(!codeGen.generateFile())
				{
					int codeGenErrorsSize = codeGenErrors.size();
					for(int i = 0; i < codeGenErrorsSize; i++)
						print(codeGenErrors.get(i));
				}
				else
					print("\n\nCOMPILATION COMPLETED WITH SUCCESS.\n\n");
			}
		}
		else
		{
			print(Error.LEXICAL_CREATION);
			readyToGenerate = false;
		}
	}
	
	/**
	 * Adds a new element to the symbol table.
	 * @param e new element.
	 * @return true if success, false if not.
	 */
	public boolean addElementToSymbolTable(SymbolTableElement e)
	{
		return symTable.addElement(e);
	}
	
	/**
	 * Gets an element from the symbol table by its name.
	 * @param name
	 * @return element
	 */
	public SymbolTableElement getElementByName(String name)
	{
		return symTable.getElementByName(name);
	}
	
	/**
	 * Adds  new parsing error.
	 * @param error 
	 */
	public void addParsingError(String error)
	{
		parsingErrors.add(error);
	}
	
	/**
	 * Adds  new semantic error.
	 * @param error 
	 */
	public void addSemanticError(String error)
	{
		semanticErrors.add(error);
		symTable.setError(true);
	}
	
	public void addCodeGenError(String error)
	{
		codeGenErrors.add(error);
	}
	
	/**
	 * Prints on the executing main.
	 * @param message
	 */
	public void print(String message)
	{
		if(txtStatus != null)
		{
			txtStatus.append("\n\n\n" + message);
		}
		else
		{
			System.out.print("\n\n\n" + message);
		}
	}
	
	/**
	 * Gets the file name.
	 * @return file name.
	 */
	public String getFileName()
	{
		return fileName;
	}
	
	/**
	 * Gets the symbol table ready to be used by the code generator.
	 * @return
	 */
	public ArrayList<String> getSymbolTableForCodeGenerator()
	{
		return symTable.getSymbolTableForCodeGenerator();
	}
}
