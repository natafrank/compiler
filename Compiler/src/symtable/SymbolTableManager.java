package symtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import core.Core;
import error.Error;

/**
 * Manages all the symbol table operations.
 * @author natafrank
 *
 */
public class SymbolTableManager
{
	private HashMap<String, SymbolTableElement> symbolTable;
	private Core core;
	private boolean error;
	
	/**
	 * Constructor.
	 */
	public SymbolTableManager(Core core)
	{
		this.core = core;
		symbolTable = new HashMap<String, SymbolTableElement>();
		error = false;
	}
	
	/**
	 * Adds a new element to the table.
	 * @param element
	 * @return true if succeded, false if not.
	 */
	public boolean addElement(SymbolTableElement element)
	{
		if(!isInTable(element))
		{
			symbolTable.put(element.getName(), element);
			return true;
		}
		error = true;
		return false;
	}
	
	/**
	 * Checks if an element is in the table. If it is then add the corresponding error.
	 * @param name
	 * @return true if element is in table, if not, returns false.
	 */
	private boolean isInTable(SymbolTableElement element)
	{
		if(!symbolTable.containsKey(element.getName())) return false;
		core.addSemanticError(Error.semanticEntryAlreadyDefined(element.getLine(), 
				element.getName()));
		return true;
	}
	
	/**
	 * Gets the String of the symbol table.
	 * @return String symbol table.
	 */
	public String getPrintableTable()
	{
		Collection<SymbolTableElement> c = symbolTable.values();
		String buffer = "\n---------------------------------------------------------------------"
				+ "--------------------------------------------\n";
		buffer += "|	 NAME 	|	CLASS	| 	TYPE 	| 	IS_DIM 	|	 DIM	 | 	VALUE 	|	 "
				+ "LINE 	|";
		buffer += "\n----------------------------------------------------------------------------"
				+ "-------------------------------------";	
		for(SymbolTableElement e : c)
		{
			buffer += "\n| " + e.getName() + "	| " + SymbolTableElement.getClassNameByIndex
					(e.getElementClass()) + "	|	"
					+ SymbolTableElement.getTypeNameByIndex(e.getType()) + "	|	"
					+ e.isDimensioned() + "	|	"; 
			ArrayList<Integer> dim = e.getDim();
			if(!dim.isEmpty())
			{
				for(Integer i : dim)
				{
					buffer += "[ " + i + " ] ";
				}
			}
			else buffer += "--";
			buffer += "	|	" + e.getValue() + " |	" + e.getLine() + "  |";
		}
		
		return buffer;
	}
	
	/**
	 * Gets the flag of error.
	 * @return error.
	 */
	public boolean hasError()
	{
		return error;
	}
}