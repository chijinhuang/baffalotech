package com.baffalotech.integration.tcp;

public class LengthField extends AbstractField{
	
	private int value;

	public LengthField(String name,int value, int length, char fillChar, FillDirection fillDirection) {
		super(name, length, fillChar, fillDirection);
		// TODO Auto-generated constructor stub
		this.value = value;
	}
	
	public LengthField(String name,int value, int length) {
		super(name, length);
		// TODO Auto-generated constructor stub
		this.value = value;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value+"";
	}
	
	public int getRawValue()
	{
		return value;
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
			this.setValue(Integer.parseInt(sb.toString()));
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
			this.setValue(Integer.parseInt(sb.toString()));
			break;
		}
		default:
			break;
		}
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}
