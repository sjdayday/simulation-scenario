package org.grayleaves.utility;

public class InvalidPropertiesException extends Exception
{

	private static final long serialVersionUID = 1L;

	public InvalidPropertiesException()
	{
	}

	public InvalidPropertiesException(String message)
	{
		super(message);

	}

	public InvalidPropertiesException(Throwable cause)
	{
		super(cause);

	}

	public InvalidPropertiesException(String message, Throwable cause)
	{
		super(message, cause);

	}

}
