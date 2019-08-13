package com.baffalotech.integration.tcp;

public abstract class AbstractField {

	//字段名字
	private String name;
	
	//长度
	private int length;
	
	//填充字符
	private char fillChar;
	
	private FillDirection fillDirection;
	
	public AbstractField(String name,int length,char fillChar,FillDirection fillDirection)
	{
		this.name = name;
		this.length = length;
		this.fillChar = fillChar;
		this.fillDirection = fillDirection;
	}
	
	public AbstractField(String name,int length)
	{
		this(name, length, ' ', FillDirection.LEFT);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public char getFillChar() {
		return fillChar;
	}

	public void setFillChar(char fillChar) {
		this.fillChar = fillChar;
	}

	public FillDirection getFillDirection() {
		return fillDirection;
	}

	public void setFillDirection(FillDirection fillDirection) {
		this.fillDirection = fillDirection;
	}
	
	/**
	 * 获取要填充的字符串
	 * @return
	 */
	public abstract String getValue();
	
	/*
	 * 从text中解析值
	 */
	public abstract void parse(String text);
	
	public String toFieldText()
	{
		String toFilledString = getValue();
		StringBuilder sb = new StringBuilder();
		for(int i=toFilledString.length();i<length;i++)
		{
			switch (this.fillDirection) {
			case LEFT:
				sb.insert(0, this.fillChar);
				break;
			case RIGHT:
				sb.append(this.fillChar);
				break;
			default:
				//do nothing
				break;
			}
		}
		sb.append(getValue());
		return sb.toString();
	}
}
