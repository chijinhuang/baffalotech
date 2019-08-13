package com.baffalotech.integration.tcp;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TCPProtocal {

	private List<AbstractField> fieldList = new ArrayList<AbstractField>();
	
	public List<AbstractField> getFieldList() {
		return fieldList;
	}
	
	public void setFieldList(List<AbstractField> fieldList) {
		this.fieldList = fieldList;
	}
	
	public void addField(AbstractField field)
	{
		this.fieldList.add(field);
	}
	
	public void removeField(AbstractField field)
	{
		this.fieldList.remove(field);
	}
	
	public LengthField getLengthField()
	{
		for(AbstractField field :fieldList)
		{
			if(field instanceof LengthField)
			{
				return (LengthField)field;
			}
		}
		return null;
	}
	
	//获取header的长度
	public int getHeaderLength()
	{
		int headerLength = 0;
		for(AbstractField field : this.fieldList)
		{
			headerLength += field.getLength();
		}
		return headerLength;
	}
	
	public int getDataLength()
	{
		LengthField lengthField = getLengthField();
		if(lengthField != null)
		{
			return lengthField.getRawValue();
		}else {
			return 0;
		}
	}
	
	//解析，主要是从header里面取出值，然后塞到协议里面
	public void parse(String header)
	{
		int pos = 0;

		for(AbstractField field : this.fieldList)
		{
			String fieldText = header.substring(pos, pos+field.getLength());
			field.parse(fieldText);
			pos+=field.getLength();
		}
	}
}
