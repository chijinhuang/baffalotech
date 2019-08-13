package com.baffalotech.integration.tcp;

public class FixedLengthField extends AbstractField {
	
	private String value;
	
	public FixedLengthField(String name,String value,int length,char fillChar,FillDirection fillDirection)
	{
		super(name, length,fillChar,fillDirection);
		this.value = value;
	}
	
	public FixedLengthField(String name,String value,int length) {
		super(name,length);
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}
	
	@Override
	public void parse(String text) {
		// TODO Auto-generated method stub
		switch (this.getFillDirection()) {
		case LEFT:{
			int i =0;
			StringBuilder sb = new StringBuilder();
			boolean flag = false;
			for(i=0;i<getLength();i++)
			{
				char c = text.charAt(i);
				if(c != getFillChar() && !flag)
				{
					flag = true;
				}
				if (flag) {
					sb.append(c);
				}
			}
			this.setValue(sb.toString());
			break;
		}
		case RIGHT:{
			int i =0;
			StringBuilder sb = new StringBuilder();
			boolean flag = false;
			for(i=getLength()-1;i>=0;i--)
			{
				char c = text.charAt(i);
				if(c != getFillChar() && !flag)
				{
					flag = true;
				}
				if (flag) {
					sb.insert(0, c);
				}
			}
			this.setValue(sb.toString());
			break;
		}
		default:
			break;
		}
	}
}
