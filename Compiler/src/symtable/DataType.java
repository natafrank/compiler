package symtable;

/**
 * Class used to denote a data type as an string.
 * @author natafrank
 *
 */
public class DataType
{
	/**
	 * Indexes for dataTypes.
	 */
	public final static String UNDEFINED = "undefined";
	public final static String ENTERO = "E";
	public final static String DECIMAL = "D";
	public final static String LOGICO = "L";
	public final static String ALFANUMERICO = "A";
	
	private String dataType;
	
	public DataType()
	{
		dataType = UNDEFINED;
	}
	
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public String getDataType()
	{
		return dataType;
	}
}
