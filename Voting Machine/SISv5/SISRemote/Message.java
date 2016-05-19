import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
	StringProperty name;
	StringProperty path;

	public Message(String name, String path) {
		setName(name);
		setPath(path);
	}

	public final void setName(String value) {
		nameProperty().set(value);
	}

	public final String getName() {
		return nameProperty().get();
	}

	public StringProperty nameProperty() {
		if (name == null) {
			name = new SimpleStringProperty();
		}
		return name;
	}

	public final void setPath(String value) {
		pathProperty().set(value);
	}

	public final String getPath() {
		return pathProperty().get();
	}

	public StringProperty pathProperty() {
		if (path == null) {
			path = new SimpleStringProperty();
		}
		return path;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = HashCodeUtil.hash(HashCodeUtil.SEED, getName());
		result = HashCodeUtil.hash(result, getPath());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Message) {
			Message n = (Message) obj;
			return n != null && n.getName().equals(getName())
					&& n.getPath().equals(getPath());
		} else {
			return false;
		}
	}

}
