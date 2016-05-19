import javafx.beans.property.SimpleStringProperty;

public class Field {
	private final SimpleStringProperty key;
	private final SimpleStringProperty value;

	public Field(String key, String value) {
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
	}

	public final void setKey(String val) {
		key.set(val);
	}

	public final String getKey() {
		return key.get();
	}

	public final void setValue(String val) {
		value.set(val);
	}

	public final String getValue() {
		return value.get();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = HashCodeUtil.hash(HashCodeUtil.SEED, getKey());
		result = HashCodeUtil.hash(result, getValue());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Message) {
			Message n = (Message) obj;
			return n != null && n.getName().equals(getKey())
					&& n.getPath().equals(getValue());
		} else {
			return false;
		}

	}
}