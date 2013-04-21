package org.grayleaves.utility;

import java.io.IOException;
import java.io.NotSerializableException;

public class NotSerializableClass
{
	 private void writeObject(java.io.ObjectOutputStream stream)
     throws IOException
     {
		 throw new NotSerializableException("can't be serialized"); 
     }
}
