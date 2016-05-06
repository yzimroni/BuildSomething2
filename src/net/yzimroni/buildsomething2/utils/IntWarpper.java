package net.yzimroni.buildsomething2.utils;

public class IntWarpper {

	private int value;

	public IntWarpper() {
		value = 0;
	}

	public IntWarpper(int id) {
		value = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IntWarpper)) {
			return false;
		}
		IntWarpper other = (IntWarpper) obj;
		if (value != other.value) {
			return false;
		}
		return true;
	}
	
	

}
