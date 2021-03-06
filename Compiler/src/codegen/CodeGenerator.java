package codegen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import core.Core;
import error.Error;

/**
 * This class is in charge of the code generation of the compiling file.
 * @author Frank
 *
 */
public class CodeGenerator
{
	private Core core;
	private BufferedWriter writer;
	private int instructionNumber;
	private int tagNumber;
	private ArrayList<String> instructions;
	private ArrayList<String> symbolTableIns;
	private ArrayList<String> tags;
	private boolean hasError;
	private String mainTag;
	
	//This array will hold instructions that need to be added after some progress.
	private ArrayList<String> instructionsBuffer;
	private boolean activeInstructionBuffer;
	private Stack<String> instructionsStackBuffer;
	private boolean activeStackBuffer;
	
	/**
	 * Constructor.
	 * @param core
	 */
	public CodeGenerator(Core core)
	{
		this.core = core;
		instructionNumber = 1;
		tagNumber = 1;
		instructions = new ArrayList<String>();
		instructionsBuffer = new ArrayList<String>();
		tags = new ArrayList<String>();
		activeInstructionBuffer = false;
		instructionsStackBuffer = new Stack<String>();
		activeStackBuffer = false;
		hasError = false;
		String fileName = core.getFileName();
		try
		{
			writer = new BufferedWriter(new FileWriter(fileName.substring(0, fileName
					.lastIndexOf(".")) + ".eje"));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			core.addCodeGenError(Error.codeGenFreeError("Error while creating bytecode file."));
			hasError = true;
		}
	}
	
	/**
	 * Adds a new instruction to the vector of instructions.
	 * @param mnemonic
	 * @param p1 parameter 1
	 * @param p2 parameter 2
	 */
	public void addInstruction(String mnemonic, String p1, String p2)
	{
		String instruction = mnemonic + " " + p1 + "," + p2;
		if(!activeInstructionBuffer)
			instructions.add(instructionNumber++ + " "  + instruction);
		else
		{
			if(activeStackBuffer)
				instructionsStackBuffer.push(instruction);
			else
				instructionsBuffer.add(instruction);
		}
	}
	
	/**
	 * Appends the instructions buffer to the main instructions set.
	 */
	public void addBufferToMainInstructionSet()
	{
		if(!activeStackBuffer)
		{
			for(String instruction : instructionsBuffer)
				instructions.add(instructionNumber++ + " " + instruction);
			instructionsBuffer.clear();
		}
		else
		{
			for(String instruction : instructionsStackBuffer)
				instructions.add(instructionNumber++ + " " + instruction);
			instructionsStackBuffer.clear();
		}
	}
	
	/**
	 * Generates the file ready to execute.
	 * @return
	 */
	public boolean generateFile()
	{	
		try
		{
			//Write main tag.
			writer.write(mainTag);
			
			//Write tags.
			int tagsSize = tags.size();
			for(int i = 0; i < tagsSize; i++)
				writer.write(tags.get(i) + "\n");
			
			//Write symbol table.
			symbolTableIns = core.getSymbolTableForCodeGenerator();
			int symTabSize = symbolTableIns.size();
			for(int i = 0; i < symTabSize; i++)
				writer.write(symbolTableIns.get(i) + "\n");
			
			//Write separator
			writer.write("@\n");
			
			//Write instructions.
			int instructionsSize = instructions.size();
			for(int i = 0; i < instructionsSize; i++)
				writer.write(instructions.get(i) + "\n");

			writer.close();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			core.addCodeGenError(Error.codeGenFreeError("Error while writing into the "
					+ "bytecode file."));
			hasError = true;
			return false;
		}
	}
	
	/**
	 * Gets the error status of th ecode generator.
	 * @return
	 */
	public boolean hasError()
	{
		return hasError;
	}
	
	public void setMainTag()
	{
		mainTag = "_P,I,I," + instructionNumber + ",0,#,\n";
	}
	
	public String getNextTag()
	{
		return "_E" + tagNumber++;
	}
	
	public void addTagToSymbolTable(String tagName, int line)
	{
		String tag = tagName + ",I,I," + line + ",0,#,";
		tags.add(tag);
	}
	
	public int getInstructionNumber()
	{
		return instructionNumber;
	}
	
	public void setActiveInstructionBuffer(boolean b)
	{
		activeInstructionBuffer = b;
	}
	
	public void setActiveStackBuffer(boolean b)
	{
		activeStackBuffer = b;
	}
}
